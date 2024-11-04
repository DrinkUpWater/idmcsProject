package com.inside.idmcs.api.common.util.parser;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.IdRequest;
import com.inside.idmcs.api.common.model.dto.MobileRequest;
import com.inside.idmcs.api.common.model.dto.PageInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Parser {
	
	private static final ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * 객체를 JSON 형식의 문자열로 변환하는 메서드.
	 * 
	 * 주어진 객체를 JSON 형식으로 변환하여 문자열로 반환합니다. 변환 과정에서 오류가 발생할 경우, 스택 트레이스를 출력하고 오류 메시지를
	 * 반환합니다.
	 * 
	 * @param object JSON으로 변환할 객체
	 * @return JSON 형식의 문자열 또는 변환 오류 시 "json 변환 오류" 메시지
	 */
	public String toJson(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
			return "json 변환 오류";
		}
	}

	/**
	 * 주어진 LocalDate 객체를 'yyyyMMdd' 형식의 문자열로 변환하는 메서드.
	 * 
	 * 주어진 날짜를 'yyyyMMdd' 형식으로 포맷팅하여 문자열로 반환합니다.
	 * 
	 * @param date 변환할 LocalDate 객체
	 * @return 'yyyyMMdd' 형식의 문자열
	 */
	public String formatDateString(LocalDate date, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return date.format(formatter);
	}

	/**
	 * 'yyyyMMdd' 형식의 문자열을 LocalDate 객체로 변환하는 메서드.
	 * 
	 * 주어진 'yyyyMMdd' 형식의 문자열을 LocalDate로 파싱하여 반환합니다.
	 * 
	 * @param dateStr 'yyyyMMdd' 형식의 날짜 문자열
	 * @return LocalDate 객체
	 */
	public LocalDate parseStringToLocalDate(String dateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return LocalDate.parse(dateStr, formatter);
	}

	/**
	 * ReqVO 객체로부터 MobileRequest 객체를 생성하여 반환하는 메서드.
	 *
	 * 이 메서드는 주어진 ReqVO 객체의 데이터를 사용하여 MobileRequest 객체를 생성합니다.
	 *
	 * @param reqVO MobileRequest 객체를 생성할 데이터가 담긴 ReqVO 객체
	 * @return 생성된 MobileRequest 객체
	 */
	public MobileRequest getMoblieRequest(ReqVO reqVO) {

		return new MobileRequest(reqVO.getCi(), reqVO.getAppKey(), reqVO.getMobileNo(), reqVO.getDeviceInfo(), reqVO.getTelecom(), reqVO.getEncKey());
	}

	/**
	 * 주어진 객체가 `ReqVO` 인스턴스인 경우 IdRequest 객체로 변환하는 메서드.
	 * 
	 * 이 메서드는 입력받은 객체가 `ReqVO` 타입일 경우, 해당 객체의 정보를 사용하여 새로운 IdRequest 객체를 생성하고 반환합니다.
	 * 그렇지 않은 경우 `null`을 반환합니다.
	 * 
	 * @param object IdRequest로 변환할 객체
	 * @return 변환된 IdRequest 객체 또는 `null`
	 */
	public IdRequest getIdRequest(Object object) {

		if (object instanceof ReqVO) {
			return new IdRequest(((ReqVO) object).getBirthDay(), ((ReqVO) object).getSubCode(),
					((ReqVO) object).getUserName(), ((ReqVO) object).getIssuedYmd());
		} else {
			return null;
		}
	}

	/**
	 * MobileRequest 객체의 데이터를 주어진 ReqVO 객체에 입력하는 메서드.
	 * 
	 * 이 메서드는 주어진 객체가 `ReqVO` 타입일 경우, MobileRequest 객체의 데이터를 해당 ReqVO 객체에 설정하고
	 * 반환합니다.
	 * 
	 * @param object        MobileRequest 데이터를 입력할 대상 객체 (ReqVO 타입이어야 함)
	 * @param mobileRequest 데이터를 가져올 MobileRequest 객체
	 * @return 데이터를 입력받은 ReqVO 객체
	 */
	public ReqVO inputDataToReqVO(Object object, MobileRequest mobileRequest) {

		if (object instanceof ReqVO) {
			((ReqVO) object).setCi(mobileRequest.getCi());
			((ReqVO) object).setAppKey(mobileRequest.getAppKey());
			((ReqVO) object).setMobileNo(mobileRequest.getMobileNo());
			((ReqVO) object).setDeviceInfo(mobileRequest.getDeviceInfo());
			((ReqVO) object).setTelecom(mobileRequest.getTelecom());
		}

		return (ReqVO) object;
	}

	/**
	 * IdRequest 객체의 데이터를 주어진 ReqVO 객체에 입력하는 메서드.
	 * 
	 * 이 메서드는 주어진 객체가 `ReqVO` 타입일 경우, IdRequest 객체의 데이터를 해당 ReqVO 객체에 설정하고 반환합니다.
	 * 
	 * @param object          IdRequest 데이터를 입력할 대상 객체 (ReqVO 타입이어야 함)
	 * @param encrytIdRequest 데이터를 가져올 IdRequest 객체
	 * @return 데이터를 입력받은 ReqVO 객체
	 */
	public ReqVO inputDataToReqVO(Object object, IdRequest encrytIdRequest) {

		if (object instanceof ReqVO) {
			((ReqVO) object).setBirthDay(encrytIdRequest.getBirthDay());
			((ReqVO) object).setSubCode(encrytIdRequest.getSubCode());
			((ReqVO) object).setUserName(encrytIdRequest.getUserName());
			((ReqVO) object).setIssuedYmd(encrytIdRequest.getIssuedYmd());
		}

		return (ReqVO) object;
	}

	/**
	 * 주어진 agencyToken과 applicationToken 값을 ReqVO 객체에 입력하는 메서드.
	 * 
	 * 이 메서드는 주어진 객체가 `ReqVO` 타입일 경우, agencyToken과 applicationToken을 해당 ReqVO 객체에
	 * 설정하고 반환합니다.
	 * 
	 * @param object           token 값을 입력할 대상 객체 (ReqVO 타입이어야 함)
	 * @param agencyToken      설정할 기관 토큰
	 * @param applicationToken 설정할 애플리케이션 토큰
	 * @return 데이터를 입력받은 ReqVO 객체
	 */
	public ReqVO inputDataToReqVO(Object object, String agencyToken, String applicationToken) {

		if (object instanceof ReqVO) {
			((ReqVO) object).setAgencyToken(agencyToken);
			((ReqVO) object).setApplicationToken(applicationToken);
		}

		return (ReqVO) object;
	}

	/**
	 * 주어진 ReqVO 객체의 데이터를 새로운 ReqVO 객체로 복사하는 메서드.
	 * 
	 * 이 메서드는 주어진 객체가 `ReqVO` 타입일 경우, 해당 객체의 모든 필드 값을 새로운 ReqVO 객체에 복사하고 반환합니다. 만약
	 * 객체가 `ReqVO` 타입이 아니면 빈 ReqVO 객체를 반환합니다.
	 * 
	 * @param object 데이터를 복사할 원본 객체 (ReqVO 타입이어야 함)
	 * @return 데이터를 복사한 새로운 ReqVO 객체
	 * @throws Exception 
	 */
	public ReqVO inputDataToReqVO(Object object) throws Exception {

		try {
			ReqVO reqVO = new ReqVO();

			if (object instanceof ReqVO) {
				ReqVO obj = (ReqVO) object;
				reqVO.setAgencyToken(obj.getAgencyToken());
				reqVO.setApplicationToken(obj.getApplicationToken());
				reqVO.setBirthDay(obj.getBirthDay());
				reqVO.setSubCode(obj.getSubCode());
				reqVO.setUserName(obj.getUserName());
				reqVO.setIssuedYmd(obj.getIssuedYmd());
				reqVO.setEncKey(obj.getEncKey());
				reqVO.setCi(obj.getCi());
				reqVO.setAppKey(obj.getAppKey());
				reqVO.setMobileNo(obj.getMobileNo());
				reqVO.setDeviceInfo(obj.getDeviceInfo());
				reqVO.setTelecom(obj.getTelecom());
				reqVO.setQrCd(obj.getQrCd());
				reqVO.setStDt(obj.getStDt());
				reqVO.setEndDt(obj.getEndDt());
				reqVO.setCurListIndex(obj.getCurListIndex());
				reqVO.setReqListCnt(obj.getReqListCnt());
				reqVO.setOrder(obj.getOrder());
				reqVO.setStatus(obj.getStatus());
				reqVO.setRange(obj.getRange());
			}

			return reqVO;

		} catch (Exception e) {
			log.error("ReqVO 파싱 실패 : {}", object);
			throw new Exception("ReqVO 파싱 실패");
		}
	}
	
	/**
	 * 주어진 문자열 형식에 따라 DateTimeFormatter 객체를 생성하여 반환하는 메서드.
	 *
	 * 이 메서드는 주어진 날짜 형식(format)을 사용하여 해당 형식에 맞는 
	 * DateTimeFormatter 객체를 생성합니다.
	 *
	 * @param issuedTimestamp 형식화할 타임스탬프 문자열 (사용되지 않음)
	 * @param format 날짜와 시간의 형식을 정의하는 패턴 문자열
	 * @return 주어진 형식에 맞는 DateTimeFormatter 객체
	 */
	public DateTimeFormatter StringToFormatDate(String issuedTimestamp, String format) {
		
		return DateTimeFormatter.ofPattern(format);
	}
	
	/**
	 * 요청에서 클라이언트의 IP 주소를 가져오는 메서드.
	 * 
	 * 여러 HTTP 헤더(X-Forwarded-For, Proxy-Client-IP 등)를 확인하여 클라이언트의 실제 IP 주소를 가져옵니다.
	 * 만약 헤더에 유효한 IP가 없으면, `request.getRemoteAddr()`을 통해 클라이언트의 IP를 반환합니다.
	 * 
	 * @param request 클라이언트의 요청 정보를 담고 있는 HttpServletRequest 객체
	 * @return 클라이언트의 IP 주소
	 */
	public String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 클라이언트 요청 정보를 가져오는 메서드.
	 * 
	 * 이 메서드는 요청 메소드, URI, 쿼리 파라미터, 요청 IP, 요청 헤더, 요청 바디(JSON) 등의 정보를 수집하여
	 * `RequestInfo` 객체로 반환합니다.
	 * 
	 * @param request 클라이언트의 요청 정보를 담고 있는 HttpServletRequest 객체
	 * @return 요청 정보를 담은 RequestInfo 객체
	 * @throws CustomException 
	 */
	public RequestInfo getRequestInfo(HttpServletRequest request) throws CustomException {
		try {
			
			// 1. 요청 메소드 (GET, POST 등)
			String reqMethod = request.getMethod();

			// 2. 요청 URI
			String reqUrl = request.getRequestURI();

			// 3. 요청 쿼리 파라미터 (GET 파라미터)
			String queryString = request.getQueryString();

			// 4. 요청 IP
			String reqIp = getClientIp(request);

			// 4. 요청 헤더
			Enumeration<String> headerNames = request.getHeaderNames();
			StringBuilder headers = new StringBuilder();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				String headerValue = request.getHeader(headerName);
				headers.append(headerName).append(": ").append(headerValue);//.append("\n");
			}
			String reqHeader = headers.toString();

			// 5. 요청 바디(JSON)
			ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
			byte[] requestBody = wrapper.getContentAsByteArray();
			String jsonReqBody = "";
			try {
				jsonReqBody = new String(requestBody, request.getCharacterEncoding());
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			return new RequestInfo(reqMethod, reqUrl, queryString, reqIp, reqHeader, jsonReqBody);

		} catch (Exception e) {
			log.error("[내부오류] ReqeustInfo객체 생성 실패 : {}", request);
			throw new CustomException(ErrorCode.F901);
		}
	}
	
	/**
	 * 페이지네이션 정보를 생성하여 반환하는 메서드.
	 *
	 * 이 메서드는 현재 목록 인덱스, 요청 목록 수, 전체 히스토리 개수를 기반으로 
	 * PageInfo 객체를 생성합니다. 오프셋과 다음 페이지 존재 여부를 계산하여 반환합니다.
	 *
	 * @param curListIndex 현재 페이지의 목록 인덱스 (1부터 시작)
	 * @param reqListCnt 요청한 목록의 개수 (한 페이지당 표시할 항목 수)
	 * @param historyTotalCount 전체 히스토리 개수
	 * @return 생성된 PageInfo 객체
	 */
	public PageInfo createPageInfo(int curListIndex, int reqListCnt, int historyTotalCount) {
		
		int offset = (curListIndex - 1) * reqListCnt;
		
		String hasNext = historyTotalCount - (curListIndex * reqListCnt) > 0 ? "Y" : "N";
		
		return new PageInfo(reqListCnt, historyTotalCount, hasNext, offset);
	}
	
	/**
	 * 주어진 CI 값을 사용하여 요청 본문(request body)으로 사용할 맵 객체를 생성하는 메서드.
	 *
	 * 이 메서드는 단일 CI 값을 포함하는 키-값 쌍을 가진 맵 객체를 생성하여 반환합니다.
	 *
	 * @param ci 요청 본문에 포함할 CI 값
	 * @return CI 값을 포함한 요청 본문 맵 객체
	 */
	public Map<String, String> createRequestBody(String ci) {

		 Map<String, String> requestBody = new HashMap<>();
	        requestBody.put("ci", ci);
		
		return requestBody;
	}

}
