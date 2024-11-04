package com.inside.idmcs.api.common.util.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.util.parser.Parser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QRCheckValidation extends QRValidation {

	public QRCheckValidation(Parser parser) {
		super(parser);
	}

	/**
	 * 주어진 ReqVO 객체의 유효성을 검사하는 메서드.
	 *
	 * 이 메서드는 ReqVO 객체의 CI와 encKey 필드의 길이를 검사합니다. 
	 * 각 필드의 길이가 유효하지 않은 경우 적절한 오류 코드와 함께 CustomException을 발생시킵니다.
	 *
	 * @param reqVO 유효성 검사를 수행할 ReqVO 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F103: 유효하지 않은 데이터 형식 또는 값
	 *                               - 고객 CI의 크기가 88이 아닌 경우
	 *                               - 서버 대칭키(encKey)의 크기가 33이 아닌 경우
	 */
	@Override
	public void isValid(ReqVO reqVO) throws CustomException {
		
		if(reqVO.getCi() != null && !checkSize(reqVO.getCi(), 88)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("고객 CI"));
		}
		
		if(!checkSize(reqVO.getEncKey(), 33)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("서버 대칭키"));
		}
		
	}
	
	/**
	 * QR 코드 발급 타임스탬프의 유효성을 검사하는 메서드.
	 *
	 * 이 메서드는 QR 코드 발급 타임스탬프가 주어진 시간(sec) 내에 유효한지 확인합니다.
	 * 타임스탬프가 만료된 경우 F404 오류 코드와 함께 CustomException을 발생시킵니다.
	 *
	 * @param qrMap QR 코드 정보가 담긴 맵 (발급 타임스탬프는 "issuedTimestamp" 키로 제공됨)
	 * @param sec 유효성 검사를 위한 시간(초) 제한
	 * @throws CustomException QR 코드가 만료된 경우 F404 예외를 발생시킴
	 * @throws Exception 기타 오류가 발생할 경우 발생하는 예외
	 */
	public void isValid(Map<String, String> qrMap, int sec) throws Exception {
		
		String qrIssuedTimestamp = qrMap.get("issuedTimestamp");
		
		if (!checkExpired(qrIssuedTimestamp, sec)) {
			throw new CustomException(ErrorCode.F404);
		}
	}
	
	/**
	 * 주어진 타임스탬프가 지정된 시간(초) 내에 유효한지 검사하는 메서드.
	 *
	 * 이 메서드는 발급 타임스탬프를 파싱하여 현재 시간과 비교합니다. 
	 * 주어진 시간(sec) 내에 유효하지 않으면 로그를 기록하고 false를 반환합니다.
	 *
	 * @param issuedTimestamp 발급 타임스탬프 (yyyyMMddHHmmss 형식의 문자열)
	 * @param sec 유효성을 검사할 제한 시간(초)
	 * @return 타임스탬프가 유효한 경우 true, 만료된 경우 false
	 * @throws Exception 형식 파싱 오류 또는 시간 계산 중 오류가 발생할 경우 예외를 던짐
	 */
	public boolean checkExpired(String issuedTimestamp, int sec) throws Exception {
	    // 주어진 타임스탬프의 형식 지정 (yyyyMMddHHmmss)
		DateTimeFormatter formatter = parser.StringToFormatDate(issuedTimestamp, "yyyyMMddHHmmss");
		
	    // issuedTimestamp를 LocalDateTime 객체로 변환
	    LocalDateTime issuedTime = LocalDateTime.parse(issuedTimestamp, formatter);

	    // 현재 시간 가져오기
	    LocalDateTime currentTime = LocalDateTime.now();

	    // issuedTime으로부터 sec초 후의 시간 계산
	    LocalDateTime expirationTime = issuedTime.plusSeconds(sec);

	    // 현재 시간이 expirationTime을 지났는지 확인
	    if (currentTime.isAfter(expirationTime)) {
	    	log.error("유효기간 만료({}초), 발급시간: {}, 검사시간: {}", sec, issuedTimestamp, currentTime);
	    	return false;
	    } else {
	    	return true;
	    }
	}
	
}
