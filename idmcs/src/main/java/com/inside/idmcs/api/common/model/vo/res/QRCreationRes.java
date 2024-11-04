package com.inside.idmcs.api.common.model.vo.res;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QRCreationRes {
	private String qrCd;

	public QRCreationRes(String qrCd) {
		super();
		this.qrCd = qrCd;
	}

}	
