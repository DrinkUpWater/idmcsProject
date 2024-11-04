package com.inside.idmcs.api.common.util.crypto;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.MobileRequest;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.util.parser.Parser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Crypto {

	protected final Parser parser;

	public Crypto(Parser parser) {
		this.parser = parser;
	}

	// application.properties 파일에서 encryption.enabled 값을 읽어옴
	@Value("${encryption.enabled}")
	protected boolean encryptionEnabled;

	/**
	 * RSA 알고리즘을 사용하여 암호화된 문자열을 복호화하는 메서드.
	 *
	 * 이 메서드는 Base64로 인코딩된 개인키를 사용하여 RSA 복호화를 수행합니다.
	 * 복호화가 비활성화된 경우, 입력된 값을 그대로 반환합니다.
	 *
	 * @param value 복호화할 Base64로 인코딩된 암호화된 문자열
	 * @param key Base64로 인코딩된 RSA 개인키 문자열
	 * @return 복호화된 문자열
	 * @throws Exception 키 생성 또는 복호화 중 오류가 발생할 경우 발생하는 예외
	 */
	public String decryptRSA(String value, String key) throws Exception {

		// 암화화 설정 false면 그대로 리턴
		if (!encryptionEnabled)
			return value;

		// Base64로 인코딩된 개인키 문자열을 디코딩
		byte[] privateKeyBytes = Base64.getDecoder().decode(key);

		// PKCS8EncodedKeySpec을 사용하여 PrivateKey 객체 생성
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

		// Base64로 인코딩된 암호화된 encKey를 디코딩
		byte[] encryptedBytes = Base64.getDecoder().decode(value);

		// RSA 암호화 알고리즘을 사용하여 복호화하는 Cipher 객체 생성
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		// 암호화된 encKey 복호화
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

		// 복호화된 바이트 배열을 문자열로 변환하여 반환
		return new String(decryptedBytes, "UTF-8");

	}

	/**
	 * AES 알고리즘을 사용하여 암호화된 문자열을 복호화하는 메서드.
	 *
	 * 이 메서드는 주어진 AES 키를 사용하여 Base64로 인코딩된 문자열을 복호화합니다.
	 * 복호화가 비활성화된 경우, 입력된 값을 그대로 반환합니다.
	 *
	 * @param value 복호화할 Base64로 인코딩된 암호화된 문자열
	 * @param key AES 복호화에 사용할 키 문자열
	 * @return 복호화된 문자열
	 * @throws Exception 키 생성 또는 복호화 중 오류가 발생할 경우 발생하는 예외
	 */
	public String decryptAES(String value, String key) throws Exception {

		// 암호화 설정이 비활성화되어 있으면, 입력 값을 그대로 반환
		if (!encryptionEnabled)
			return value;

		// AES 키를 바이트 배열로 변환
		byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

		// Cipher 객체를 AES 알고리즘으로 초기화 (복호화 모드)
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

		// Base64로 인코딩된 문자열을 바이트 배열로 변환
		byte[] encryptedBytes = Base64.getDecoder().decode(value);

		// AES 복호화 수행
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

		// 복호화된 바이트 배열을 문자열로 변환하여 반환
		return new String(decryptedBytes, StandardCharsets.UTF_8);
	}

	/**
	 * AES 알고리즘을 사용하여 문자열을 암호화하는 메서드.
	 *
	 * 이 메서드는 주어진 AES 키를 사용하여 입력된 문자열을 암호화하고,
	 * 암호화된 데이터를 Base64로 인코딩하여 반환합니다.
	 * 암호화가 비활성화된 경우, 입력된 값을 그대로 반환합니다.
	 *
	 * @param value 암호화할 문자열
	 * @param key AES 암호화에 사용할 키 문자열
	 * @return 암호화된 문자열 (Base64 인코딩)
	 * @throws Exception 키 생성 또는 암호화 중 오류가 발생할 경우 발생하는 예외
	 */

	public String encryptAES(String value, String key) throws Exception {

		if (value == null)
			return null;

		// 암호화 설정이 비활성화되어 있으면, 입력 값을 그대로 반환
		if (!encryptionEnabled)
			return value;

		// AES 키를 바이트 배열로 변환
		byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
		SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

		// Cipher 객체를 AES 알고리즘으로 초기화 (암호화 모드)
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

		// 암호화할 문자열을 바이트 배열로 변환
		byte[] plainTextBytes = value.getBytes(StandardCharsets.UTF_8);

		// AES 암호화 수행
		byte[] encryptedBytes = cipher.doFinal(plainTextBytes);

		// 암호화된 바이트 배열을 Base64로 인코딩하여 문자열로 반환
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}
	
	/**
	 * AES 알고리즘을 사용하여 MobileRequest 객체의 특정 필드를 복호화하는 메서드.
	 *
	 * 이 메서드는 주어진 AES 키를 사용하여 MobileRequest 객체의 암호화된 필드를 복호화합니다.
	 * 복호화가 비활성화된 경우, 원본 MobileRequest 객체를 그대로 반환합니다.
	 *
	 * @param mobileRequest 복호화할 MobileRequest 객체
	 * @param key AES 복호화에 사용할 키 문자열
	 * @return 복호화된 필드를 가진 새로운 MobileRequest 객체
	 * @throws Exception 키 생성 또는 복호화 중 오류가 발생할 경우 발생하는 예외
	 */
	public MobileRequest decryptAES(MobileRequest mobileRequest, String key) throws Exception {

		// 암화화 설정 false면 그대로 리턴
		if (!encryptionEnabled)
			return mobileRequest;

		MobileRequest encryptMobileRequest = new MobileRequest();
		
		if (mobileRequest.getCi() != null)
			encryptMobileRequest.setCi(decryptAES(mobileRequest.getCi(), key));
		
		if (mobileRequest.getAppKey() != null)
			encryptMobileRequest.setAppKey(decryptAES(mobileRequest.getAppKey(), key));
		
		if (mobileRequest.getMobileNo() != null)
		encryptMobileRequest.setMobileNo(decryptAES(mobileRequest.getMobileNo(), key));
		
		if (mobileRequest.getDeviceInfo() != null)
		encryptMobileRequest.setDeviceInfo(decryptAES(mobileRequest.getDeviceInfo(), key));
		
		if (mobileRequest.getTelecom() != null)
			encryptMobileRequest.setTelecom(mobileRequest.getTelecom());
		
		return encryptMobileRequest;

	}
	
	/**
	 * RSA 및 AES 알고리즘을 사용하여 ReqVO 객체와 그 내부의 MobileRequest 객체를 복호화하는 메서드.
	 *
	 * 이 메서드는 주어진 RSA 개인키와 AES 키를 사용하여 ReqVO 객체의 encKey와
	 * 내부 MobileRequest 데이터를 복호화합니다. 복호화가 비활성화된 경우,
	 * 입력된 ReqVO 객체를 그대로 반환합니다.
	 *
	 * @param reqVO 복호화할 ReqVO 객체
	 * @param privateKey RSA 복호화에 사용할 개인키 문자열
	 * @return 복호화된 데이터를 가진 새로운 ReqVO 객체
	 * @throws CustomException 복호화 과정에서 오류가 발생할 경우 F109 예외를 발생시킴
	 */
	public ReqVO decryptReqVO(ReqVO reqVO, String privateKey) throws CustomException {
		if (!encryptionEnabled) // 암호화 설정 false면 그대로 리턴
			return reqVO;

		try {
			// 복화화ReqVO 생성 reqVO 정보 담아서
			ReqVO decrytReqVO = parser.inputDataToReqVO(reqVO);
			
			// encKey RSA 복화화
			String encKey = decryptRSA(reqVO.getEncKey(), privateKey);
			decrytReqVO.setEncKey(encKey);

			// ReqVO의 MobileRequest 정보 파싱
			MobileRequest mobileRequest = parser.getMoblieRequest(reqVO);

			// MobileRequest 정보 AES 복호화(encKey)
			MobileRequest decrytMobileRequest = decryptAES(mobileRequest, encKey);

			// MobileRequest 정보 복화화ReqVO객체에 담기
			decrytReqVO = parser.inputDataToReqVO(decrytReqVO, decrytMobileRequest);

			return decrytReqVO;

		} catch (Exception e) {
			throw new CustomException(ErrorCode.F109);
		}

	}
	
	/**
	 * AES 알고리즘을 사용하여 IdInfo 객체의 필드들을 암호화하는 메서드.
	 *
	 * 이 메서드는 주어진 AES 키(appKey)를 사용하여 IdInfo 객체의 여러 필드를 암호화합니다.
	 * 암호화가 비활성화된 경우, 입력된 IdInfo 객체를 그대로 반환합니다.
	 *
	 * @param idInfo 암호화할 IdInfo 객체
	 * @param appKey AES 암호화에 사용할 키 문자열
	 * @return 암호화된 데이터를 가진 새로운 IdInfo 객체
	 * @throws CustomException 암호화 과정에서 오류가 발생할 경우 F110 예외를 발생시킴
	 */
	public IdInfo encryptAESInfo(IdInfo idInfo, String appKey) throws CustomException {

		if (!encryptionEnabled) // 암호화 설정 false면 그대로 리턴
			return idInfo;

		try {
			IdInfo encryptIdInfo = new IdInfo();

			encryptIdInfo.setBirthDay(encryptAES(idInfo.getBirthDay(), appKey));
			encryptIdInfo.setSubCode(encryptAES(idInfo.getSubCode(), appKey));
			encryptIdInfo.setUserName(encryptAES(idInfo.getUserName(), appKey));
			encryptIdInfo.setIssuedYmd(encryptAES(idInfo.getIssuedYmd(), appKey));
			encryptIdInfo.setAddress(encryptAES(idInfo.getAddress(), appKey));
			encryptIdInfo.setDetailAddress(encryptAES(idInfo.getDetailAddress(), appKey));
			encryptIdInfo.setPhoto(encryptAES(idInfo.getPhoto(), appKey));
			encryptIdInfo.setIssuedInstNm(encryptAES(idInfo.getIssuedInstNm(), appKey));

			return encryptIdInfo;

		} catch (Exception e) {
			throw new CustomException(ErrorCode.F110);
		}

	}

}
