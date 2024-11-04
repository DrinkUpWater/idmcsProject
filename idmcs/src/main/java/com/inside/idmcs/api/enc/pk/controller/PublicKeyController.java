package com.inside.idmcs.api.enc.pk.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.vo.req.PublicKeyReqVO;
import com.inside.idmcs.api.common.model.vo.res.PublicKeyRes;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.enc.pk.service.PublicKeyService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PublicKeyController {

	private final PublicKeyService publicKeyService;

	public PublicKeyController(PublicKeyService publicKeyService) {
		this.publicKeyService = publicKeyService;
	}

	/**
	 * 공개키 생성 요청을 처리하는 API 엔드포인트.
	 *
	 * 이 메서드는 주어진 요청 본문(PublicKeyReqVO)을 기반으로 공개키를 생성합니다. 
	 * 성공 시 생성된 공개키와 관련 정보를 포함하는 ResVO 객체를 반환하며, 
	 * 예외가 발생할 경우 적절한 오류 코드와 메시지를 반환합니다.
	 *
	 * @param reqVO 공개키 생성을 위한 요청 데이터가 담긴 PublicKeyReqVO 객체
	 * @param request HTTP 요청 객체 (클라이언트 정보 포함)
	 * @return 생성된 공개키 응답 정보를 포함한 ResponseEntity 객체
	 *         - 성공: HTTP 상태 코드 200 (OK)와 함께 ResVO<PublicKeyRes> 객체 반환
	 *         - CustomException 발생 시: 오류 코드와 메시지 포함
	 *         - 기타 예외 발생 시: F901 오류 코드와 메시지 포함
	 */
	@PostMapping("/api/idv/enc/publicKey")
	public ResponseEntity<ResVO<PublicKeyRes>> generatePublicKeyRequest(@Valid @RequestBody PublicKeyReqVO reqVO, HttpServletRequest request) {

		try {
			ResVO<PublicKeyRes> resVO = publicKeyService.generatePublicKeyRequest(reqVO, request);
			
			return new ResponseEntity<>(resVO, HttpStatus.OK);
		} catch (CustomException e) {
			ResVO<PublicKeyRes> resVO = new ResVO<>(e.getErrorCode().name(), e.getMessage());

			return new ResponseEntity<>(resVO, HttpStatus.OK);
		} catch (Exception e) {
			// 서비스에서 예상하지 못한 오류 예외 처리
			log.error("알 수 없는 오류 : {}", e.getMessage(), e);
			ErrorCode errorCode = ErrorCode.F901;
			ResVO<PublicKeyRes> resVO = new ResVO<>(errorCode.name(), errorCode.getDescription());
			
			return new ResponseEntity<>(resVO, HttpStatus.OK);
		}
	}

}
