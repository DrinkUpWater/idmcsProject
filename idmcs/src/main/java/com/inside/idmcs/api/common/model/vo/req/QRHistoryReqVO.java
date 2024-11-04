package com.inside.idmcs.api.common.model.vo.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QRHistoryReqVO extends ReqVO {

	@NotNull(message = "기관토큰")
	@Size(max = 128, message = "기관토큰 크기")
	private String agencyToken;
	
	@NotNull(message = "연계허용App 토큰")
	@Size(max = 128, message = "연계허용App 토큰 크기")
	private String applicationToken;
	
	@NotNull(message = "고객 CI")
	private String ci;
	
	private String appKey;
	
	@NotNull(message = "핸드폰번호")
	private String mobileNo;
	
	private String deviceInfo;
	
	@NotNull(message = "통신사 구분")
	@Size(max = 1, message = "통신사 구분 크기")
	private String telecom;
	
	@NotNull(message = "조회 기간 시작일")
	@Size(max = 8, message = "조회 기간 시작일 크기")
	private String stDt;
	
	@NotNull(message = "조회 기간 종료일")
	@Size(max = 8, message = "조회 기간 종료일 크기")
	private String endDt;
	
	@NotNull(message = "시작 인덱스")
	private int curListIndex;
	
	@NotNull(message = "요청 목록 수")
	private int reqListCnt;
	
	@NotNull(message = "정렬 순서")
	@Size(max = 4, message = "정렬 순서 크기")
	private String order;
	
	@NotNull(message = "검증상태")
	@Size(max = 1, message = "검증상태 크기")
	private String status;
	
	@NotNull(message = "조회 범위")
	@Size(max = 3, message = "조회 범위 크기")
	private String range;
	
	@NotNull(message = "대칭키")
	private String encKey;
	
}
