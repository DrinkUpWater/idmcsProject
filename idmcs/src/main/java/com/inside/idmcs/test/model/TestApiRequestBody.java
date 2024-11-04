package com.inside.idmcs.test.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TestApiRequestBody {
	
	private String resultCode;
	private String resultMessage;
	private RetData retData;
	private String appKey;
	private String encKey;
	
}
