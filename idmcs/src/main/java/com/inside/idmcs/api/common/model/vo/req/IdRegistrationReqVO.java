package com.inside.idmcs.api.common.model.vo.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IdRegistrationReqVO extends ReqVO {
	
	@NotNull(message = "기관토큰")
    @Size(max = 128, message = "기관토큰 크기")
	private String agencyToken;
	
	@NotNull(message = "연계허용App 토큰")
    @Size(max = 128, message = "연계허용App 토큰 크기")
	private String applicationToken;
	
	@NotNull(message = "생년월일")
	private String birthDay;
	
	@NotNull(message = "신분번호 뒷자리")
	private String subCode;
	
	@NotNull(message = "성명")
	private String userName;
	
	@NotNull(message = "발급일자")
	private String issuedYmd;
	
	@NotNull(message = "서버 암호화키")
	private String encKey;
	
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

	public IdRegistrationReqVO(@NotNull(message = "기관토큰") @Size(max = 128, message = "기관토큰 크기") String agencyToken,
			@NotNull(message = "앱토큰") @Size(max = 128, message = "앱토큰 크기") String applicationToken) {
		super();
		this.agencyToken = agencyToken;
		this.applicationToken = applicationToken;
	}
	
}
