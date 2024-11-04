package com.inside.idmcs.api.common.model.vo.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QRCheckReqVO extends ReqVO {
	
	@NotNull(message = "기관토큰")
	@Size(max = 128, message = "기관토큰 크기")
	private String agencyToken;
	
	@NotNull(message = "연계허용App 토큰")
	@Size(max = 128, message = "연계허용App 토큰")
	private String applicationToken;
	
	@NotNull(message = "QR 코드")
	private String qrCd;
	
	private String ci;
	
	@NotNull(message = "서버 대칭키")
	private String encKey;
	
}
