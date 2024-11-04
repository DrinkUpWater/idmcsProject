package com.inside.idmcs.api.common.util.crypto;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.IdRequest;
import com.inside.idmcs.api.common.model.dto.MobileRequest;
import com.inside.idmcs.api.common.model.vo.req.IdRegistrationReqVO;
import com.inside.idmcs.api.common.util.parser.Parser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IdRegistrationCrypto extends IdCrypto {

	public IdRegistrationCrypto(Parser parser) {
		super(parser);
	}

	/**
	 * RSA 및 AES 알고리즘을 사용하여 IdRegistrationReqVO 객체와 그 내부 데이터들을 복호화하는 메서드.
	 *
	 * 이 메서드는 주어진 RSA 개인키와 AES 키를 사용하여 encKey 및 MobileRequest와
	 * IdRequest 데이터를 복호화합니다. 복호화가 비활성화된 경우, 입력된 IdRegistrationReqVO 객체를 그대로 반환합니다.
	 *
	 * @param idRegistrationReqVO 복호화할 IdRegistrationReqVO 객체
	 * @param privateKey RSA 복호화에 사용할 개인키 문자열
	 * @return 복호화된 데이터를 가진 새로운 IdRegistrationReqVO 객체
	 * @throws CustomException 복호화 과정에서 오류가 발생할 경우 F109 예외를 발생시킴
	 */
	public IdRegistrationReqVO decryptReqVO(IdRegistrationReqVO idRegistrationReqVO, String privateKey)
			throws CustomException {

		// 암화화 설정 false면 그대로 리턴
		if (!encryptionEnabled)
			return idRegistrationReqVO;

		try {
			// 복화화ReqVO에 암호화 안된 데이터 담기
			IdRegistrationReqVO decrytIdRegistrationReqVO = new IdRegistrationReqVO(
					idRegistrationReqVO.getAgencyToken(), idRegistrationReqVO.getApplicationToken());

			// encKey RSA 복화화
			String encKey = decryptRSA(idRegistrationReqVO.getEncKey(), privateKey);
			decrytIdRegistrationReqVO.setEncKey(encKey);
			
			// ReqVO의 MobileRequest 정보 파싱
			MobileRequest mobileRequest = parser.getMoblieRequest(idRegistrationReqVO);
			
			// MobileRequest 정보 AES 복호화(encKey)
			MobileRequest decrytMobileRequest = decryptAES(mobileRequest, encKey);
			
			// MobileRequest 정보 복화화ReqVO객체에 담기
			decrytIdRegistrationReqVO = (IdRegistrationReqVO) parser.inputDataToReqVO(decrytIdRegistrationReqVO,
					decrytMobileRequest);

			// ReqVO의 idRequest 정보 파싱
			IdRequest idRequest = parser.getIdRequest(idRegistrationReqVO);
			
			// IdRequest 정보 AES복호화(appKey)
			IdRequest decrytIdRequest = decryptAES(idRequest, decrytMobileRequest.getAppKey());
			
			// IdRequest정보 복호화ReqVO객체에 담기
			decrytIdRegistrationReqVO = (IdRegistrationReqVO) parser.inputDataToReqVO(decrytIdRegistrationReqVO,
					decrytIdRequest);
			
			return decrytIdRegistrationReqVO;
		} catch (Exception e) {
			throw new CustomException(ErrorCode.F109);
		}

	}
	
	/**
	 * AES 알고리즘을 사용하여 IdRequest 객체의 필드들을 복호화하는 메서드.
	 *
	 * 이 메서드는 주어진 AES 키를 사용하여 IdRequest 객체의 여러 필드를 복호화합니다.
	 * 복호화가 비활성화된 경우, 입력된 IdRequest 객체를 그대로 반환합니다.
	 *
	 * @param idRequest 복호화할 IdRequest 객체
	 * @param key AES 복호화에 사용할 키 문자열
	 * @return 복호화된 데이터를 가진 새로운 IdRequest 객체
	 * @throws Exception 복호화 과정에서 오류가 발생할 경우 발생하는 예외
	 */
	private IdRequest decryptAES(IdRequest idRequest, String key) throws Exception {

		// 암화화 설정 false면 그대로 리턴
		if (!encryptionEnabled)
			return idRequest;

		IdRequest decryptIdRequest = new IdRequest();

		decryptIdRequest.setBirthDay(decryptAES(idRequest.getBirthDay(), key));
		decryptIdRequest.setSubCode(decryptAES(idRequest.getSubCode(), key));
		decryptIdRequest.setUserName(decryptAES(idRequest.getUserName(), key));
		decryptIdRequest.setIssuedYmd(decryptAES(idRequest.getIssuedYmd(), key));

		return decryptIdRequest;
	}

}
