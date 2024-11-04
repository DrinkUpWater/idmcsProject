package com.inside.idmcs.api.common.util.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.util.logging.dao.LoggingDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Logging {

	private final LoggingDao loggingDao;

	public Logging(LoggingDao loggingDao) {
		this.loggingDao = loggingDao;
	}

	/**
	 * 로그의 기본 키를 생성하는 메서드.
	 * 
	 * 이 메서드는 현재 시각(시, 분, 초, 밀리초)과 4자리 난수를 조합하여 고유한 로그 기본 키를 생성합니다.
	 * 
	 * @return 생성된 로그 기본 키 (형식: HHmmssSSS + 4자리 난수)
	 */
	public String createLogPrimaryKey() {
		try {
			// 현재 시간 가져오기
			LocalDateTime now = LocalDateTime.now();

			// 시분초밀리초 포맷 설정
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

			// 포맷팅된 현재시간
			String formattedNow = now.format(formatter);

			// 4자리 난수
			int randomNumber = (int) (Math.random() * 10000);

			// logPrimaryKey 값
			String logPk = String.format("%s%04d", formattedNow, randomNumber);

			return logPk;

		} catch (Exception e) {
			log.error("logPk 생성 실패", e);
			return "FALL_CREATE_LOG_PK";
		}
	}

	/**
	 * 비동기로 로그를 업데이트하는 메서드입니다. 주어진 결과 코드, 결과 메시지, JSON 응답 데이터를 이용해 로그를 갱신합니다. 로그 갱신
	 * 중 오류가 발생하면 예외를 처리하고 에러 로그를 남깁니다.
	 *
	 * @param logPk         로그의 고유 식별자 (기본 키)
	 * @param resultCode    처리 결과 코드
	 * @param resultMessage 처리 결과 메시지
	 * @param jsonRes       처리 결과를 포함한 JSON 형식의 응답 데이터
	 */
	@Async
	public void saveLogAsync(String logPk, String resultCode, String resultMessage, String jsonRes) {
		try {
			if (loggingDao.saveLogWithRes(logPk, resultCode, resultMessage, jsonRes) == 0)
				log.error("업뎃실패(결과)");
		} catch (Exception e) {
			log.error("로그갱신(결과) 실패: {}, 결과Res{}, 오류: {}" + logPk, jsonRes, e.getMessage(), e);
		}
	}

	/**
	 * 비동기적으로 로그를 갱신하는 메서드.
	 * 
	 * 이 메서드는 주어진 로그 기본 키와 에러 코드를 사용하여 로그를 갱신하며, 갱신 작업이 성공 또는 실패할 경우 각각의 메시지를 출력합니다.
	 * 로그 갱신 작업은 비동기적으로 수행됩니다.
	 * 
	 * @param logPk          로그 기본 키 (고유 식별자)
	 * @param instAndAppInfo 로그 갱신 시 사용할 기관앱정보
	 */
	@Async
	public void saveLogAsync(String logPk, InstitutionAndApplicationInfo instAndAppInfo) {
		
		try {
			Thread.sleep(8);
			if (loggingDao.saveLogWithInstAndAppInfo(logPk, instAndAppInfo) == 0)
				log.error("업뎃실패(기관앱정보)");
		} catch (Exception e) {
			log.error("로그갱신(기관앱정보) 실패 ({}): {}, 오류: {}", logPk, instAndAppInfo, e.getMessage(), e);
		}
	}
	
	/**
	 * 비동기적으로 신분 정보(IdInfo)를 포함한 로그를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 IdInfo 객체를 사용하여 비동기적으로 로그를 저장합니다.
	 * 로그 저장이 실패한 경우 오류를 기록합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param idInfo 로그에 저장할 IdInfo 객체
	 */
	@Async
	public void saveLogAsync(String logPk, IdInfo idInfo) {

		try {
			if (loggingDao.saveLogWithIdInfo(logPk, idInfo) == 0)
				log.error("업뎃실패(신분정보)");
		} catch (Exception e) {
			log.error("로그갱신(신분정보) 실패: ({}): {}, 오류: {}", logPk, idInfo, e.getMessage(), e);
		}

	}
	
	/**
	 * 비동기적으로 오류 정보를 포함한 로그를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 오류 코드, 오류 메시지를 사용하여 비동기적으로 로그를 저장합니다.
	 * 로그 저장이 실패한 경우 오류를 기록합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param errorCode 저장할 오류 코드
	 * @param errorMsg 저장할 오류 메시지
	 */
	@Async
	public void saveLogAsync(String logPk, ErrorCode errorCode, String errorMsg) {
		try {
			if (loggingDao.saveLogWithError(logPk, errorCode.name(), errorMsg) == 0)
				log.error("업뎃실패(파라미터)");
		} catch (Exception e) {
			log.error("로그생성(파라미터 에러) 실패({}) : {}, 오류: {}", logPk, errorCode, e.getMessage(), e);
		}

	}
	
	/**
	 * 비동기적으로 요청 정보(RequestInfo)를 포함한 로그를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 RequestInfo 객체를 사용하여 비동기적으로 로그를 저장합니다.
	 * 로그 저장이 실패한 경우 오류를 기록합니다. 로그 저장 전에 8밀리초 동안 대기합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param requestInfo 로그에 저장할 RequestInfo 객체
	 */
	@Async
	public void saveLogAsync(String logPk, RequestInfo requestInfo) {
		try {
			Thread.sleep(8);
			if (loggingDao.saveLogWithRequestInfo(logPk, requestInfo) == 0)
				log.error("업뎃실패(요청정보)");
		} catch (Exception e) {
			log.error("로그갱신(request정보) 실패[{}] : {} 오류: {}", logPk, requestInfo, e.getMessage(), e);
		}

	}

	/**
	 * 비동기적으로 ReqVO 객체를 포함한 로그를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 ReqVO 객체를 사용하여 비동기적으로 로그를 저장합니다.
	 * 로그 저장이 실패한 경우 오류를 기록합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param reqVO 로그에 저장할 ReqVO 객체
	 */
	@Async
	public void saveLogAsync(String logPk, ReqVO reqVO) {
		try {
			if (loggingDao.saveLogWithReqVO(logPk, reqVO) == 0)
				log.error("생성실패(reqVO)");
		} catch (Exception e) {
			log.error("로그생성 실패: logPk = {}, reqVO = {}, 오류: {}", logPk, reqVO, e.getMessage(), e);
		}

	}
	
	/**
	 * 비동기적으로 QR 코드를 포함한 로그를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 QR 코드를 사용하여 비동기적으로 로그를 저장합니다.
	 * 로그 저장이 실패한 경우 오류를 기록합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param qrCode 로그에 저장할 QR 코드 문자열
	 */
	@Async
	public void saveLogAsync(String logPk, String qrCode) {
		try {
			if (loggingDao.saveLogWithQRCode(logPk, qrCode) == 0)
				log.error("업뎃실패(qr코드)");
		} catch (Exception e) {
			log.error("로그생성 실패: logPk = {}, reqVO = {}, 오류: {}", logPk, qrCode, e.getMessage(), e);
		}
	}

}
