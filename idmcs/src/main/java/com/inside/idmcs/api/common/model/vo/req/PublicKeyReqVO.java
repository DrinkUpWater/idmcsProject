package com.inside.idmcs.api.common.model.vo.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PublicKeyReqVO extends ReqVO {
	
	@NotNull(message = "기관토큰")
    @Size(max = 128, message = "기관토큰 크기")
	private String agencyToken;
	
	@NotNull(message = "앱토큰")
    @Size(max = 128, message = "앱토큰 크기")
	private String applicationToken;
	
}
