package com.inside.idmcs.api.id.terminate.service;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.model.vo.req.IdTerminationReqVO;
import com.inside.idmcs.api.common.model.vo.res.ResVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

public interface IdTerminationService {

	/**
	 * 신분 해지 요청을 처리하고 응답 데이터를 생성하는 메서드.
	 *
	 * 이 메서드는 주어진 IdTerminationReqVO 객체와 HTTP 요청 정보를 사용하여 신분 해지를 수행합니다.
	 * 처리 결과를 ResVO 객체로 반환하며, 오류가 발생할 경우 CustomException을 던집니다.
	 *
	 * @param reqVO 신분 해지를 위한 요청 데이터가 담긴 IdTerminationReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return 신분 해지 결과를 포함한 ResVO 객체
	 * @throws CustomException 신분 해지 과정에서 오류가 발생할 경우 발생하는 예외
	 */
	<T> ResVO<T> terminateIdRequest(@Valid IdTerminationReqVO reqVO, HttpServletRequest request) throws CustomException;

}