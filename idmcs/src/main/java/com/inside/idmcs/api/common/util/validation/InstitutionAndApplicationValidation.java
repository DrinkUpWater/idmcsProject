package com.inside.idmcs.api.common.util.validation;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.util.parser.Parser;


@Component
public class InstitutionAndApplicationValidation extends Validation {

	public InstitutionAndApplicationValidation(Parser parser) {
		super(parser);
	}
	
	/**
	 * 기관 및 애플리케이션 정보의 유효성을 검사하는 메서드.
	 *
	 * 이 메서드는 주어진 기관 및 애플리케이션 정보와 요청 IP, 요청 URL을 기반으로 유효성을 검사합니다.
	 * 각 항목이 유효하지 않은 경우, 해당 오류 코드와 함께 CustomException을 발생시킵니다.
	 *
	 * @param instAndAppInfo 기관 및 애플리케이션 정보가 담긴 InstitutionAndApplicationInfo 객체
	 * @param requestIp 요청을 보낸 클라이언트의 IP 주소
	 * @param requestUrl 요청을 보낸 URL
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F104: 유효하지 않은 기관명 또는 기관 유효기간 만료
	 *                         - F105: 유효하지 않은 앱명 또는 앱 유효기간 만료
	 *                         - F106: 허용되지 않은 URL로의 접근
	 *                         - F107: 허용되지 않은 IP로의 접근
	 */
	public void isValid(InstitutionAndApplicationInfo instAndAppInfo, String requestIp,
			String requestUrl) throws CustomException {
		
		//기관명 검사
		if(isNullOrEmpty(instAndAppInfo.getInstNm()) || instAndAppInfo.getInstStatus().equals("N")) {
			throw new CustomException(ErrorCode.F104);
		} 
		
		//앱명 검사
		if(isNullOrEmpty(instAndAppInfo.getAppNm()) || instAndAppInfo.getAppStatus().equals("N")) {
			throw new CustomException(ErrorCode.F105);
		} 
		
		//기관 유효기간검사
		if(!checkExpired(instAndAppInfo.getInstVldBgngYmd(), instAndAppInfo.getInstVldEndYmd())) {
			throw new CustomException(ErrorCode.F104);
		}
		
		//앱 유효기간 검사
		if(!checkExpired(instAndAppInfo.getAppVldBgngYmd(), instAndAppInfo.getAppVldEndYmd())) {
			throw new CustomException(ErrorCode.F105);
		}
		
		//접속가능 ip검사
		if(!containsInJoinedValues(requestIp, instAndAppInfo.getIpList())) {
			throw new CustomException(ErrorCode.F107);
		}
		
		//접속가능 url검사
		if(!containsInJoinedValues(requestUrl, instAndAppInfo.getUrlList())) {
			throw new CustomException(ErrorCode.F106);
		}
		
		
	}
	
	/**
	 * 기관명이 유효한지 검사하는 메서드.
	 *
	 * 이 메서드는 기관명이 비어있거나(null 또는 empty) 유효하지 않은 경우,
	 * F102 오류 코드와 함께 CustomException을 발생시킵니다.
	 *
	 * @param instNm 검사할 기관명 문자열
	 * @throws CustomException 기관명이 비어있거나 유효하지 않은 경우 F102 예외를 발생시킴
	 */
	public void validateInstitutionName(String instNm) throws CustomException {
		if(isNullOrEmpty(instNm)) {
			throw new CustomException(ErrorCode.F102);
		} 
	}

	/**
	 * 주어진 값이 쉼표로 구분된 문자열 목록에 포함되어 있는지 확인하는 메서드.
	 * 
	 * 쉼표로 구분된 문자열(values)을 분리하여 리스트로 만든 후, 주어진 값(value)이 그 리스트에 포함되어 있는지 검사합니다.
	 * 
	 * @param value 검사할 값
	 * @param values 쉼표로 구분된 문자열 목록
	 * @return 주어진 값이 문자열 목록에 포함되어 있으면 true, 그렇지 않으면 false
	 */
	public boolean containsInJoinedValues(String value, String values) {
		
		if (isNullOrEmpty(values)) {
			return false;
        }
		
		// ","로 분리하고 양쪽 공백 제거
		List<String> ipList = Arrays.asList(values.split("\\s*,\\s*")); 
		if (ipList.contains(value)) {
			return true;
		}
		
		return false;
	};
	
}
