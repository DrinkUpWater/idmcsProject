package com.inside.idmcs.test;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.springframework.stereotype.Service;

import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.util.crypto.Crypto;
import com.inside.idmcs.api.enc.pk.dao.PublicKeyDao;
import com.inside.idmcs.test.model.RetData;
import com.inside.idmcs.test.model.TestApiReqCi;
import com.inside.idmcs.test.model.TestApiRequestBody;

@Service
public class TestApiServiceImpl implements TestApiService {

	private final Crypto crypto;
	private final PublicKeyDao publickeyDao;

	public TestApiServiceImpl(Crypto crypto, PublicKeyDao publicKeyDao) {
		this.crypto = crypto;
		this.publickeyDao = publicKeyDao;
	}

	@Override
	public IdInfo selectIdinfoApi(TestApiReqCi reqVO) {

		String ci = reqVO.getCi();

		if (ci.equals("9R2Sa1A4WByuSDScjY7jBXhNilCimE6RNCNqVU8Q0QzY5fvFPbKxgAtXK5WwFIIFyZQxh29QDGLVYIcILnX5HIf8")) {
			IdInfo idInfo = new IdInfo();

			idInfo.setUserId("user01");
			idInfo.setBirthDay("19990909");
			idInfo.setSubCode("1090909");
			idInfo.setUserName("김공구");
			idInfo.setIssuedYmd("20240101");
			idInfo.setCi("9R2Sa1A4WByuSDScjY7jBXhNilCimE6RNCNqVU8Q0QzY5fvFPbKxgAtXK5WwFIIFyZQxh29QDGLVYIcILnX5HIf8");
			idInfo.setAddress("서울시 강남구 공구동");
			idInfo.setDetailAddress("공구아파트 9동 909호");
			idInfo.setMobileNo("01009090909");
			idInfo.setIssuedInstNm("inst01");
			idInfo.setPhoto("photodata01");
			idInfo.setAppKey("MyVC8d1LtFA5BymW0EXF20V0rioYt5kY");
			idInfo.setTelecom("S");
			idInfo.setDeviceInfo(
					"9ELl946Hz2dZy8Vt0tg7ReUlbjNxvsmErKWXPfCPq0OUkBJ1EdweFmGgUEr1NnIILeeP0P6YvzCn9qFyrAu7CeQp6SWdeiVZlmtup4jN8t0ttLWl9eMWGt7eYHus4IRtmxLYkIBcPMAOZmCqlh0dsLTccYmIPTjEY1zoRFpelOVdaztFHesTWQFy0YVvW9edaiTRr3EhMsckAqBmR9vfRsgoDrDG5vRSq8tRX5g7O3Ru4oa6dsi2KI8ULMZGDQJmn3AupeeSM20TrUmc7WnVrm4XfivTzK7PFgsmEYuP2URo5PXOeCJcFyg17dj8MD9IWHGN89eVQfRrEVhBep0wZ3RFTpo4YjxCOrSpLdFe0J7rqZGa6vycVLnYz4WmNlOWIVhfRpMTYXzFQWXPT8cHhN4vIan3ZoLCBsJ5u3cB1jVmAo0f1WtG6QwW6RApIxt5ZaTxJ3K8HcO5l9oAdqftGT99yFnLZPCBD0f21eFuvAR6K6AzrhMgLoF49uIdKR8r");
			idInfo.setStatus("재직");// 휴직, 퇴직
			idInfo.setIdStatus("정상"); // 훼손, 분실

			return idInfo;
		} 
		
		if (ci.equals("202Sa1A4WByuSDScjY7jBXhNilCimE6RNCNqVU8Q0QzY5fvFPbKxgAtXK5WwFIIFyZQxh29QDGLVYIcILnX5HIf8")) {
			IdInfo idInfo = new IdInfo();

			idInfo.setUserId("user02");
			idInfo.setBirthDay("19990202");
			idInfo.setSubCode("2020202");
			idInfo.setUserName("이공이");
			idInfo.setIssuedYmd("20240202");
			idInfo.setCi("202Sa1A4WByuSDScjY7jBXhNilCimE6RNCNqVU8Q0QzY5fvFPbKxgAtXK5WwFIIFyZQxh29QDGLVYIcILnX5HIf8");
			idInfo.setAddress("서울시 이공구 이공동");
			idInfo.setDetailAddress("이공이아파트 2동 202호");
			idInfo.setMobileNo("01002020202");
			idInfo.setIssuedInstNm("inst01");
			idInfo.setPhoto("photodata02");
			idInfo.setAppKey("MyVC8d1LtFA5BymW0EXF20V0rioYt5kY");
			idInfo.setTelecom("K");
			idInfo.setDeviceInfo(
					"2020946Hz2dZy8Vt0tg7ReUlbjNxvsmErKWXPfCPq0OUkBJ1EdweFmGgUEr1NnIILeeP0P6YvzCn9qFyrAu7CeQp6SWdeiVZlmtup4jN8t0ttLWl9eMWGt7eYHus4IRtmxLYkIBcPMAOZmCqlh0dsLTccYmIPTjEY1zoRFpelOVdaztFHesTWQFy0YVvW9edaiTRr3EhMsckAqBmR9vfRsgoDrDG5vRSq8tRX5g7O3Ru4oa6dsi2KI8ULMZGDQJmn3AupeeSM20TrUmc7WnVrm4XfivTzK7PFgsmEYuP2URo5PXOeCJcFyg17dj8MD9IWHGN89eVQfRrEVhBep0wZ3RFTpo4YjxCOrSpLdFe0J7rqZGa6vycVLnYz4WmNlOWIVhfRpMTYXzFQWXPT8cHhN4vIan3ZoLCBsJ5u3cB1jVmAo0f1WtG6QwW6RApIxt5ZaTxJ3K8HcO5l9oAdqftGT99yFnLZPCBD0f21eFuvAR6K6AzrhMgLoF49uIdKR8r");
			idInfo.setStatus("재직");// 재직, 휴직, 퇴직
			idInfo.setIdStatus("정상"); // 정상, 훼손, 분실

			return idInfo;
		}
		
		if (ci.equals("303Sa1A4WByuSDScjY7jBXhNilCimE6RNCNqVU8Q0QzY5fvFPbKxgAtXK5WwFIIFyZQxh29QDGLVYIcILnX5HIf8")) {
			IdInfo idInfo = new IdInfo();

			idInfo.setUserId("user03");
			idInfo.setBirthDay("20030303");
			idInfo.setSubCode("3030303");
			idInfo.setUserName("박공삼");
			idInfo.setIssuedYmd("20240303");
			idInfo.setCi("303Sa1A4WByuSDScjY7jBXhNilCimE6RNCNqVU8Q0QzY5fvFPbKxgAtXK5WwFIIFyZQxh29QDGLVYIcILnX5HIf8");
			idInfo.setAddress("서울시 공삼구 공삼동");
			idInfo.setDetailAddress("공삼아파트 3동 303호");
			idInfo.setMobileNo("01003030303");
			idInfo.setIssuedInstNm("inst01");
			idInfo.setPhoto("photodata03");
			idInfo.setAppKey("MyVC8d1LtFA5BymW0EXF20V0rioYt5kY");
			idInfo.setTelecom("L");
			idInfo.setDeviceInfo(
					"3030946Hz2dZy8Vt0tg7ReUlbjNxvsmErKWXPfCPq0OUkBJ1EdweFmGgUEr1NnIILeeP0P6YvzCn9qFyrAu7CeQp6SWdeiVZlmtup4jN8t0ttLWl9eMWGt7eYHus4IRtmxLYkIBcPMAOZmCqlh0dsLTccYmIPTjEY1zoRFpelOVdaztFHesTWQFy0YVvW9edaiTRr3EhMsckAqBmR9vfRsgoDrDG5vRSq8tRX5g7O3Ru4oa6dsi2KI8ULMZGDQJmn3AupeeSM20TrUmc7WnVrm4XfivTzK7PFgsmEYuP2URo5PXOeCJcFyg17dj8MD9IWHGN89eVQfRrEVhBep0wZ3RFTpo4YjxCOrSpLdFe0J7rqZGa6vycVLnYz4WmNlOWIVhfRpMTYXzFQWXPT8cHhN4vIan3ZoLCBsJ5u3cB1jVmAo0f1WtG6QwW6RApIxt5ZaTxJ3K8HcO5l9oAdqftGT99yFnLZPCBD0f21eFuvAR6K6AzrhMgLoF49uIdKR8r");
			idInfo.setStatus("휴직");// 재직, 휴직, 퇴직
			idInfo.setIdStatus("정상"); // 정상, 훼손, 분실

			return idInfo;
		}
		
		return null;
	}
	
	@Override
	public ReqVO encryptoDataApi(ReqVO reqVO) throws Exception {

		InstitutionAndApplicationInfo instAndAppInfo = publickeyDao.selectInstAndAppInfo(reqVO.getAgencyToken(),
				reqVO.getApplicationToken());

		//appKey로 암호화
		reqVO.setBirthDay(crypto.encryptAES(reqVO.getBirthDay(), reqVO.getAppKey()));
		reqVO.setSubCode(crypto.encryptAES(reqVO.getSubCode(), reqVO.getAppKey()));
		reqVO.setUserName(crypto.encryptAES(reqVO.getUserName(), reqVO.getAppKey()));
		reqVO.setIssuedYmd(crypto.encryptAES(reqVO.getIssuedYmd(), reqVO.getAppKey()));
		
		//encKey로 암호화
		reqVO.setCi(crypto.encryptAES(reqVO.getCi(), reqVO.getEncKey()));
		reqVO.setAppKey(crypto.encryptAES(reqVO.getAppKey(), reqVO.getEncKey()));
		reqVO.setMobileNo(crypto.encryptAES(reqVO.getMobileNo(), reqVO.getEncKey()));
		reqVO.setDeviceInfo(crypto.encryptAES(reqVO.getDeviceInfo(), reqVO.getEncKey()));
		
		//encKey publicKey로 암호화
		reqVO.setEncKey(encryptRSA(reqVO.getEncKey(), instAndAppInfo.getPublicKey()));

		return reqVO;
	}
	
	@Override
	public IdInfo decryptoDataApi(TestApiRequestBody reqVO) throws Exception {
		
		IdInfo idInfo = new IdInfo();
		
		String appKey = reqVO.getAppKey();
		
		RetData retData = reqVO.getRetData();
		
		//appKey로 복호화
		idInfo.setBirthDay(crypto.decryptAES(retData.getBirthDay(), appKey));
		idInfo.setSubCode(crypto.decryptAES(retData.getSubCode(), appKey));
		idInfo.setUserName(crypto.decryptAES(retData.getUserName(), appKey));
		idInfo.setIssuedYmd(crypto.decryptAES(retData.getIssuedYmd(), appKey));
		idInfo.setAddress(crypto.decryptAES(retData.getAddress(), appKey));
		idInfo.setDetailAddress(crypto.decryptAES(retData.getDetailAddress(), appKey));
		idInfo.setPhoto(crypto.decryptAES(retData.getPhoto(), appKey));
		idInfo.setIssuedInstNm(crypto.decryptAES(retData.getIssuOrgName(), appKey));
		
		return idInfo;
	}

	// 암호화 RSA
	public String encryptRSA(String value, String publicKeyStr) throws Exception {

		// Base64로 인코딩된 공개 키 문자열을 디코딩
		byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);

		// X509EncodedKeySpec을 사용하여 PublicKey 객체 생성
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(keySpec);

		// RSA 암호화 알고리즘을 사용하여 암호화하는 Cipher 객체 생성
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);

		// 평문을 암호화하여 바이트 배열로 반환
		byte[] encryptedBytes = cipher.doFinal(value.getBytes("UTF-8"));

		// 암호화된 바이트 배열을 Base64 문자열로 변환하여 반환
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

}
