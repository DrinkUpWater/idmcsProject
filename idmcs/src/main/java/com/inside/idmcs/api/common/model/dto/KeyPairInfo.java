package com.inside.idmcs.api.common.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KeyPairInfo {
	
	private String publicKey;
	private String privateKey;
	private String vldBgngYmd;
	private String vldEndYmd;
	
	public KeyPairInfo() {
		super();
	}
	
	public KeyPairInfo(String publicKey, String privateKey, String vldBgngYmd, String vldEndYmd) {
		super();
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.vldBgngYmd = vldBgngYmd;
		this.vldEndYmd = vldEndYmd;
	}
	
}
