package com.inside.idmcs.api.common.model.vo.res;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PublicKeyRes {

	private String publicKey;
	private String startDate;
	private String expiryDate;
	
	public PublicKeyRes(String publicKey, String startDate, String expiryDate) {
		super();
		this.publicKey = publicKey;
		this.startDate = startDate;
		this.expiryDate = expiryDate;
	}
	
}
