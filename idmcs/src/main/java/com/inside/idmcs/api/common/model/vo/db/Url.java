package com.inside.idmcs.api.common.model.vo.db;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Url {
	
	private long urlNo;
	private String url;
	private String usePsbltyYn;
	private String vldBgngYmd;
	private String vldEndYmd;
	private String stts;
	
}
