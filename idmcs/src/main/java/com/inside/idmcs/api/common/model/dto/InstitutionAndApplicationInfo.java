package com.inside.idmcs.api.common.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InstitutionAndApplicationInfo {
	
	private long instNo;
	private String instNm;
	private String instToken;
	private String instVldBgngYmd;
	private String instVldEndYmd;
	private long appNo;
	private String appNm;
	private String appToken;
	private String appVldBgngYmd;
	private String appVldEndYmd;
	private String ipList;
	private String urlList;
	private String publicKey;
	private String privateKey;
	private String keyVldBgngYmd;
	private String keyVldEndYmd;
	private String instStatus;
	private String appStatus;
	
	
}
