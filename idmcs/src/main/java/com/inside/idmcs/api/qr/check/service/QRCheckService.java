package com.inside.idmcs.api.qr.check.service;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.model.vo.req.QRCheckReqVO;
import com.inside.idmcs.api.common.model.vo.res.ResVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface QRCheckService {

	/**
	 * QR 코드 검증 요청을 처리하고 응답 데이터를 생성하는 메서드.
	 *
	 * 이 메서드는 주어진 QRCheckReqVO 객체와 HTTP 요청 정보를 사용하여 
	 * QR 코드 검증을 수행합니다. 처리 결과를 ResVO 객체로 반환하며, 
	 * 오류가 발생할 경우 CustomException을 던집니다.
	 *
	 * @param reqVO QR 코드 검증을 위한 요청 데이터가 담긴 QRCheckReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return QR 코드 검증 결과를 포함한 ResVO 객체
	 * @throws CustomException QR 코드 검증 과정에서 오류가 발생할 경우 발생하는 예외
	 */
	<T> ResVO<T> checkQRRequest(@Valid QRCheckReqVO reqVO, HttpServletRequest request) throws CustomException;

}