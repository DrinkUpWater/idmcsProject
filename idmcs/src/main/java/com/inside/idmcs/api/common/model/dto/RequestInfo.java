package com.inside.idmcs.api.common.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestInfo {
	
	private String reqMethod;
	private String reqUrl;
	private String queryString;
	private String reqIp;
	private String reqHeader;
	private String jsonReqBody;
	
	public RequestInfo(String reqMethod, String reqUrl, String queryString, String reqIp, String reqHeader,
			String jsonReqBody) {
		super();
		this.reqMethod = reqMethod;
		this.reqUrl = reqUrl;
		this.queryString = queryString;
		this.reqIp = reqIp;
		this.reqHeader = reqHeader;
		this.jsonReqBody = jsonReqBody;
	}
	
}
