package com.inside.idmcs.api.common.model.vo.req;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReqVO {
	
	private String agencyToken;
	private String applicationToken;
	private String birthDay;
	private String subCode;
	private String userName;
	private String issuedYmd;
	private String encKey;
	private String ci;
	private String appKey;
	private String mobileNo;
	private String deviceInfo;
	private String telecom;
	private String qrCd;
	private String stDt;
	private String endDt;
	private int curListIndex;
	private int reqListCnt;
	private String order;
	private String status;
	private String range;
	
	public ReqVO() {
		super();
	}
	
	public ReqVO(String agencyToken, String applicationToken) {
		super();
		this.agencyToken = agencyToken;
		this.applicationToken = applicationToken;
	}

}