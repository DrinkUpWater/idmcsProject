package com.inside.idmcs.api.common.model.vo.db;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QRHistory {
	
	private long qrHistNo;
	private String checkRes;
	private String checkDt;
	private String qrCd;
	private long appNo;
	private long instNo;
	private String empId;
}
