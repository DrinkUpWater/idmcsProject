package com.inside.idmcs.api.common.util.validation;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class QRHistoryValidation extends QRValidation {

	public QRHistoryValidation(Parser parser) {
		super(parser);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 주어진 ReqVO 객체의 유효성을 검사하는 메서드.
	 *
	 * 이 메서드는 상위 클래스의 유효성 검사를 수행한 후, QR 이력 관련 필드들의 유효성을 추가로 검사합니다.
	 * 각 필드가 유효하지 않은 경우, F103 오류 코드와 함께 CustomException을 발생시킵니다.
	 *
	 * @param reqVO 유효성 검사를 수행할 ReqVO 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F103: 시작 인덱스가 0 이하인 경우
	 *                         - F103: 요청 목록 수가 0 이하인 경우
	 *                         - F103: 정렬 순서가 유효하지 않은 경우
	 *                         - F103: 검증 상태가 유효하지 않은 경우
	 *                         - F103: 조회 범위가 유효하지 않은 경우
	 *                         - F103: 조회 기간 시작일 또는 종료일 형식이 유효하지 않은 경우 (yyyyMMdd)
	 */
	@Override
	public void isValid(ReqVO reqVO) throws CustomException {
		super.isValid(reqVO);
		
		//QR이력 시작 인덱스 유효성 검사
		if(!(reqVO.getCurListIndex() > 0)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("시작 인덱스"));
		}
		
		//QR이력 요청 목록 수 유효성 검사
		if(!(reqVO.getReqListCnt() > 0)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("요청 목록 수"));
		}

		//QR이력 정렬 순서 유효성 검사
		if (!checkOrder(reqVO.getOrder())) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("정렬 순서"));
		}

		//QR이력 검증상태 유효성 검사
		if (!checkQRstatus(reqVO.getStatus())) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("검증상태"));
		}

		//QR이력 조회범위 유효성 검사
		if (!checkRange(reqVO.getRange())) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("조회 범위"));
		}
		
		//QR이력 조회 기간 시작일 유효성 검사
		if (!checkDateFormat(reqVO.getStDt(), "yyyyMMdd")) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("조회 기간 시작일"));
		}
		
		//QR이력 조회 기간 종료일 유효성 검사
		if (!checkDateFormat(reqVO.getEndDt(), "yyyyMMdd")) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("조회 기간 종료일"));
		}
		
	}

	/**
	 * 주어진 정렬 순서가 유효한지 검사하는 메서드.
	 *
	 * 이 메서드는 정렬 순서가 "DESC" 또는 "ASC" 중 하나인지 확인합니다.
	 *
	 * @param order 검사할 정렬 순서 문자열
	 * @return 유효한 정렬 순서일 경우 true, 그렇지 않으면 false
	 */
	public boolean checkOrder(String order) {

		if (order.equals("DESC") || order.equals("ASC"))
			return true;

		return false;
	}

	/**
	 * 주어진 QR 코드 상태가 유효한지 검사하는 메서드.
	 *
	 * 이 메서드는 상태 값이 "S", "F", "A" 중 하나인지 확인합니다.
	 *
	 * @param status 검사할 QR 코드 상태 문자열
	 * @return 유효한 상태일 경우 true, 그렇지 않으면 false
	 */
	public boolean checkQRstatus(String status) {

		if (status.equals("S") || status.equals("F") || status.equals("A"))
			return true;

		return false;
	}

	/**
	 * 주어진 조회 범위가 유효한지 검사하는 메서드.
	 *
	 * 이 메서드는 조회 범위가 "ALL" 또는 "APP" 중 하나인지 확인합니다.
	 *
	 * @param range 검사할 조회 범위 문자열
	 * @return 유효한 조회 범위일 경우 true, 그렇지 않으면 false
	 */
	public boolean checkRange(String range) {

		if (range.equals("ALL") || range.equals("APP"))
			return true;

		return false;
	}

}
