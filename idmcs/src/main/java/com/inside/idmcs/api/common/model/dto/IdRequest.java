package com.inside.idmcs.api.common.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IdRequest {
	
	private String birthDay;
	private String subCode;
	private String userName;
	private String issuedYmd;
	
	public IdRequest() {
		super();
	}
	
	public IdRequest(String birthDay, String subCode, String userName, String issuedYmd) {
		super();
		this.birthDay = birthDay;
		this.subCode = subCode;
		this.userName = userName;
		this.issuedYmd = issuedYmd;
	}
	
}
