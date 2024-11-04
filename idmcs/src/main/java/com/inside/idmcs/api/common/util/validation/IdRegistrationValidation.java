package com.inside.idmcs.api.common.util.validation;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.IdRequest;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class IdRegistrationValidation extends IdValidation {

	public IdRegistrationValidation(Parser parser) {
		super(parser);
	}

	/**
	 * 주어진 ReqVO 객체의 유효성을 검사하는 메서드.
	 *
	 * 이 메서드는 상위 클래스의 isValid 메서드를 호출한 후, 
	 * ReqVO에서 파싱한 IdRequest 객체의 유효성을 추가로 검사합니다.
	 * 만약 유효성 검사에 실패하면 CustomException을 던질 수 있습니다.
	 *
	 * @param reqVO 유효성 검사를 수행할 ReqVO 객체
	 * @throws CustomException 유효성 검사에 실패할 경우 발생하는 예외
	 */
	@Override
	public void isValid(ReqVO reqVO) throws CustomException {
		super.isValid(reqVO);
		
		// 신분 요청정보 유효성 검사
		IdRequest idRequest = parser.getIdRequest(reqVO);
		isValid(idRequest);
	}
	
	/**
	 * 주어진 IdRequest 객체의 필드들을 유효성 검사하는 메서드.
	 *
	 * 이 메서드는 IdRequest 객체의 필수 정보를 검사하며, 각 필드에 대해 크기, 형식, 날짜 유효성 등을 확인합니다.
	 * 유효하지 않은 경우 적절한 오류 코드와 메시지를 포함한 CustomException을 발생시킵니다.
	 *
	 * @param idRequest 유효성 검사를 수행할 IdRequest 객체
	 * @throws CustomException 유효성 검사에 실패할 경우 발생하는 예외
	 *                         - F102: 필수 정보 누락
	 *                         - F103: 유효하지 않은 데이터 형식 또는 값
	 */
	private void isValid(IdRequest idRequest) throws CustomException {

		if (isNullOrEmpty(idRequest)) {
			throw new CustomException(ErrorCode.F102, ErrorCode.F102.formatMessage("필수정보 누락"));
		}

		// 생년월일 정보 유효성검사
		String birthDay = idRequest.getBirthDay();
		if (isNullOrEmpty(birthDay)) {
			throw new CustomException(ErrorCode.F102, ErrorCode.F102.formatMessage("생년월일"));
		}
		if (!checkSize(birthDay, 8) || !checkDateFormat(birthDay, "yyyyMMdd") || !isPast(birthDay)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("생년월일"));
		}

		// 수정해야함 검사 사항 더 추가해야함
		// 신분번호 뒷자리 정보 유효성검사
		String subCode = idRequest.getSubCode();
		if (isNullOrEmpty(subCode)) {
			throw new CustomException(ErrorCode.F102, ErrorCode.F102.formatMessage("신분번호 뒷자리"));
		}
		if (!checkSize(subCode, 7) || !checkSubCode(birthDay, subCode)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("신분번호 뒷자리"));
		}

		// 성명 정보 유효성검사
		String userName = idRequest.getUserName();
		if (isNullOrEmpty(userName)) {
			throw new CustomException(ErrorCode.F102, ErrorCode.F102.formatMessage("성명"));
		}
		//크기 및 정규식 검사(한글 영어만 포함)
		if (!checkSize(userName, 38) || !checkRegex(userName, "^[a-zA-Z가-힣]*$")) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("성명"));
		}

		// 발급일자 정보 유효성검사
		String issuedYmd = idRequest.getIssuedYmd();
		if (isNullOrEmpty(issuedYmd)) {
			throw new CustomException(ErrorCode.F102, ErrorCode.F102.formatMessage("발급일자"));
		}
		if (!checkSize(issuedYmd, 8) || !checkDateFormat(issuedYmd, "yyyyMMdd") || !isPast(issuedYmd)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("발급일자"));
		}

	}
	
	/**
	 * 주어진 ReqVO와 IdInfo 객체의 정보를 비교하여 신분 정보의 일치 여부를 확인하는 메서드.
	 *
	 * 이 메서드는 상위 클래스의 checkUseIdInfo 메서드를 호출한 후, 
	 * 성명, 생년월일, 신분번호 뒷자리, 발급일자가 일치하는지 추가로 검사합니다.
	 * 만약 정보가 일치하지 않을 경우 F303 오류 코드와 함께 CustomException을 발생시킵니다.
	 *
	 * @param reqVO 검증할 요청 정보가 담긴 ReqVO 객체
	 * @param idInfo 비교할 신분 정보가 담긴 IdInfo 객체
	 * @param instNm 기관명
	 * @throws CustomException 신분 정보가 일치하지 않을 경우 F303 예외를 발생시킴
	 */
	@Override
	public void checkUseIdInfo(ReqVO reqVO, IdInfo idInfo, String instNm) throws CustomException {
		super.checkUseIdInfo(reqVO, idInfo, instNm);
		
		// 신분정보오류 (성명, 주민번호, 발급일자 등 불일치)
		if (!reqVO.getUserName().equals(idInfo.getUserName()) 
				|| !reqVO.getBirthDay().equals(idInfo.getBirthDay())
				|| !reqVO.getSubCode().equals(idInfo.getSubCode())
				|| !reqVO.getIssuedYmd().equals(idInfo.getIssuedYmd())) {
			throw new CustomException(ErrorCode.F303);
		}
	}
	
	/**
	 * 주어진 생년월일과 신분번호 뒷자리의 첫 번째 자리를 검사하는 메서드.
	 *
	 * 이 메서드는 생년월일의 앞 두 자리에 따라 신분번호 뒷자리의 첫 번째 자리가 유효한지 검사합니다.
	 * 1800년대 출생자의 경우 0 또는 9, 홀수 연도대의 경우 1, 2, 5, 6,
	 * 짝수 연도대의 경우 3, 4, 7, 8이 유효한 값으로 인정됩니다.
	 *
	 * @param birthDay 검사할 생년월일 (yyyyMMdd 형식)
	 * @param subCode 검사할 신분번호 뒷자리 문자열
	 * @return 신분번호 뒷자리 첫 자리가 유효한 경우 true, 그렇지 않으면 false
	 */
	public boolean checkSubCode(String birthDay, String subCode) {
		
		//출생년도 앞자리 2개
		String birth = birthDay.substring(0, 2);
		//신분번호 첫 자리
		char subChar = subCode.charAt(0);
		//출생년도 1->홀수 0-> 짝수 
		int type = Integer.parseInt(birth) % 2;
		
		if (birth.equals("18"))//1800년대 0,9
			return subChar == '0' || subChar == '9';
		
		switch (type) {
	       case 1://19, 21 ,23..년대 1,2,5,6
	           return subChar == '1' || subChar == '2' || subChar == '5' || subChar == '6';
	       case 0://20, 22, 24..년대 3,4,7,8
	           return subChar == '3' || subChar == '4' || subChar == '7' || subChar == '8';
	       default:
	           return false; 
	    }
		
	}

}
