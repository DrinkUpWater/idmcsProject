package com.inside.idmcs.api.qr.create.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.error.SuccessCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.req.QRCreationReqVO;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.model.vo.res.QRCreationRes;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.common.util.crypto.QRCreationCrypto;
import com.inside.idmcs.api.common.util.logging.Logging;
import com.inside.idmcs.api.common.util.parser.Parser;
import com.inside.idmcs.api.common.util.qr.QR;
import com.inside.idmcs.api.common.util.validation.QRCreationValidation;
import com.inside.idmcs.api.enc.pk.dao.PublicKeyDao;
import com.inside.idmcs.api.id.check.dao.IdCheckDao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QRCreationServiceImpl implements QRCreationService {

	private final PublicKeyDao publicKeyDao;
	private final IdCheckDao idCheckDao;
	private final Logging logging;
	private final Parser parser;
	private final QRCreationValidation validation;
	private final QRCreationCrypto crypto;
	private final QR qr;

	public QRCreationServiceImpl(IdCheckDao idCheckDao,Logging logging, Parser parser,
			PublicKeyDao publicKeyDao, QRCreationValidation validation,
			QRCreationCrypto crypto, QR qr) {
		this.logging = logging;
		this.parser = parser;
		this.publicKeyDao = publicKeyDao;
		this.validation = validation;
		this.crypto = crypto;
		this.qr = qr;
		this.idCheckDao = idCheckDao;
	}

	/**
	 * QR 코드 생성 요청을 처리하고 응답 데이터를 반환하는 메서드.
	 *
	 * 이 메서드는 주어진 QRCreationReqVO 요청 객체와 HTTP 요청 정보를 사용하여 
	 * QR 코드를 생성합니다. 각 처리 단계에서 로그를 비동기적으로 저장하며, 
	 * 신분 정보가 유효하지 않거나 데이터베이스 작업이 실패할 경우 
	 * CustomException을 발생시킵니다. 성공 시 생성된 QR 코드를 포함하는 ResVO 객체를 반환합니다.
	 *
	 * @param reqVO QR 코드 생성을 위한 요청 데이터를 담고 있는 QRCreationReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return QR 코드 생성 결과를 포함한 ResVO 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F104: 유효하지 않은 기관 또는 앱 정보
	 *                         - F201: 신분 정보가 존재하지 않음 (미가입 사용자)
	 *                         - F901: 예상치 못한 시스템 오류 발생
	 */
	@Transactional
	@Override
	public <T> ResVO<T> createQRRequest(@Valid QRCreationReqVO reqVO, HttpServletRequest request) throws CustomException {
		
		log.info("step1. start : createQRRequest");
		String logPk = "";
		try {
			log.info("step2. createLogPrimaryKey");
			// logPrimaryKey 값 가져오기 (현재시간(시분초밀리초) + 4자리 난수)
			logPk = logging.createLogPrimaryKey();

			log.info("step3. saveLogAsync ({})", reqVO);
			// 로그 생성 비동기 : reqVO 정보
			logging.saveLogAsync(logPk, reqVO);

			log.info("step4. getRequestInfo ({})", request);
			//RequestInfo 생성(url, ip 등 request 정보)
			RequestInfo requestInfo = parser.getRequestInfo(request);

			log.info("step5. saveLogAsync ({})", requestInfo);
			// 로그 생성 비동기 : requestInfo 정보
			logging.saveLogAsync(logPk, requestInfo);

			log.info("step6. selectInstAndAppInfo (agencyToken: {}, appToken: {})", reqVO.getAgencyToken(),
					reqVO.getApplicationToken());
			// 기관앱정보 데이터베이서 select
			InstitutionAndApplicationInfo instAndAppInfo = publicKeyDao.selectInstAndAppInfo(reqVO.getAgencyToken(),
					reqVO.getApplicationToken());
			if (instAndAppInfo == null) 
				throw new CustomException(ErrorCode.F104);

			log.info("step7. isValid ({}, ip: {}, url: {})", instAndAppInfo, requestInfo.getReqIp(),
					requestInfo.getReqUrl());
			// 기관앱정보 유효성 검사
			validation.isValid(instAndAppInfo, requestInfo.getReqIp(), requestInfo.getReqUrl());

			log.info("step8. saveLogAsync ({})", instAndAppInfo);
			// 로그 갱신 기관앱정보 비동기
			logging.saveLogAsync(logPk, instAndAppInfo);

			log.info("step9. decryptReqVO ({})", reqVO);
			// 신분등록ReqVO 데이터 복호화
			ReqVO decryptReqVO = crypto.decryptReqVO(reqVO, instAndAppInfo.getPrivateKey());

			log.info("step10. isValid ({})", decryptReqVO);
			// 복호화된 IdCheckReqVO정보 유효성 검사
			validation.isValid(decryptReqVO);

			log.info("step11. saveLogAsync ({})", decryptReqVO);
			// 로그 갱신 reqVO 비동기
			logging.saveLogAsync(logPk, decryptReqVO);
			
			log.info("step12. selectIdInfo (ci: {})", decryptReqVO.getCi());
			// 직원정보 가져오기
			IdInfo idInfo = idCheckDao.selectIdInfo(decryptReqVO.getCi());
			if (idInfo == null) {
				throw new CustomException(ErrorCode.F201);
			}

			log.info("step13. saveLogAsync ({})", idInfo);
			// 로그 갱신 idInfo 비동기
			logging.saveLogAsync(logPk, idInfo);
			
			log.info("step14. checkUseIdInfo ({}, {}, instNm: {})", decryptReqVO, idInfo, instAndAppInfo.getInstNm());
			// Req와 idInfo 정보 유효성검사
			validation.checkUseIdInfo(decryptReqVO, idInfo, instAndAppInfo.getInstNm());

			log.info("step15. createQRCodeRequest (id: {})", idInfo.getUserId());
			// qr 생성
			String qrCode = qr.createQRCodeWithId(idInfo.getUserId(), 150);

			log.info("step16. saveLogAsync (qrCode: {})", qrCode);
			// qr정보 로그갱신
			logging.saveLogAsync(logPk, qrCode);
			
			log.info("step17. createQRCreationRes (qrCode: {})", qrCode);
			//QRCreationRes객체 생성 
			QRCreationRes res = new QRCreationRes(qrCode);
			
			SuccessCode sCode = SuccessCode.S00000;
			
			log.info("step18. createResVO<QRCreationRes> (sCode: {}, sMsg: {}, {})", sCode, sCode.getDescription(), res);
			// resVO성공 객체 생성 (idInfoRes)
			@SuppressWarnings("unchecked")
			ResVO<T> resVO = (ResVO<T>) new ResVO<QRCreationRes>(sCode.name(), sCode.getDescription(), res);
			
			log.info("step19. saveLogAsync ({})", resVO);
			// 로그갱신 resVO 비동기
			logging.saveLogAsync(logPk, resVO.getResultCode(), resVO.getResultMessage(), parser.toJson(resVO));

			log.info("step20. End : createQRRequest");
			return resVO;
			
		} catch (CustomException e) {
			log.error("에러 : {}", e.getMessage(), e);
			// 에러 로그갱신(에러정보)
			logging.saveLogAsync(logPk, e.getErrorCode().name(), e.getMessage(), null);

			throw e;
		} catch (Exception e) {
			log.error("기타 에러 : {}", e.getMessage(), e);
			ErrorCode errorCode = ErrorCode.F901;
			// 에러 로그갱신(에러정보)
			logging.saveLogAsync(logPk, errorCode.name(), errorCode.getDescription(), null);

			throw new CustomException(errorCode);
		}
	}

}
