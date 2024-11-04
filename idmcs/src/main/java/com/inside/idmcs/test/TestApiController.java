package com.inside.idmcs.test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.test.model.TestApiReqCi;
import com.inside.idmcs.test.model.TestApiRequestBody;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class TestApiController {
	
	private final TestApiService testApiService;
	
	TestApiController (TestApiService testApiService) {
		this.testApiService = testApiService;
	}
	
	//임시 신분정보 가져오는 API
	@PostMapping("/api/idv/test/idinfo")
	ResponseEntity<IdInfo> selectIdinfoApi(@RequestBody TestApiReqCi reqVO, HttpServletRequest request) {
		
		try {
			IdInfo idInfo = testApiService.selectIdinfoApi(reqVO);
			
			return new ResponseEntity<>(idInfo, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
		
	}
	
	//reqVO데이터 암호화하는 API
	@PostMapping("/api/idv/test/encrypt")
	ResponseEntity<ReqVO> encryptoDataApi(@RequestBody ReqVO reqVO, HttpServletRequest request) {
		
		
		try {
			ReqVO encryptReqVO = testApiService.encryptoDataApi(reqVO);
			
			return new ResponseEntity<>(encryptReqVO, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
		
	}
	
	//신분정보 복호화하는 API
	@PostMapping("/api/idv/test/decrypt")
	ResponseEntity<IdInfo> decryptoDataApi(@RequestBody TestApiRequestBody reqVO, HttpServletRequest request) {
		
		
		try {
			IdInfo idInfo = testApiService.decryptoDataApi(reqVO);
			
			return new ResponseEntity<>(idInfo, HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
		
	}
	
}
