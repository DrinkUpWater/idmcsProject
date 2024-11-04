package com.inside.idmcs.api.qr.check.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.vo.req.QRCheckReqVO;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.qr.check.service.QRCheckService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j	
@Controller
public class QRCheckController {
	
	private QRCheckService qRCheckService;

	public QRCheckController (QRCheckService qRCheckService) {
		this.qRCheckService = qRCheckService;
	}
	
	/**
	 * QR 코드 검증 요청을 처리하는 API 엔드포인트.
	 *
	 * 이 메서드는 주어진 QRCheckReqVO 객체와 HTTP 요청 정보를 사용하여 
	 * QR 코드 검증을 수행합니다. 성공 시 ResVO 객체를 반환하며, 
	 * 예외 발생 시 적절한 오류 코드와 메시지를 포함한 응답을 반환합니다.
	 *
	 * @param reqVO QR 코드 검증을 위한 요청 데이터를 담고 있는 QRCheckReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return QR 코드 검증 결과를 포함한 ResponseEntity 객체
	 *         - 성공: HTTP 상태 코드 200과 함께 ResVO 객체 반환
	 *         - CustomException 발생 시: 오류 코드와 메시지 포함
	 *         - 기타 예외 발생 시: F901 오류 코드와 메시지 포함
	 */
	@PostMapping("/api/idv/qr/check")
	public <T> ResponseEntity<ResVO<T>> checkQRRequest(@Valid @RequestBody QRCheckReqVO reqVO, HttpServletRequest request) {
		
		try {
			ResVO<T> resVO = qRCheckService.checkQRRequest(reqVO, request);
			
			return new ResponseEntity<>(resVO, HttpStatus.OK);
		} catch (CustomException e) {
			ResVO<T> resVO = new ResVO<>(e.getErrorCode().name(), e.getMessage());
			
			return new ResponseEntity<>(resVO, HttpStatus.OK);
		} catch (Exception e) {
			// 서비스에서 예상하지 못한 오류 예외 처리
			log.error("알 수 없는 오류 : {}", e.getMessage(), e);
			ErrorCode errorCode = ErrorCode.F901;
			ResVO<T> resVO = new ResVO<>(errorCode.name(), errorCode.getDescription());
			
			return new ResponseEntity<>(resVO, HttpStatus.OK);
		}
	}
}
