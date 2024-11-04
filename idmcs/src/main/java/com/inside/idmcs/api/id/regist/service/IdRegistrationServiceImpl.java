package com.inside.idmcs.api.id.regist.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inside.idmcs.api.ApiCallService;
import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.error.SuccessCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.req.IdRegistrationReqVO;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.common.util.crypto.IdRegistrationCrypto;
import com.inside.idmcs.api.common.util.logging.Logging;
import com.inside.idmcs.api.common.util.parser.Parser;
import com.inside.idmcs.api.common.util.validation.IdRegistrationValidation;
import com.inside.idmcs.api.enc.pk.dao.PublicKeyDao;
import com.inside.idmcs.api.id.check.service.IdCheckServiceImpl;
import com.inside.idmcs.api.id.regist.dao.IdRegistrationDao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IdRegistrationServiceImpl implements IdRegistrationService {

	private final PublicKeyDao publicKeyDao;
	private final IdRegistrationDao idRegistrationDao;
	private final Logging logging;
	private final Parser parser;
	private final IdRegistrationValidation validation;
	private final IdRegistrationCrypto crypto;
	private final ApiCallService apiCallService;

	public IdRegistrationServiceImpl(IdRegistrationDao idRegistrationDao, Logging logging, Parser parser,
			PublicKeyDao publicKeyDao, IdRegistrationValidation validation, ApiCallService apiCallService,
			IdRegistrationCrypto crypto, IdCheckServiceImpl idCheckService) {
		this.publicKeyDao = publicKeyDao;
		this.idRegistrationDao = idRegistrationDao;
		this.logging = logging;
		this.parser = parser;
		this.validation = validation;
		this.crypto = crypto;
		this.apiCallService = apiCallService;
	}
	
	/**
	 * 신분 등록 요청을 처리하고 응답 데이터를 반환하는 메서드.
	 *
	 * 이 메서드는 주어진 IdRegistrationReqVO 요청 객체와 HTTP 요청 정보를 사용하여 
	 * 신분 등록을 수행합니다. 과정 중 발생하는 각 단계에 대해 로그를 비동기적으로 저장하며, 
	 * 신분 정보가 유효하지 않은 경우 CustomException을 발생시킵니다. 성공 시 ResVO 객체를 반환합니다.
	 *
	 * @param reqVO 신분 등록을 위한 요청 데이터를 담고 있는 IdRegistrationReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return 신분 등록 결과를 포함한 ResVO 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F104: 유효하지 않은 기관 또는 앱 정보
	 *                         - F200: 이미 등록된 신분 정보
	 *                         - F801: 신분 정보 저장 또는 사진 저장 실패
	 *                         - F901: 예상치 못한 시스템 오류 발생
	 */
	@Transactional
	@Override
	public <T> ResVO<T> registIdRequest(@Valid IdRegistrationReqVO reqVO, HttpServletRequest request) throws CustomException {

		log.info("step1. start : registIdRequest");
		String logPk = "";
		try {
			log.info("step2. createLogPrimaryKey");
			// logPrimaryKey 값 가져오기 (현재시간(시분초밀리초) + 4자리 난수)
			logPk = logging.createLogPrimaryKey();
			
			log.info("step3. saveLogAsync ({})", reqVO);
			//로그 생성 비동기 : reqVO 정보
			logging.saveLogAsync(logPk, reqVO);
			
			log.info("step4. getRequestInfo ({})", request);
			//RequestInfo 생성(url, ip 등 request 정보)
			RequestInfo requestInfo = parser.getRequestInfo(request);
			
			log.info("step5. saveLogAsync ({})", requestInfo);
			// 로그 생성 비동기 : requestInfo 정보
			logging.saveLogAsync(logPk, requestInfo);
			
			log.info("step6. selectInstAndAppInfo (agencyToken: {}, appToken: {})", reqVO.getAgencyToken(), reqVO.getApplicationToken());
			// 기관앱정보 데이터베이서 select
			InstitutionAndApplicationInfo instAndAppInfo = publicKeyDao.selectInstAndAppInfo(
					reqVO.getAgencyToken(), reqVO.getApplicationToken());
			if(instAndAppInfo == null) {
				log.error("기관앱정보 조회 실패 agencyToken: {}, applicationToken: {}", reqVO.getAgencyToken(), reqVO.getApplicationToken());
			    throw new CustomException(ErrorCode.F104);
			}
			
			log.info("step7. isValid ({}, ip: {}, url: {})", instAndAppInfo, requestInfo.getReqIp(), requestInfo.getReqUrl());
			// 기관앱정보 유효성 검사
			validation.isValid(instAndAppInfo, requestInfo.getReqIp(), requestInfo.getReqUrl());
			
			log.info("step8. saveLogAsync ({})", instAndAppInfo);
			// 로그 갱신 기관앱정보 비동기
			logging.saveLogAsync(logPk, instAndAppInfo);
			
			log.info("step9. decryptReqVO ({})", reqVO);
			// 신분등록ReqVO 데이터 복호화
			ReqVO decryptReqVO = crypto.decryptReqVO(reqVO,	instAndAppInfo.getPrivateKey());
			
			log.info("step10. isValid ({})", decryptReqVO);
			//복호화된 IdCheckReqVO정보 유효성 검사
			validation.isValid(decryptReqVO);
	
			log.info("step11. saveLogAsync ({})", decryptReqVO);
			// 로그 갱신 reqVO 비동기
			logging.saveLogAsync(logPk, decryptReqVO);
			
			log.info("step12. checkRegistrationStatus (ci: {})", decryptReqVO.getCi());
			// 등록여부 확인
			String status = idRegistrationDao.checkRegistrationStatus(decryptReqVO.getCi());
			if (status != null && status.equals("Y")) 
				throw new CustomException(ErrorCode.F200);
			
			log.info("step13. apiGetIdInfo (ci: {})", decryptReqVO.getCi());
			//내부망에서 신분정보 불러오는 api 호출
			IdInfo idInfo = apiCallService.apiGetIdInfo(decryptReqVO.getCi());
			
			log.info("step14. saveLogAsync ({})", idInfo);
			// 로그 갱신 신분정보 비동기
			logging.saveLogAsync(logPk, idInfo);
			
			log.info("step15. isValid ({})", idInfo);
			//신분정보 유효성검사
			validation.isValid(idInfo);
			
			log.info("step16. checkUseIdInfo ({}, {}, instNm: {})", decryptReqVO, idInfo, instAndAppInfo.getInstNm());
			// ReqVO와 idInfo 정보 유효성검사
			validation.checkUseIdInfo(decryptReqVO, idInfo, instAndAppInfo.getInstNm());
			
			log.info("step17. saveIdInfo ({})", idInfo);
			//신분정보 직원데이터베이스에 인설트 
			if(idRegistrationDao.saveIdInfo(idInfo) == 0) 
				throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분등록 실패(saveIdInfo)"));
			
			log.info("step18. savePhoto ({})", idInfo);
			//사진정보 save
			if(idInfo.getPhoto() != null) 
				idRegistrationDao.savePhoto(idInfo);
			
			SuccessCode sCode = SuccessCode.S00000;
			
			log.info("step19. createResVO<> (sCode: {}, sMsg: {})", sCode.name(), sCode.getDescription());
			// resVO성공 객체 생성 ()
			ResVO<T> resVO = new ResVO<>(sCode.name(), sCode.getDescription());
			
			log.info("step20. saveLogAsync ({})", resVO);
			// 로그갱신 resVO 비동기
			logging.saveLogAsync(logPk, resVO.getResultCode(), resVO.getResultMessage(), parser.toJson(resVO));
			
			log.info("step21. End : registIdRequest");
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
