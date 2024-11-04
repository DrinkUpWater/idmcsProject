package com.inside.idmcs.api.common.model.vo.db;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Ip {

	private long ipNo;
	private long appNo;
	private String ip;
	private String cidr;
	private String usePsbltyYn;
	private String stts;
	
}
