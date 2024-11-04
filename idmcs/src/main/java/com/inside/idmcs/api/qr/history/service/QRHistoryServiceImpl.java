package com.inside.idmcs.api.qr.history.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inside.idmcs.api.ApiCallService;
import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.error.SuccessCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.PageInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.req.QRHistoryReqVO;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.model.vo.res.QRCheckHistory;
import com.inside.idmcs.api.common.model.vo.res.QRHistoryRes;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.common.util.crypto.QRHistoryCrypto;
import com.inside.idmcs.api.common.util.logging.Logging;
import com.inside.idmcs.api.common.util.parser.Parser;
import com.inside.idmcs.api.common.util.validation.QRHistoryValidation;
import com.inside.idmcs.api.enc.pk.dao.PublicKeyDao;
import com.inside.idmcs.api.id.check.dao.IdCheckDao;
import com.inside.idmcs.api.qr.history.dao.QRHistoryDao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QRHistoryServiceImpl implements QRHistoryService {
	
	private final PublicKeyDao publicKeyDao;
	private final QRHistoryDao qRHistoryDao;
	private final Logging logging;
	private final Parser parser;
	private final QRHistoryValidation validation;
	private final QRHistoryCrypto crypto;
	private final ApiCallService apiCallService;
	private final IdCheckDao idCheckDao;

	public QRHistoryServiceImpl(Logging logging, Parser parser, QRHistoryDao qRHistoryDao,
			PublicKeyDao publicKeyDao, QRHistoryValidation validation,
			QRHistoryCrypto crypto, ApiCallService apiCallService, IdCheckDao idCheckDao) {
		this.logging = logging;
		this.parser = parser;
		this.publicKeyDao = publicKeyDao;
		this.qRHistoryDao = qRHistoryDao;
		this.validation = validation;
		this.crypto = crypto;
		this.apiCallService = apiCallService;
		this.idCheckDao = idCheckDao;
	}
	
	
	/**
	 * QR 코드 이력 조회 요청을 처리하고 응답 데이터를 반환하는 메서드.
	 *
	 * 이 메서드는 주어진 QRHistoryReqVO 요청 객체와 HTTP 요청 정보를 사용하여 
	 * QR 코드 이력을 조회합니다. 각 단계에서 로그를 비동기적으로 저장하며, 
	 * 필요한 데이터가 유효하지 않거나 데이터베이스 작업이 실패할 경우 
	 * CustomException을 발생시킵니다. 성공 시 조회된 이력 정보를 포함하는 ResVO 객체를 반환합니다.
	 *
	 * @param reqVO QR 코드 이력 조회를 위한 요청 데이터를 담고 있는 QRHistoryReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return QR 코드 이력 조회 결과를 포함한 ResVO 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F104: 유효하지 않은 기관 또는 앱 정보
	 *                         - F201: 신분 정보가 존재하지 않음 (미가입 사용자)
	 *                         - F901: 예상치 못한 시스템 오류 발생
	 */
	@Transactional
	@Override
	public <T> ResVO<T> selectQRHistory(@Valid QRHistoryReqVO reqVO, HttpServletRequest request) throws CustomException  {
		
		log.info("step1. start : selectQRHistory");
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
			
			log.info("step17. checkUseIdInfo ({}, {}, instNm: {})", decryptReqVO, idInfo, instAndAppInfo.getInstNm());
			// Req와 idInfo 정보 유효성검사
			validation.checkUseIdInfo(decryptReqVO, idInfo, instAndAppInfo.getInstNm());
			
			log.info("step18. getCountHistoryQR ({}, appNo: {})", decryptReqVO, instAndAppInfo);
			// 조건에 맞는 QRHistory 갯수 가져오기
			int historyTotalCount =  qRHistoryDao.getCountHistoryQR(decryptReqVO, instAndAppInfo);
			
			log.info("step19. createPageInfo (curListIndex: {}, reqListCnt: {}, totalCnt: {})", decryptReqVO.getCurListIndex(), decryptReqVO.getReqListCnt(), historyTotalCount);
			//PageInfo 정보 생성
			PageInfo pageInfo = parser.createPageInfo(decryptReqVO.getCurListIndex(), decryptReqVO.getReqListCnt(), historyTotalCount);
			
			log.info("step20. selectQRHistoryList ({}, {})", decryptReqVO, pageInfo);
			//pageinfo로 데이터리스트 가져오기
			List<QRCheckHistory> qRHistoryList = qRHistoryDao.selectQRHistoryList(decryptReqVO, pageInfo, instAndAppInfo);
			
			log.info("step21. createQRHistoryRes ({}, {})", qRHistoryList, pageInfo);
			//QRHistoryRes객체 생성 
			QRHistoryRes res = new QRHistoryRes(qRHistoryList, pageInfo.getHasNext(), pageInfo.getTotalCount());
			
			SuccessCode sCode = SuccessCode.S00000;
			
			log.info("step22. createResVO<QRHistoryRes> ({})", res);
			// resVO성공 객체 생성 QRHistoryRes(공개키, 유효 시작일, 유효 만료일)
			@SuppressWarnings("unchecked")
			ResVO<T> resVO = (ResVO<T>) new ResVO<>(sCode.name(), sCode.getDescription(), res);

			log.info("step23. saveLogAsync ({})", resVO);
			// 로그갱신 resVO 비동기
			logging.saveLogAsync(logPk, sCode.name(), sCode.getDescription(), parser.toJson(resVO));
			
			log.info("step24. End : selectQRHistory");
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
