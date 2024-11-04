package com.inside.idmcs.api.common.model.vo.db;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Application {
	
	private long appNo;
	private String appNm;
	private String appToken;
	private long instNo;
	private String usePsbltyYn;
	private String vldBgngYmd;
	private String vldEndYmd;
	private String publicKey;
	private String privateKey;
	private String keyVldBgngYmd;
	private String keyVldEndYmd;
	private String crtDt;
	private String updtDt;
	private String stts;
	
}
