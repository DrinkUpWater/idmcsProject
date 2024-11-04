package com.inside.idmcs.api.common.util.validation;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.MobileRequest;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.util.parser.Parser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MobileValidation extends InstitutionAndApplicationValidation {

	public MobileValidation(Parser parser) {
		super(parser);
	}

	/**
	 * 기관 및 애플리케이션 정보의 유효성을 검사하는 메서드.
	 *
	 * 이 메서드는 상위 클래스의 isValid 메서드를 호출한 후, 추가로 공개키의 유효성을 검사합니다.
	 * 공개키가 유효하지 않을 경우 F108 오류 코드와 함께 CustomException을 발생시킵니다.
	 *
	 * @param instAndAppInfo 기관 및 애플리케이션 정보가 담긴 InstitutionAndApplicationInfo 객체
	 * @param requestIp 요청을 보낸 클라이언트의 IP 주소
	 * @param requestUrl 요청을 보낸 URL
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - 상위 클래스의 유효성 검사 실패 시 발생하는 예외
	 *                         - F108: 공개키가 유효하지 않은 경우
	 */
	@Override
	public void isValid(InstitutionAndApplicationInfo instAndAppInfo, String requestIp, String requestUrl)
			throws CustomException {

		super.isValid(instAndAppInfo, requestIp, requestUrl);

		// 공개키 유효성 검사
		if (!isValid(instAndAppInfo)) {
			throw new CustomException(ErrorCode.F108);
		}

	}
	
	/**
	 * 기관 및 애플리케이션 정보의 공개키와 개인키의 유효성을 검사하는 메서드.
	 *
	 * 이 메서드는 기관 및 애플리케이션 정보에서 공개키와 개인키가 존재하는지, 
	 * 그리고 키의 유효기간이 만료되지 않았는지 확인합니다.
	 *
	 * @param instAndAppInfo 유효성을 검사할 InstitutionAndApplicationInfo 객체
	 * @return 키 정보가 유효하면 true, 그렇지 않으면 false
	 */
	public boolean isValid(InstitutionAndApplicationInfo instAndAppInfo) {
		if (isNullOrEmpty(instAndAppInfo.getPublicKey())) {
			return false;
		}
		if (isNullOrEmpty(instAndAppInfo.getPrivateKey())) {
			return false;
		}
		if (!checkExpired(instAndAppInfo.getKeyVldBgngYmd(), instAndAppInfo.getKeyVldEndYmd())) {
			return false;
		}
		return true;
	}
	
	/**
	 * MobileRequest 객체의 필드들을 유효성 검사하는 메서드.
	 *
	 * 이 메서드는 MobileRequest 객체의 필수 정보와 각 필드의 길이 및 형식을 검사합니다.
	 * 유효하지 않은 경우 적절한 오류 코드와 메시지를 포함한 CustomException을 발생시킵니다.
	 *
	 * @param mobileRequest 유효성 검사를 수행할 MobileRequest 객체
	 * @throws CustomException 다음과 같은 경우 발생:
	 *                         - F102: 필수 정보 누락
	 *                         - F103: 유효하지 않은 데이터 형식 또는 값
	 */
	public void isValid(MobileRequest mobileRequest) throws CustomException {
		// ci, appKey, mobileNo, deviceInfo, telecom 정보 유효성 검사

		if (isNullOrEmpty(mobileRequest)) {
			throw new CustomException(ErrorCode.F102, ErrorCode.F102.formatMessage("필수정보 누락"));
		}

		// ci 정보 유효성검사
		if (mobileRequest.getCi() == null || !checkSize(mobileRequest.getCi(), 88)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("고객CI"));
		}

		// appKey정보 유효성 검사
		if (mobileRequest.getAppKey() != null && !checkSize(mobileRequest.getAppKey(), 32)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("appKey"));
		}

		// mobileNo정보 유효성 검사
		if (mobileRequest.getMobileNo() != null && (!checkSize(mobileRequest.getMobileNo(), 11) || !checkMobileNo(mobileRequest.getMobileNo()))) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("핸드폰번호"));
		}

		// deviceInfo정보 유효성 검사
		if (mobileRequest.getDeviceInfo() != null && !checkSize(mobileRequest.getDeviceInfo(), 512)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("단말정보"));
		}

		// telecom정보 유효성 검사
		if (mobileRequest.getTelecom() != null && (!checkSize(mobileRequest.getTelecom(), 1)
				|| !checkTelecom(mobileRequest.getTelecom()))) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("통신사"));
		}

		// encKey정보 유효성 검사
		if (mobileRequest.getEncKey() != null && !checkSize(mobileRequest.getEncKey(), 33)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("encKey"));
		}

	}

	/**
	 * 주어진 핸드폰 번호가 유효한지 검사하는 메서드.
	 *
	 * 이 메서드는 핸드폰 번호가 0보다 큰 숫자로만 구성되어 있는지, 
	 * 또는 "01"로 시작하는지를 검사합니다.
	 *
	 * @param mobileNo 검사할 핸드폰 번호 문자열
	 * @return 번호가 유효한 경우 true, 그렇지 않으면 false
	 */
	public boolean checkMobileNo(String mobileNo) {
		
		String sub = mobileNo.substring(0, 2);
		
		//0보다 큰 숫자로 구성되어있는지 or "01"로 시작하는지
		if(!checkNumber(mobileNo, 0) || sub.equals("01")) {
			return true;
		}
		
		return false;
	}	

	/**
	 * 주어진 통신사 코드가 유효한지 검사하는 메서드.
	 *
	 * 이 메서드는 통신사 코드가 "S", "K", "L" 중 하나인지 확인합니다.
	 *
	 * @param telecom 검사할 통신사 코드
	 * @return 코드가 유효한 경우 true, 그렇지 않으면 false
	 */
	public boolean checkTelecom(String telecom) {

		if (telecom.equals("S") || telecom.equals("K") || telecom.equals("L"))
			return true;

		return false;
	}
	
	/**
	 * 주어진 ReqVO와 IdInfo 객체의 정보를 비교하여 일치 여부를 검사하는 메서드.
	 *
	 * 이 메서드는 가입 여부, 핸드폰 번호, 단말 정보, 통신사, appKey, 발급 기관의 일치 여부를 검사합니다.
	 * 불일치 시 적절한 오류 코드와 함께 CustomException을 발생시킵니다.
	 *
	 * @param reqVO 검증할 요청 정보가 담긴 ReqVO 객체
	 * @param idInfo 비교할 신분 정보가 담긴 IdInfo 객체
	 * @param instNm 검증할 기관명
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F201: 미가입 사용자
	 *                         - F202: 핸드폰 번호 불일치
	 *                         - F203: 단말 정보 불일치
	 *                         - F204: appKey 불일치
	 *                         - F205: 통신사 불일치
	 *                         - F209: 발급 기관 불일치
	 */
	public void checkUseIdInfo(ReqVO reqVO, IdInfo idInfo, String instNm) throws CustomException {

		// 미가입 사용자
		if (idInfo == null) {
			throw new CustomException(ErrorCode.F201);
		}

		// 핸드폰 번호 불일치
		if (!reqVO.getMobileNo().equals(idInfo.getMobileNo())) {
			throw new CustomException(ErrorCode.F202);
		}

		// 단말정보 불일치
		if (!reqVO.getDeviceInfo().equals(idInfo.getDeviceInfo())) {
			throw new CustomException(ErrorCode.F203);
		}

		// 통신사 불일치
		if (!reqVO.getTelecom().equals(idInfo.getTelecom())) {
			throw new CustomException(ErrorCode.F205);
		}

		// appkey 불일치
		if (!reqVO.getAppKey().equals(idInfo.getAppKey())) {
			throw new CustomException(ErrorCode.F204);
		}

		// 발급 기관 불일치
		if (!instNm.equals(idInfo.getIssuedInstNm())) {
			throw new CustomException(ErrorCode.F209);
		}

		checkIdInfoStatus(idInfo);

	}

	/**
	 * 주어진 IdInfo 객체의 상태를 검사하는 메서드.
	 *
	 * 이 메서드는 직원 상태, 신분증 상태, 그리고 신분 서비스 등록 여부를 확인합니다.
	 * 각 상태가 유효하지 않은 경우 적절한 오류 코드와 함께 CustomException을 발생시킵니다.
	 *
	 * @param idInfo 검사할 IdInfo 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F301: 비정상 직원 상태
	 *                         - F302: 비정상 신분증 상태
	 *                         - F206: 신분 서비스 해지 상태
	 */
	public void checkIdInfoStatus(IdInfo idInfo) throws CustomException {
		// 비정상 직원 상태
		if (!idInfo.getStatus().equals("재직")) {
			throw new CustomException(ErrorCode.F301);
		}

		// 비정상 신분증 상태
		if (!idInfo.getIdStatus().equals("정상")) {
			throw new CustomException(ErrorCode.F302);
		}

		// 신분서비스 해지상태 확인
		if (idInfo.getRegistYn() != null && !idInfo.getRegistYn().equals("Y")) {
			throw new CustomException(ErrorCode.F206);
		}

	}

	/**
	 * 주어진 CI와 encKey의 유효성을 검사하는 메서드.
	 *
	 * 이 메서드는 CI와 encKey의 값이 비어 있지 않은지(null 또는 empty)와 
	 * 각각의 크기 제한을 만족하는지 검사합니다. 유효하지 않은 경우 적절한 
	 * 오류 코드와 메시지를 포함한 CustomException을 발생시킵니다.
	 *
	 * @param ci 검사할 고객 CI 정보
	 * @param encKey 검사할 encKey 정보
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F102: 필수 정보 누락 (CI 또는 encKey가 비어 있을 때)
	 *                         - F103: 유효하지 않은 데이터 형식 또는 값 (CI 또는 encKey의 크기가 유효하지 않을 때)
	 */
	public void isValid(String ci, String encKey) throws CustomException {

		// ci 정보 유효성검사
		if (isNullOrEmpty(ci)) {
			throw new CustomException(ErrorCode.F102, ErrorCode.F102.formatMessage("고객CI"));
		}
		if (!checkSize(ci, 88)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("고객CI"));
		}

		// encKey정보 유효성 검사
		if (isNullOrEmpty(encKey)) {
			throw new CustomException(ErrorCode.F102, ErrorCode.F102.formatMessage("encKey"));
		}
		if (!checkSize(encKey, 33)) {
			throw new CustomException(ErrorCode.F103, ErrorCode.F103.formatMessage("encKey"));
		}
	}

	/**
	 * 주어진 ReqVO 객체의 유효성을 검사하는 메서드.
	 *
	 * 이 메서드는 ReqVO 객체에서 MobileRequest 객체를 추출하여 
	 * 해당 객체의 필드들에 대해 유효성 검사를 수행합니다. 
	 * 유효하지 않은 경우 적절한 CustomException을 발생시킵니다.
	 *
	 * @param reqVO 유효성 검사를 수행할 ReqVO 객체
	 * @throws CustomException MobileRequest 객체의 유효성 검사에 실패할 경우 발생하는 예외
	 */
	public void isValid(ReqVO reqVO) throws CustomException {

		// ci, appKey, mobileNo, deviceInfo, telecom 정보 유효성 검사
		MobileRequest mobileRequest = parser.getMoblieRequest(reqVO);
		isValid(mobileRequest);

	}

}
