package com.inside.idmcs.api.common.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IdInfo {
	
	private String userId;
	private String birthDay;
	private String subCode;
	private String userName;
	private String issuedYmd;
	private String ci;
	private String mobileNo;
	private String address;
	private String detailAddress;
	private String issuedInstNm;
	private String photo;
	private String appKey;
	private String telecom;
	private String deviceInfo;
	private String status; //직원상태
	private String idStatus; //신분증 상태
	private String registYn;
	
}
