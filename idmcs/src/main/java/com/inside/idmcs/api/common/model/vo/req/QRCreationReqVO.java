package com.inside.idmcs.api.common.model.vo.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QRCreationReqVO extends ReqVO {
	
	
	@NotNull(message = "기관토큰")
    @Size(max = 128, message = "기관토큰 크기")
	private String agencyToken;
	
	@NotNull(message = "연계허용App 토큰")
    @Size(max = 128, message = "연계허용App 토큰 크기")
	private String applicationToken;
	
	@NotNull(message = "고객 CI")
	private String ci;
	
	@NotNull(message = "앱 AppKey")
	private String appKey;
	
	@NotNull(message = "핸드폰번호")
	private String mobileNo;
	
	@NotNull(message = "단말정보 해시")
	private String deviceInfo;
	
	@NotNull(message = "통신사 구분")
    @Size(max = 1, message = "통신사 구분 크기")
	private String telecom;
	
	@NotNull(message = "대칭키")
	private String encKey;
	
}
