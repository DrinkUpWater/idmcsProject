package com.inside.idmcs.api.common.util.logging.dao;

import org.apache.ibatis.annotations.Mapper;

import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.KeyPairInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.req.IdRegistrationReqVO;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;

@Mapper
public interface LoggingDao {
	
	/**
	 * 주어진 로그 기본 키와 기관 및 애플리케이션 정보를 사용하여 로그를 갱신하는 메서드.
	 * 
	 * 이 메서드는 기존 로그 레코드를 주어진 기관 및 애플리케이션 정보로 갱신합니다.
	 * 
	 * @param logPk 갱신할 로그의 고유 식별자 (Primary Key)
	 * @param instAndAppInfo 갱신할 기관 및 애플리케이션 정보
	 * @return 갱신된 로그 레코드의 개수 (성공 시 1, 실패 시 0)
	 */
	int saveLogWithInstAndAppInfo(String logPk, InstitutionAndApplicationInfo instAndAppInfo);
	
	/**
	 * 주어진 로그 기본 키와 JSON 응답 데이터를 사용하여 로그를 갱신하는 메서드.
	 * 
	 * 이 메서드는 기존 로그 레코드를 주어진 JSON 형식의 응답 데이터로 갱신합니다.
	 * 
	 * @param logPk 갱신할 로그의 고유 식별자 (Primary Key)
	 * @param jsonRes 갱신할 JSON 응답 데이터
	 * @return 갱신된 로그 레코드의 개수 (성공 시 1, 실패 시 0)
	 */
	int saveLogWithJsonRes(String logPk, String jsonRes);
	
	/**
	 * 주어진 로그 기본 키와 키 페어 정보를 사용하여 로그를 갱신하는 메서드.
	 * 
	 * 이 메서드는 기존 로그 레코드를 주어진 공개키 및 개인키 정보로 갱신합니다.
	 * 
	 * @param logPk 갱신할 로그의 고유 식별자 (Primary Key)
	 * @param keyPairInfo 갱신할 공개키 및 개인키 정보
	 * @return 갱신된 로그 레코드의 개수 (성공 시 1, 실패 시 0)
	 */
	int saveLogWithPublicKey(String logPk, KeyPairInfo keyPairInfo);

	/**
	 * 로그에 결과 정보를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 결과 코드, 결과 메시지, JSON 응답 데이터를 사용하여 로그를 저장합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param resultCode 결과 코드
	 * @param resultMessage 결과 메시지
	 * @param jsonRes 로그에 저장할 JSON 응답 데이터
	 * @return 저장된 로그의 개수를 나타내는 정수 값 (성공적으로 저장된 경우 1, 실패 시 0)
	 */
	int saveLogWithRes(String logPk, String resultCode, String resultMessage, String jsonRes);

	/**
	 * 로그에 IdInfo 객체의 정보를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 IdInfo 객체의 데이터를 사용하여 로그를 저장합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param idInfo 로그에 저장할 IdInfo 객체
	 * @return 저장된 로그의 개수를 나타내는 정수 값 (성공적으로 저장된 경우 1, 실패 시 0)
	 */
	int saveLogWithIdInfo(String logPk, IdInfo idInfo);
	
	/**
	 * 로그에 오류 정보를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 오류 코드, 오류 메시지를 사용하여 로그를 저장합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param errorCode 오류 코드
	 * @param errorMsg 오류 메시지
	 * @return 저장된 로그의 개수를 나타내는 정수 값 (성공적으로 저장된 경우 1, 실패 시 0)
	 */
	int saveLogWithError(String logPk, String errorCode, String errorMsg);
	
	/**
	 * 로그에 RequestInfo 객체의 정보를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 RequestInfo 객체의 데이터를 사용하여 로그를 저장합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param requestInfo 로그에 저장할 RequestInfo 객체
	 * @return 저장된 로그의 개수를 나타내는 정수 값 (성공적으로 저장된 경우 1, 실패 시 0)
	 */
	int saveLogWithRequestInfo(String logPk, RequestInfo requestInfo);
	
	/**
	 * 로그에 기관 및 애플리케이션 토큰 정보를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 기관 토큰(instToken), 애플리케이션 토큰(appToken)을 사용하여 로그를 저장합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param instToken 기관 토큰
	 * @param appToken 애플리케이션 토큰
	 * @return 저장된 로그의 개수를 나타내는 정수 값 (성공적으로 저장된 경우 1, 실패 시 0)
	 */
	int saveLogWithTokens(String logPk, String instToken, String appToken);
	
	/**
	 * 로그에 응답 정보와 오류 정보를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 오류 코드, 오류 메시지, JSON 응답 데이터를 사용하여 로그를 저장합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param errorCode 오류 코드
	 * @param errorMsg 오류 메시지
	 * @param jsonRes 로그에 저장할 JSON 응답 데이터
	 * @return 저장된 로그의 개수를 나타내는 정수 값 (성공적으로 저장된 경우 1, 실패 시 0)
	 */
	int saveLogWithResVO(String logPk, String errorCode, String errorMsg, String jsonRes);

	/**
	 * 로그에 IdRegistrationReqVO 객체의 정보를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 IdRegistrationReqVO 객체의 데이터를 사용하여 로그를 저장합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param reqVO 로그에 저장할 IdRegistrationReqVO 객체
	 * @return 저장된 로그의 개수를 나타내는 정수 값 (성공적으로 저장된 경우 1, 실패 시 0)
	 */
	int saveLogWithIdRegistrationReqVO(String logPk, IdRegistrationReqVO reqVO);
	
	/**
	 * 로그에 ReqVO 객체의 정보를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 ReqVO 객체의 데이터를 사용하여 로그를 저장합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param reqVO 로그에 저장할 ReqVO 객체
	 * @return 저장된 로그의 개수를 나타내는 정수 값 (성공적으로 저장된 경우 1, 실패 시 0)
	 */
	int saveLogWithReqVO(String logPk, ReqVO reqVO);

	/**
	 * 로그에 QR 코드 정보를 저장하는 메서드.
	 *
	 * 이 메서드는 주어진 로그 기본 키와 QR 코드를 사용하여 로그를 저장합니다.
	 *
	 * @param logPk 로그 기본 키(고유 식별자)
	 * @param qrCode 로그에 저장할 QR 코드 문자열
	 * @return 저장된 로그의 개수를 나타내는 정수 값 (성공적으로 저장된 경우 1, 실패 시 0)
	 */
	int saveLogWithQRCode(String logPk, String qrCode);

}
