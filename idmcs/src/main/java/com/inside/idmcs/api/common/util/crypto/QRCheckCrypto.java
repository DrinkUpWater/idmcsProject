package com.inside.idmcs.api.common.util.crypto;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.util.parser.Parser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QRCheckCrypto extends QRCrypto {

	public QRCheckCrypto(Parser parser) {
		super(parser);
	}
	
	/**
	 * AES 알고리즘을 사용하여 IdInfo 객체의 필드들을 암호화하는 메서드.
	 *
	 * 이 메서드는 주어진 appKey와 encKey를 사용하여 IdInfo 객체의 데이터를 각각 암호화합니다.
	 * appKey는 신분 정보에 사용되며, encKey는 appKey와 deviceInfo를 암호화하는 데 사용됩니다.
	 * 암호화가 비활성화된 경우, 입력된 IdInfo 객체를 그대로 반환합니다.
	 *
	 * @param idInfo 암호화할 IdInfo 객체
	 * @param appKey 신분 정보를 암호화하는 데 사용할 AES 키 문자열
	 * @param encKey appKey와 deviceInfo를 암호화하는 데 사용할 AES 키 문자열
	 * @return 암호화된 데이터를 가진 새로운 IdInfo 객체
	 * @throws CustomException 암호화 과정에서 오류가 발생할 경우 F110 예외를 발생시킴
	 */
	public IdInfo encryptIdInfo(IdInfo idInfo, String appKey, String encKey) throws CustomException {

		// 암호화 설정이 비활성화되어 있으면, 입력 값을 그대로 반환
		if (!encryptionEnabled)
			return idInfo;
		
		try {
			//신분정보 AES암호화 appKey
			IdInfo encryptIdInfo = encryptAESInfo(idInfo, appKey);
			
			//appKey, deviceInfo AES암호화 encKey
			encryptIdInfo.setAppKey(encryptAES(idInfo.getAppKey(), encKey));
			encryptIdInfo.setDeviceInfo(encryptAES(idInfo.getDeviceInfo(), encKey));

			return encryptIdInfo;

		} catch (Exception e) {
			throw new CustomException(ErrorCode.F110);
		}
	}
}
