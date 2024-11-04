package com.inside.idmcs.api.common.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PageInfo {
	
	private int reqListCount;
	private int totalCount;
	private String hasNext;
	private int offset;
	
	public PageInfo(int reqListCount, int totalCount, String hasNext, int offset) {
		super();
		this.reqListCount = reqListCount;
		this.totalCount = totalCount;
		this.hasNext = hasNext;
		this.offset = offset;
	}
	
}
