package com.inside.idmcs.api.common.model.vo.res;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QRCheckRes {
	
	private String birthDay;
	private String subCode;
	private String userName;
	private String issuedYmd;
	private String address;
	private String detailAddress;
	private String photo;
	private String issuOrgName;
	private String appKey;
	private String deviceInfo;
	
}
