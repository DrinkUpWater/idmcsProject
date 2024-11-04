package com.inside.idmcs.api.enc.pk.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.error.SuccessCode;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.req.PublicKeyReqVO;
import com.inside.idmcs.api.common.model.vo.res.PublicKeyRes;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.common.util.logging.Logging;
import com.inside.idmcs.api.common.util.parser.Parser;
import com.inside.idmcs.api.common.util.validation.PublicKeyValidation;
import com.inside.idmcs.api.enc.pk.dao.PublicKeyDao;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PublicKeyServiceImpl implements PublicKeyService {

	private final PublicKeyDao publicKeyDao;
	private final Logging logging;
	private final PublicKeyValidation validation;
	private final Parser parser;

	public PublicKeyServiceImpl(PublicKeyDao publicKeyDao, Logging logging, Parser parser,
			PublicKeyValidation validation) {
		this.publicKeyDao = publicKeyDao;
		this.logging = logging;
		this.validation = validation;
		this.parser = parser;
	}
	
	/**
	 * 공개키 생성 요청을 처리하고 응답 데이터를 반환하는 메서드.
	 *
	 * 이 메서드는 요청 데이터(PublicKeyReqVO)와 HTTP 요청 정보를 사용하여 공개키를 생성합니다. 
	 * 과정 중 발생하는 각 단계에 대해 로그를 비동기적으로 저장하며, 오류 발생 시 적절한 
	 * CustomException을 던집니다. 성공 시 생성된 공개키와 유효기간 정보를 포함한 ResVO 객체를 반환합니다.
	 *
	 * @param reqVO 공개키 생성을 위한 요청 데이터를 담고 있는 PublicKeyReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return 공개키 응답 데이터를 포함한 ResVO 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F104: 기관 또는 애플리케이션 정보가 유효하지 않음
	 *                         - F801: 키 정보 업데이트 실패
	 *                         - F901: 예상하지 못한 시스템 오류 발생
	 */
	@Transactional
	@Override
	public <T> ResVO<T> generatePublicKeyRequest(PublicKeyReqVO reqVO, HttpServletRequest request) throws CustomException {

		log.info("step1. start : generatePublicKeyRequest");
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
			InstitutionAndApplicationInfo instAndAppInfo = publicKeyDao
					.selectInstAndAppInfo(reqVO.getAgencyToken(), reqVO.getApplicationToken());
			if(instAndAppInfo == null) {
			    throw new CustomException(ErrorCode.F104);
			}

			log.info("step7. isValid ({}, ip: {}, url: {})", instAndAppInfo, requestInfo.getReqIp(), requestInfo.getReqUrl());
			// 기관앱정보 유효성 검사
			validation.isValid(instAndAppInfo, requestInfo.getReqIp(), requestInfo.getReqUrl());

			log.info("step8. saveLogAsync ({})", instAndAppInfo);
			// 로그 갱신 기관앱정보 비동기 (키정보 X)
			logging.saveLogAsync(logPk, instAndAppInfo);

			log.info("step9. createKeyPair ({})", instAndAppInfo);
			//공개키 생성 후 기관앱 정보 객체에 set
			instAndAppInfo = createKeyPair(instAndAppInfo, 12);

			log.info("step10. saveLogAsync ({})", instAndAppInfo);
			// 로그 갱신 공개키정보 비동기 (키정보 O)
			logging.saveLogAsync(logPk, instAndAppInfo);

			log.info("step11. updateKeyPairInfoToApplication ({})", instAndAppInfo);
			// 키정보를 application테이블에 update
			if (publicKeyDao.updateKeyPairInfoToApplication(instAndAppInfo) == 0) {
				throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("키정보 업데이트 실패(updateKeyPairInfoToApplication)"));
			}
			
			log.info("step12. createPublicKeyRes (publicKey: {}, sDate: {}, eDate: {})", 
					instAndAppInfo.getPublicKey(), instAndAppInfo.getKeyVldBgngYmd(), instAndAppInfo.getKeyVldEndYmd());
			//PublicKeyRes객체 생성 
			PublicKeyRes res = new PublicKeyRes(instAndAppInfo.getPublicKey(), instAndAppInfo.getKeyVldBgngYmd(),
					instAndAppInfo.getKeyVldEndYmd());
			
			//성공 코드
			SuccessCode sCode = SuccessCode.S00000;
			
			log.info("step13. createResVO<PublicKeyRes> (sCode:{}, sMsg: {})", sCode, res);
			// resVO성공 객체 생성 PublicKeyRes(공개키, 유효 시작일, 유효 만료일)
			@SuppressWarnings("unchecked")
			ResVO<T> resVO = (ResVO<T>) new ResVO<PublicKeyRes>(sCode.name(), sCode.getDescription(), res);

			log.info("step14. saveLogAsync (sCode: {}, sMsg: {})", sCode, resVO);
			// 로그갱신 resVO 비동기
			logging.saveLogAsync(logPk, sCode.name(), sCode.getDescription(), parser.toJson(resVO));
			
			log.info("step15. End : generatePublicKeyRequest");
			return resVO;

		} catch (CustomException e) {
			log.error("에러 : {}", e.getMessage(), e);
			// 에러 로그갱신(에러정보)
			logging.saveLogAsync(logPk, e.getErrorCode().name(), e.getErrorCode().getDescription(),
					null);
			throw e;
		} catch (Exception e) {
			log.error("기타 에러 : {}", e.getMessage(), e);
			ErrorCode errorCode = ErrorCode.F901;
			// 에러 로그갱신(에러정보)
			logging.saveLogAsync(logPk, errorCode.name(), errorCode.getDescription(), null);
			
			throw new CustomException(errorCode);
		}
	}

	/**
	 * 기관 및 애플리케이션 정보를 기반으로 RSA 키 페어를 생성하고, 지정된 유효 기간(month)과 함께 KeyPairInfo 객체로
	 * 반환하는 메서드.
	 * 
	 * 이 메서드는 SecureRandom을 사용하여 RSA 키 페어(공개키 및 개인키)를 생성하고, 생성된 키를 Base64 형식으로
	 * 변환합니다. 또한, 현재 날짜를 기준으로 지정된 월 수(month) 동안의 유효 기간을 설정하여 키 페어 정보와 함께 반환합니다.
	 * 
	 * @param instAndAppInfo 기관 및 애플리케이션 정보 (키 생성에 필요한 고유성 제공을 위한 정보)
	 * @param month          유효 기간을 설정할 개월 수
	 * @return RSA 공개키, 개인키, 시작일, 종료일을 포함한 KeyPairInfo 객체
	 * @throws CustomException RSA 키 생성 중 오류가 발생할 경우 발생 (F502 에러 코드)
	 */
	private InstitutionAndApplicationInfo createKeyPair(InstitutionAndApplicationInfo instAndAppInfo, int month)
			throws CustomException {

		try {
			// 무작위 secureRandom 생성
			SecureRandom secureRandom = new SecureRandom();

			// RSA 알고리즘을 위한 KeyPairGenerator 객체 생성 및 초기화 (키 크기: 2048비트)
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048, secureRandom);

			// RSA 키 페어 생성 (공개키, 개인키)
			KeyPair keyPair = keyPairGenerator.genKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();

			// 공개키와 개인키를 Base64 형식으로 인코딩하여 문자열로 변환
			String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());

			// 현재날짜부터 1년뒤까지 유효기간 설정
			LocalDate validStart = LocalDate.now();
			LocalDate validEnd = validStart.plusMonths(month);
			
			// 기관앱 객체에 키정보 set
			instAndAppInfo.setPublicKey(stringPublicKey);
			instAndAppInfo.setPrivateKey(stringPrivateKey);
			instAndAppInfo.setKeyVldBgngYmd(parser.formatDateString(validStart, "yyyyMMdd"));
			instAndAppInfo.setKeyVldEndYmd(parser.formatDateString(validEnd, "yyyyMMdd"));
			
			return instAndAppInfo;

		} catch (Exception e) {
			log.error("공개키 생성 실패 : {}", instAndAppInfo);
			throw new CustomException(ErrorCode.F502);
		}

	}

}
