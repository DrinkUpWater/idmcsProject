package com.inside.idmcs.api.common.model.vo.db;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Institution {
	
	private long instNo;
	private String instNm;
	private String instToken;
	private String usePsbltyYn;
	private String vldBgngYmd;
	private String vldEndYmd;
	private String crtDt;
	private String updtDt;
	private String stts;
	
}
