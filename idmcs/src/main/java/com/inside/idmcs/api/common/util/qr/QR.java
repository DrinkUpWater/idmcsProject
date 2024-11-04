package com.inside.idmcs.api.common.util.qr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QR {
	
	/**
	 * 주어진 사용자 ID와 현재 시간을 사용하여 QR 코드를 생성하는 메서드.
	 *
	 * 이 메서드는 사용자 ID와 현재 시간을 조합한 문자열로 QR 코드를 생성합니다.
	 * QR 코드 생성 중 오류가 발생하면 F401 오류 코드와 함께 CustomException을 던집니다.
	 *
	 * @param userId QR 코드에 포함할 사용자 ID
	 * @param size 생성할 QR 코드의 크기
	 * @return 생성된 QR 코드의 문자열 표현
	 * @throws CustomException QR 코드 생성 중 오류가 발생할 경우 F401 예외를 발생시킴
	 */
	public String createQRCodeWithId(String userId, int size) throws CustomException {
		
		try {
			//현재 시간(시분초밀리초) 가져오기
			String now = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			
			//qrContent (유저아이디_현재시간)
			String qrContent = userId + "_" + now;
			
			//qrcode생성 및 리턴
			return createQRCode(qrContent, size);
			
		} catch (Exception e) {
			
			throw new CustomException(ErrorCode.F401);
		}
	}
	
	/**
	 * QR 코드 내용을 사용하여 지정된 크기의 QR 코드를 생성하고, 
	 * Base64로 인코딩된 문자열로 반환하는 메서드.
	 *
	 * 이 메서드는 주어진 QR 코드 내용을 바탕으로 QR 코드를 생성한 후, 
	 * 이미지를 Base64 문자열로 변환하여 반환합니다.
	 *
	 * @param qrContent QR 코드에 포함할 내용
	 * @param size QR 코드의 가로와 세로 크기 (픽셀 단위)
	 * @return Base64로 인코딩된 QR 코드 이미지 문자열
	 * @throws Exception QR 코드 생성 또는 이미지 처리 중 오류가 발생할 경우 예외를 던짐
	 */
	public String createQRCode(String qrContent, int size) throws Exception {
		
		QRCodeWriter qrCodeWriter = new QRCodeWriter();

		// QR 코드 생성
		BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, size, size);

		// BitMatrix를 BufferedImage로 변환
		BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

		// BufferedImage를 ByteArrayOutputStream으로 변환
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
	        ImageIO.write(qrImage, "png", byteArrayOutputStream);

	        // ByteArrayOutputStream을 Base64로 인코딩
	        byte[] imageBytes = byteArrayOutputStream.toByteArray();
	        String qrCodeStr = Base64.getEncoder().encodeToString(imageBytes);

	        // 결과로 Base64 인코딩된 QR 코드 이미지 String 반환
	        return qrCodeStr;
	    }
		
	}
	
	/**
	 * Base64로 인코딩된 QR 코드 이미지를 디코딩하여 QR 코드 내용을 추출하고, 
	 * 이를 맵 형태로 반환하는 메서드.
	 *
	 * 이 메서드는 QR 코드 이미지에서 사용자 ID와 발행 타임스탬프를 추출하여 
	 * 키-값 쌍으로 구성된 맵으로 반환합니다. QR 코드 처리 중 오류가 발생하면 
	 * F499 오류 코드와 함께 CustomException을 던집니다.
	 *
	 * @param qrCodeStr Base64로 인코딩된 QR 코드 이미지 문자열
	 * @return QR 코드에서 추출한 사용자 ID와 발행 타임스탬프를 포함하는 맵 객체
	 * @throws Exception QR 코드 처리 중 오류가 발생할 경우 F499 예외를 발생시킴
	 */
	public Map<String, String> decodeQRcode(String qrCodeStr) throws Exception {
		
		// Base64로 인코딩된 QR 코드 이미지를 디코딩
	    byte[] imageBytes = Base64.getDecoder().decode(qrCodeStr);

	    // 바이트 배열을 BufferedImage로 변환
	    try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
	        BufferedImage qrImage = ImageIO.read(byteArrayInputStream);

	        // QR 코드 이미지로부터 BitMatrix를 추출하여 QR 코드를 읽기
	        LuminanceSource source = new BufferedImageLuminanceSource(qrImage);
	        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

	        // QR 코드를 디코딩하기 위해 QRCodeReader 사용
	        QRCodeReader qrCodeReader = new QRCodeReader();
	        Result result = qrCodeReader.decode(bitmap);
	     
	        // QR 코드에서 추출한 내용을 반환
	        String qrContent = result.getText();
	        
	        String[] arrStr = qrContent.split("_");
	        
	        Map<String, String> map = new HashMap<>();
	        map.put("userId", arrStr[0]);
	        map.put("issuedTimestamp", arrStr[1]);
	        
	        return map;
	        
	    } catch (Exception e) {
	        throw new CustomException(ErrorCode.F499);
	    }
	}

}
