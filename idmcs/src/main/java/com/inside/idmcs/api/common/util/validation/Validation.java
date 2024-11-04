package com.inside.idmcs.api.common.util.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class Validation {
	
	protected final Parser parser;
	
	public Validation(Parser parser) {
		this.parser = parser;
	}
	
	/**
	 * 객체가 null 또는 비어 있는지 확인하는 메서드.
	 * 
	 * 이 메서드는 객체가 null이거나, 문자열의 경우 빈 문자열인지, 컬렉션이나 맵의 경우 요소가 없는지 확인합니다.
	 * 다른 타입의 경우에는 null 여부만 확인합니다.
	 * 
	 * @param value 검사할 객체 (String, Collection, Map, 기타 객체)
	 * @return 객체가 null이거나 비어 있으면 true, 그렇지 않으면 false
	 */
	public boolean isNullOrEmpty(Object value) {
        if (value == null) {
            return true;
        }

        if (value instanceof String) {
            return ((String) value).trim().isEmpty();
        }

        if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        }

        if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }

        // 다른 타입의 경우에는 null만 체크
        return false;
    }
	
	/**
	 * 문자열의 길이가 주어진 최대 크기 이하인지 확인하는 메서드.
	 * 
	 * 이 메서드는 주어진 문자열의 길이가 지정된 크기보다 작거나 같은지 검사합니다.
	 * 
	 * @param value 검사할 문자열
	 * @param size 최대 허용 크기
	 * @return 문자열의 길이가 size 이하이면 true, 그렇지 않으면 false
	 */
	public boolean checkSize(String value, int size) {
		return value.length() <= size;
	}

	/**
	 * 주어진 문자열이 YYYYMMDD 형식의 날짜인지 확인하는 메서드.
	 * 
	 * 주어진 문자열을 'yyyyMMdd' 형식으로 파싱하여 유효성을 검사합니다.
	 * 
	 * @param date 검사할 날짜 문자열
	 * @return 유효한 'yyyyMMdd' 형식이면 true, 그렇지 않으면 false
	 */
	public boolean checkDateFormat(String date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            LocalDate.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

	/**
	 * 주어진 시작일과 만료일을 비교하여 날짜가 만료되었는지 확인하는 메서드.
	 * 
	 * 날짜 형식이 유효한지 먼저 검사한 후, 오늘 날짜가 시작일과 만료일 사이에 있는지 확인합니다.
	 * 
	 * @param startDate 시작일 (YYYYMMDD 형식)
	 * @param expiryDate 만료일 (YYYYMMDD 형식)
	 * @return 날짜가 유효하며, 오늘 날짜가 시작일과 만료일 사이에 있으면 true, 그렇지 않으면 false
	 */
    public boolean checkExpired(String startDate, String expiryDate) {
        // 먼저 날짜 형식이 유효한지 확인
        if (!checkDateFormat(startDate, "yyyyMMdd") || !checkDateFormat(expiryDate, "yyyyMMdd")) {
            return false;
        }

        LocalDate start = parser.parseStringToLocalDate(startDate);
        LocalDate expire = parser.parseStringToLocalDate(expiryDate);
        LocalDate today = LocalDate.now();
        
        return !today.isBefore(start) && !today.isAfter(expire);
    }

    /**
     * 문자열이 주어진 최소값 이상인 숫자인지 확인하는 메서드.
     * 
     * 문자열이 null이 아니고 비어 있지 않은 상태에서 지정된 최소값(min) 이상인 정수인지 확인합니다.
     * 
     * @param value 검사할 문자열
     * @param min 최소 허용 값
     * @return 문자열이 주어진 최소값 이상인 숫자이면 true, 그렇지 않으면 false
     */
    public boolean checkNumber(String value, int min) {
        if (isNullOrEmpty(value)) {
            return false;
        }
        try {
            int number = Integer.parseInt(value);
            return number >= min;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * 주어진 날짜가 현재 날짜보다 과거인지 검사하는 메서드.
     *
     * 이 메서드는 문자열로 된 날짜를 LocalDate로 변환한 후, 
     * 해당 날짜가 오늘 날짜보다 이전인지 확인합니다.
     *
     * @param value 검사할 날짜 문자열 (yyyyMMdd 형식)
     * @return 주어진 날짜가 과거일 경우 true, 그렇지 않으면 false
     */
    public boolean isPast(String value) {
    	
    	LocalDate time = parser.parseStringToLocalDate(value);
        LocalDate today = LocalDate.now();
        
		return time.isBefore(today);
    }
    
    /**
     * 주어진 입력 문자열이 지정된 정규 표현식 패턴과 일치하는지 검사하는 메서드.
     *
     * 이 메서드는 입력 문자열이 null이 아니고, 지정된 패턴과 일치하는지 확인합니다.
     *
     * @param input 검사할 입력 문자열
     * @param pattern 일치 여부를 확인할 정규 표현식 패턴
     * @return 입력 문자열이 패턴과 일치할 경우 true, 그렇지 않으면 false
     */
    public boolean checkRegex(String input, String pattern) {
        return input != null && input.matches(pattern);
    }
    
}
