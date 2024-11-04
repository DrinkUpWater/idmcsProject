package com.inside.idmcs.api.common.model.vo.res;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QRHistoryRes {
	
	private List<QRCheckHistory> qrHist;
	private String hasNext;
	private int totCnt;
	
	public QRHistoryRes(List<QRCheckHistory> qrHist, String hasNext, int totCnt) {
		super();
		this.qrHist = qrHist;
		this.hasNext = hasNext;
		this.totCnt = totCnt;
	}
	
}
