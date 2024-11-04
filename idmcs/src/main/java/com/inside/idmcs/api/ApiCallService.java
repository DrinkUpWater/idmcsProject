package com.inside.idmcs.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.util.parser.Parser;

@Service
public class ApiCallService {
	
	private final Parser parser;
	private final RestTemplate restTemplate;
	
	public ApiCallService(Parser parser, RestTemplate restTemplate) {
		this.parser = parser;
		this.restTemplate = restTemplate;
	}

	public IdInfo apiGetIdInfo(String ci) {

		// RequestBody 생성 (ci)
		Map<String, String> requestBody = parser.createRequestBody(ci);

		String url = "http://localhost:8080/api/idv/test/idinfo";
		// 신분정보 가져오는 api 호출
		IdInfo idInfo = apiGetIdInfo(requestBody, url);

		return idInfo;
		
	}

	public IdInfo apiGetIdInfo(Map<String, String> requestBody, String url) {

		ResponseEntity<IdInfo> response = restTemplate.postForEntity(url, requestBody, IdInfo.class);
        
        return response.getBody();

	}

}
