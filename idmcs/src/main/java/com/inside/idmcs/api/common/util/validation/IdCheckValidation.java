package com.inside.idmcs.api.common.util.validation;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class IdCheckValidation extends IdValidation {

	public IdCheckValidation(Parser parser) {
		super(parser);
	}

	/**
	 * 주어진 ReqVO와 IdInfo 객체를 사용하여 사용 가능한 회원 정보인지 확인하는 메서드.
	 *
	 * 이 메서드는 상위 클래스의 checkUseIdInfo 메서드를 호출한 후, 
	 * 회원이 탈퇴한 상태인지 확인합니다. 만약 회원이 탈퇴한 상태일 경우 
	 * F206 오류 코드와 함께 CustomException을 던집니다.
	 *
	 * @param reqVO 검증할 요청 정보가 담긴 ReqVO 객체
	 * @param idInfo 검증할 회원 정보가 담긴 IdInfo 객체
	 * @param instNm 기관명
	 * @throws CustomException 회원이 탈퇴한 상태일 경우 F206 예외를 발생시킴
	 */
	@Override
	public void checkUseIdInfo(ReqVO reqVO, IdInfo idInfo, String instNm) throws CustomException {
		super.checkUseIdInfo(reqVO, idInfo, instNm);
		
		//탈퇴한 회원인지 확인
		if (idInfo.getRegistYn() != null && !idInfo.getRegistYn().equals("Y")) {
			throw new CustomException(ErrorCode.F206);
		}
		
	}

}
