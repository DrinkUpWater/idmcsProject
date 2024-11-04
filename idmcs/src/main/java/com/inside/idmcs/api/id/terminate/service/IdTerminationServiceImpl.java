package com.inside.idmcs.api.id.terminate.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.error.SuccessCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.req.IdTerminationReqVO;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.common.util.crypto.IdTerminationCrypto;
import com.inside.idmcs.api.common.util.logging.Logging;
import com.inside.idmcs.api.common.util.parser.Parser;
import com.inside.idmcs.api.common.util.validation.IdTerminationValidation;
import com.inside.idmcs.api.enc.pk.dao.PublicKeyDao;
import com.inside.idmcs.api.id.check.dao.IdCheckDao;
import com.inside.idmcs.api.id.terminate.dao.IdTerminationDao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IdTerminationServiceImpl implements IdTerminationService {

	private final PublicKeyDao publicKeyDao;
	private final IdCheckDao idCheckDao;
	private final IdTerminationDao idTerminationDao;
	private final Logging logging;
	private final Parser parser;
	private final IdTerminationValidation validation;
	private final IdTerminationCrypto crypto;

	public IdTerminationServiceImpl(IdCheckDao idCheckDao, Logging logging, Parser parser,
			PublicKeyDao publicKeyDao, IdTerminationValidation validation,
			IdTerminationCrypto crypto, IdTerminationDao idTerminationDao) {
		this.idCheckDao = idCheckDao;
		this.logging = logging;
		this.parser = parser;
		this.publicKeyDao = publicKeyDao;
		this.validation = validation;
		this.crypto = crypto;
		this.idTerminationDao = idTerminationDao;
	}

	/**
	 * 신분 해지 요청을 처리하고 응답 데이터를 반환하는 메서드.
	 *
	 * 이 메서드는 주어진 IdTerminationReqVO 요청 객체와 HTTP 요청 정보를 사용하여 
	 * 신분 해지를 수행합니다. 각 처리 단계에서 로그를 비동기적으로 저장하며, 
	 * 해지에 실패하거나 데이터가 유효하지 않을 경우 CustomException을 발생시킵니다. 
	 * 성공 시 ResVO 객체를 반환합니다.
	 *
	 * @param reqVO 신분 해지를 위한 요청 데이터를 담고 있는 IdTerminationReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return 신분 해지 결과를 포함한 ResVO 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F104: 유효하지 않은 기관 또는 앱 정보
	 *                         - F201: 신분 정보가 존재하지 않음 (미가입 사용자)
	 *                         - F206: 이미 해지된 상태
	 *                         - F801: 신분 정보 또는 사진 정보 해지 실패
	 *                         - F901: 예상치 못한 시스템 오류 발생
	 */
	@Transactional
	@Override
	public <T> ResVO<T> terminateIdRequest(@Valid IdTerminationReqVO reqVO, HttpServletRequest request) throws CustomException {

		log.info("step1. start : terminateIdRequest");
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

			log.info("step12. selectIdInfo (ci: {})", decryptReqVO.getCi());
			// 직원정보 가져오기 
			IdInfo idInfo = idCheckDao.selectIdInfo(decryptReqVO.getCi());
			if (idInfo == null) //미가입 이용자
				throw new CustomException(ErrorCode.F201);
				
			log.info("step13. saveLogAsync ({})", idInfo);
			// 로그 갱신 신분정보 비동기
			logging.saveLogAsync(logPk, idInfo);
			
			log.info("step14. checkRegistYn (registYn: {})", idInfo.getRegistYn());
			// 해지가능 여부 확인
			if (idInfo.getRegistYn().equals("N"))
				throw new CustomException(ErrorCode.F201);

			log.info("step15. terminateId(직원 테이블 stts N처리) (ci {})", decryptReqVO.getCi());
			// 신분정보 해지 (직원 테이블 stts N처리)
			if(idTerminationDao.terminateId(decryptReqVO.getCi()) == 0) 
				throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분해지 실패(terminateId)"));
			
			// 사진정보 삭제 (n처리)
			idTerminationDao.terminatePhoto(idInfo.getUserId());
			
			SuccessCode sCode = SuccessCode.S00000;
			
			log.info("step16. createResVO<> (sCode: {}, sMsg: {})", sCode.name(), sCode.getDescription());
			// resVO성공 객체 생성 ()
			ResVO<T> resVO = new ResVO<>(sCode.name(), sCode.getDescription());

			log.info("step17. saveLogAsync ({})", resVO);
			// 로그갱신 resVO 비동기
			logging.saveLogAsync(logPk, resVO.getResultCode(), resVO.getResultMessage(), parser.toJson(resVO));
			
			log.info("step18. End : terminateIdRequest");
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
