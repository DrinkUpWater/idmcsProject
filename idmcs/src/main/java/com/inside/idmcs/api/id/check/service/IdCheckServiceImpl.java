package com.inside.idmcs.api.id.check.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inside.idmcs.api.ApiCallService;
import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.error.SuccessCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.req.IdCheckReqVO;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.model.vo.res.IdInfoRes;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.common.util.crypto.IdCheckCrypto;
import com.inside.idmcs.api.common.util.logging.Logging;
import com.inside.idmcs.api.common.util.parser.Parser;
import com.inside.idmcs.api.common.util.qr.QR;
import com.inside.idmcs.api.common.util.validation.IdCheckValidation;
import com.inside.idmcs.api.enc.pk.dao.PublicKeyDao;
import com.inside.idmcs.api.id.check.dao.IdCheckDao;
import com.inside.idmcs.api.id.regist.dao.IdRegistrationDao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IdCheckServiceImpl implements IdCheckService {

	private final PublicKeyDao publicKeyDao;
	private final IdCheckDao idCheckDao;
	private final Logging logging;
	private final Parser parser;
	private final IdCheckValidation validation;
	private final IdCheckCrypto crypto;
	private final QR qr;
	private final IdRegistrationDao idRegistrationDao;
	private final ApiCallService apiCallService;
	

	public IdCheckServiceImpl(IdCheckDao idCheckDao, Logging logging, Parser parser, PublicKeyDao publicKeyDao, ApiCallService apiCallService,
			IdCheckValidation validation, IdCheckCrypto crypto, QR qr, IdRegistrationDao idRegistrationDao) {
		this.publicKeyDao = publicKeyDao;
		this.idCheckDao = idCheckDao;
		this.logging = logging;
		this.parser = parser;
		this.validation = validation;
		this.crypto = crypto;
		this.qr = qr;
		this.idRegistrationDao = idRegistrationDao;
		this.apiCallService = apiCallService;
	}
	
	/**
	 * 신분 확인 요청을 처리하고 응답 데이터를 반환하는 메서드.
	 *
	 * 이 메서드는 주어진 IdCheckReqVO 요청 객체와 HTTP 요청 정보를 사용하여 
	 * 신분 확인을 수행하며, 각 처리 단계에서 로그를 비동기적으로 저장합니다. 
	 * 신분 정보가 유효하지 않은 경우 CustomException을 발생시킵니다. 
	 * 성공 시 생성된 IdInfoRes 응답 객체를 포함하는 ResVO 객체를 반환합니다.
	 *
	 * @param reqVO 신분 확인을 위한 요청 데이터를 담고 있는 IdCheckReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return 신분 확인 결과를 포함한 ResVO 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F104: 유효하지 않은 기관 또는 앱 정보
	 *                         - F201: 조회된 신분 정보가 존재하지 않음
	 *                         - F206: 신분 정보 조회 권한이 없음
	 *                         - F801: 신분 정보 업데이트 또는 응답 객체 생성 실패
	 *                         - F901: 예상치 못한 시스템 오류 발생
	 */
	@Transactional
	@Override
	public <T> ResVO<T> checkIdRequest(@Valid IdCheckReqVO reqVO, HttpServletRequest request) throws CustomException {

		log.info("step1. start : checkIdRequest");
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
			if (instAndAppInfo == null) {
				log.error("기관앱정보 조회 실패 agencyToken: {}, applicationToken: {}", reqVO.getAgencyToken(),
						reqVO.getApplicationToken());
				throw new CustomException(ErrorCode.F104);
			}

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
			IdInfo idInfoDb = idCheckDao.selectIdInfo(decryptReqVO.getCi());
			if (idInfoDb == null) 
				throw new CustomException(ErrorCode.F201); 
			
			log.info("step13. saveLogAsync ({})", idInfoDb);
			// 로그 갱신 신분정보 비동기
			logging.saveLogAsync(logPk, idInfoDb);
			
			log.info("step14. checkRegistYn (registYn: {})", idInfoDb.getRegistYn());
			// 조회가능 여부 확인
			if(idInfoDb.getRegistYn().equals("N"))
				throw new CustomException(ErrorCode.F206);
			
			log.info("step15. apiGetIdInfo (ci: {})", decryptReqVO.getCi());
			//내부망에서 신분정보 불러오는 api 호출
			IdInfo idInfo = apiCallService.apiGetIdInfo(decryptReqVO.getCi());
			
			log.info("step16. saveLogAsync ({})", idInfo);
			// 로그 갱신 idInfo 비동기
			logging.saveLogAsync(logPk, idInfo);
			
			log.info("step17. isValid ({})", idInfo);
			//유효성검사 추가
			validation.isValid(idInfo);
			
			log.info("step18. updateIdInfo ({})", idInfo);
			//신분정보 직원데이터베이스에 업데이트 
			if(idCheckDao.updateIdInfo(idInfo) == 0) 
				throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보 업데이트(updateIdInfo)"));
			
			log.info("step19. savePhoto ({})", idInfo);
			//사진정보 save
			if(idInfo.getPhoto() != null) 
				idRegistrationDao.savePhoto(idInfo);

			log.info("step20. checkUseIdInfo ({}, {}, instNm: {})", decryptReqVO, idInfo, instAndAppInfo.getInstNm());
			// Req와 idInfo 정보 유효성검사
			validation.checkUseIdInfo(decryptReqVO, idInfo, instAndAppInfo.getInstNm());

			log.info("step21. createQRCodeWithId (id: {})", idInfo.getUserId());
			// qr 생성
			String qrCode = qr.createQRCodeWithId(idInfo.getUserId(), 150);

			log.info("step22. saveLogAsync (qrCode: {})", qrCode);
			// qr정보 로그갱신
			logging.saveLogAsync(logPk, qrCode);

			log.info("step23. encryptIdInfo ({}, appKey: {})", idInfo, idInfo.getAppKey());
			// idInfo 중 resVO로 전달해야하는 데이터 id정보는 appkey로 암호화
			idInfo = crypto.encryptAESInfo(idInfo, idInfo.getAppKey());

			log.info("step24. createIdInfoRes (qrCode: {}, {})", qrCode, idInfo);
			// IdInfoRes객체 생성
			IdInfoRes res = createIdInfoRes(qrCode, idInfo);

			SuccessCode sCode = SuccessCode.S00000;

			log.info("step25. createResVO<idInfoRes> (sCode: {}, sMsg: {}, {})", sCode, sCode.getDescription(), res);
			// resVO성공 객체 생성 (idInfoRes)
			@SuppressWarnings("unchecked")
			ResVO<T> resVO = (ResVO<T>) new ResVO<IdInfoRes>(sCode.name(), sCode.getDescription(), res);

			log.info("step26. saveLogAsync ({})", resVO);
			// 로그갱신 resVO 비동기
			logging.saveLogAsync(logPk, resVO.getResultCode(), resVO.getResultMessage(), parser.toJson(resVO));

			log.info("step27. End : checkIdRequest");
			return resVO; 

		} catch (CustomException e) {
			log.error("에러 : {}", e.getMessage(), e);
			// 에러 로그갱신(에러정보)
			logging.saveLogAsync(logPk, e.getErrorCode().name(), e.getMessage(), null);

			throw e;
		} //sql exception 추가
		catch (Exception e) {
			log.error("기타 에러 : {}", e.getMessage(), e);
			ErrorCode errorCode = ErrorCode.F901;
			// 에러 로그갱신(에러정보)
			logging.saveLogAsync(logPk, errorCode.name(), errorCode.getDescription(), null);

			throw new CustomException(errorCode);
		}
	}

	/**
	 * QR 코드와 신분 정보를 조합하여 IdInfoRes 객체를 생성하는 메서드.
	 *
	 * @param qrCode 생성된 QR 코드 문자열
	 * @param idInfo 신분 정보가 담긴 IdInfo 객체
	 * @return 생성된 IdInfoRes 객체
	 * @throws CustomException 신분 정보 응답 객체 생성 실패 시 F801 예외를 발생시킴
	 */
	private IdInfoRes createIdInfoRes(String qrCode, IdInfo idInfo) throws Exception {

		try {
			IdInfoRes idInfoRes = new IdInfoRes();

			idInfoRes.setQrCd(qrCode);
			idInfoRes.setBirthDay(idInfo.getBirthDay());
			idInfoRes.setSubCode(idInfo.getSubCode());
			idInfoRes.setUserName(idInfo.getUserName());
			idInfoRes.setIssuedYmd(idInfo.getIssuedYmd());
			idInfoRes.setAddress(idInfo.getAddress());
			idInfoRes.setDetailAddress(idInfo.getDetailAddress());
			idInfoRes.setPhoto(idInfo.getPhoto());
			idInfoRes.setIssuOrgName(idInfo.getIssuedInstNm());

			return idInfoRes;
		} catch (Exception e) {
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보조회Res 생성 실패"));
		}

	}

}
