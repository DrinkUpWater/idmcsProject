package com.inside.idmcs.api.enc.pk.service;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.model.vo.req.PublicKeyReqVO;
import com.inside.idmcs.api.common.model.vo.res.ResVO;

import jakarta.servlet.http.HttpServletRequest;

public interface PublicKeyService {

	/**
	 * 공개키 생성을 처리하고 응답 데이터를 생성하는 메서드.
	 *
	 * 이 메서드는 주어진 PublicKeyReqVO 요청 객체와 HTTP 요청 정보를 사용하여 
	 * 공개키를 생성하고, 결과를 포함한 ResVO 객체를 반환합니다. 
	 * 처리 중 오류가 발생할 경우 CustomException을 던집니다.
	 *
	 * @param reqVO 공개키 생성을 위한 요청 데이터가 담긴 PublicKeyReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return 생성된 공개키 응답 데이터를 포함하는 ResVO 객체
	 * @throws CustomException 공개키 생성 과정에서 오류가 발생할 경우 발생하는 예외
	 */
	<T> ResVO<T> generatePublicKeyRequest(PublicKeyReqVO reqVO, HttpServletRequest request) throws CustomException;
	
}
