package com.inside.idmcs.api.common.util.validation;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class IdValidation extends MobileValidation {

	public IdValidation(Parser parser) {
		super(parser);
	}
	
	public void isValid(IdInfo idInfo) throws CustomException {
		
		if(isNullOrEmpty(idInfo))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보 없음"));
		
		if(isNullOrEmpty(idInfo.getUserId()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(아이디)"));
		
		if(isNullOrEmpty(idInfo.getBirthDay()) || !checkDateFormat(idInfo.getBirthDay(), "yyyyMMdd"))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(생년월일)"));
		
		if(isNullOrEmpty(idInfo.getSubCode()) || !checkNumber(idInfo.getSubCode(), 0))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(신분번호)"));
		
		if(isNullOrEmpty(idInfo.getUserName()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(이름)"));
		
		if(isNullOrEmpty(idInfo.getIssuedYmd()) || !checkDateFormat(idInfo.getIssuedYmd(), "yyyyMMdd"))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(발급일)"));
		
		if(isNullOrEmpty(idInfo.getCi()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(ci)"));
		
		if(isNullOrEmpty(idInfo.getAddress()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(주소)"));
		
		if(isNullOrEmpty(idInfo.getDetailAddress()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(상세주소)"));
		
		if(isNullOrEmpty(idInfo.getIssuedInstNm()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(발급기관명)"));
		
		if(isNullOrEmpty(idInfo.getAppKey()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(appKey)"));
		
		if(isNullOrEmpty(idInfo.getTelecom()) || !checkTelecom(idInfo.getTelecom()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(통신사)"));
		
		if(isNullOrEmpty(idInfo.getDeviceInfo()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(단말정보)"));
		
		if(isNullOrEmpty(idInfo.getStatus()) || !checkEmpStatus(idInfo.getStatus()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(상태)"));
		
		if(isNullOrEmpty(idInfo.getIdStatus()) || !checkEmpIdStatus(idInfo.getIdStatus()))
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("신분정보(신분상태)"));
		
	}

	public boolean checkEmpStatus(String status) {
		
		// 재직, 휴직, 퇴직
		if (status.equals("재직") || status.equals("휴직") || status.equals("퇴직"))
			return true;
		
		return false;
	}
	
	public boolean checkEmpIdStatus(String idStatus) {
		
		// 정상, 훼손, 분실
		if (idStatus.equals("정상") || idStatus.equals("훼손") || idStatus.equals("분실"))
			return true;
		
		return false;
	}
	

}
