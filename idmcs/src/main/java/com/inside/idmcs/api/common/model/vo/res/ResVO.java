package com.inside.idmcs.api.common.model.vo.res;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResVO<T> {
	
	private String resultCode;
	private String resultMessage;
	private T retData;
	
	public ResVO() {
		super();
	}
	
	public ResVO(String resultCode, String resultMessage) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
	}

	public ResVO(String resultCode, String resultMessage, T retData) {
		super();
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.retData = retData;
	}
}
