package com.inside.idmcs.api.common.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MobileRequest {
	
	private String ci;
	private String appKey;
	private String mobileNo;
	private String deviceInfo;
	private String telecom;
	private String encKey;
	
	public MobileRequest() {
		super();
	}
	
	public MobileRequest(String ci, String appKey, String mobileNo, String deviceInfo, String telecom, String encKey) {
		super();
		this.ci = ci;
		this.appKey = appKey;
		this.mobileNo = mobileNo;
		this.deviceInfo = deviceInfo;
		this.telecom = telecom;
		this.encKey = encKey;
	}
	
}
