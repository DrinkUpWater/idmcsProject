package com.inside.idmcs.api.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.common.util.logging.Logging;
import com.inside.idmcs.api.common.util.parser.Parser;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	private final Logging logging;
	private final Parser parser;

	public GlobalExceptionHandler(Logging logging, Parser parser) {
		this.logging = logging;
		this.parser = parser;
	}

	// JSON 형식 오류 처리
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public <T> ResponseEntity<ResVO<T>> handleJsonFormatException(HttpMessageNotReadableException ex,
			HttpServletRequest request) {

		String logPk = null;
		RequestInfo requestInfo = null;
		ErrorCode errorCode = ErrorCode.F101;

		try {
			// 로그 PK 생성
			logPk = logging.createLogPrimaryKey();
			// 로그 생성 (에러코드)
			logging.saveLogAsync(logPk, errorCode, errorCode.getDescription());

			// 요청 정보 추출
			requestInfo = parser.getRequestInfo(request);
			// 로그 갱신 (요청정보)
			logging.saveLogAsync(logPk, requestInfo);

			log.error("json형식 오류[{}]: {}", logPk, requestInfo);

		} catch (Exception e) {
			// 로그 생성 중 예외가 발생한 경우 예외 처리 및 오류 메시지 기록
			log.error("내부오류 로그 처리 중 예외 발생", e);

			// 로그 생성 실패 시, 기본 로그 처리 또는 추가 에러 처리
			if (logPk == null) {
				logPk = "FALL_CREATE_LOG_PK"; // 실패 시 기본 값 설정
				log.error("내부오류 로그 Pk 생성 실패", logPk);
			}

			if (requestInfo == null) {
				log.error("내부오류 RequestInfo 생성 실패", request);
			}
		}

		// JSON 형식 오류에 대한 F101 응답 반환
		return new ResponseEntity<>(new ResVO<>(errorCode.name(), errorCode.getDescription(), null), HttpStatus.OK);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public <T> ResponseEntity<ResVO<T>> handleValidationExceptions(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		String logPk = null;
		RequestInfo requestInfo = null;
		ErrorCode errorCode = ErrorCode.F103; // 기본적으로 형식 오류로 설정
		String message = "요청 데이터 오류";

		try {
			// 로그 PK 생성 및 요청 정보 추출
			logPk = logging.createLogPrimaryKey();
			requestInfo = parser.getRequestInfo(request);
		} catch (Exception e) {
			// 로그 생성 중 예외가 발생한 경우 예외 처리 및 오류 메시지 기록
			log.error("내부오류 로그 처리 중 예외 발생", e);

			// 로그 생성 실패 시, 기본 로그 처리 또는 추가 에러 처리
			if (logPk == null) {
				logPk = "FALL_CREATE_LOG_PK"; // 실패 시 기본 값 설정
				log.error("내부오류 로그 Pk 생성 실패", logPk);
			}

			if (requestInfo == null) {
				log.error("내부오류 RequestInfo 생성 실패", request);
			}
		}

		try {
			// 유효성 검사 오류 처리
			FieldError fieldError = ex.getBindingResult().getFieldError();
			if (fieldError != null) {
				message = fieldError.getDefaultMessage();

				// NotNull 오류일 경우 ErrorCode.F102 사용
				if (fieldError.getCode().equals("NotNull")) {
					errorCode = ErrorCode.F102; // 필수 값 누락
					message = errorCode.formatMessage(message);
				}
				// Size 오류일 경우 ErrorCode.F103 사용
				else if (fieldError.getCode().equals("Size")) {
					message = errorCode.formatMessage(message);
				}
			}
			
			// 로그 생성 (에러코드 및 메시지)
			logging.saveLogAsync(logPk, errorCode, message);

			// 로그 갱신 (요청 정보)
			logging.saveLogAsync(logPk, requestInfo);

		} catch (Exception e) {
			log.error("로그 처리 중 오류 발생", e);
		}

		// 최종 로그 기록
		log.error("데이터 오류 [{}]: {} : {}", logPk, message, requestInfo);

		return new ResponseEntity<>(new ResVO<>(errorCode.name(), message, null), HttpStatus.OK);

	}
}
