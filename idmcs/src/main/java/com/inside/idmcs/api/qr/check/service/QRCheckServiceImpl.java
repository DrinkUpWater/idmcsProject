package com.inside.idmcs.api.qr.check.service;

import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inside.idmcs.api.common.error.CustomException;
import com.inside.idmcs.api.common.error.ErrorCode;
import com.inside.idmcs.api.common.error.SuccessCode;
import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.RequestInfo;
import com.inside.idmcs.api.common.model.vo.db.QRHistory;
import com.inside.idmcs.api.common.model.vo.req.QRCheckReqVO;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.model.vo.res.QRCheckRes;
import com.inside.idmcs.api.common.model.vo.res.ResVO;
import com.inside.idmcs.api.common.util.crypto.QRCheckCrypto;
import com.inside.idmcs.api.common.util.logging.Logging;
import com.inside.idmcs.api.common.util.parser.Parser;
import com.inside.idmcs.api.common.util.qr.QR;
import com.inside.idmcs.api.common.util.validation.QRCheckValidation;
import com.inside.idmcs.api.enc.pk.dao.PublicKeyDao;
import com.inside.idmcs.api.id.check.dao.IdCheckDao;
import com.inside.idmcs.api.qr.history.dao.QRHistoryDao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QRCheckServiceImpl implements QRCheckService {
	
	private final PublicKeyDao publicKeyDao;
	private final IdCheckDao idCheckDao;
	private final Logging logging;
	private final Parser parser;
	private final QRCheckValidation validation;
	private final QRCheckCrypto crypto;
	private final QR qr;
	private final QRHistoryDao qRHistoryDao;

	public QRCheckServiceImpl(IdCheckDao idCheckDao, Logging logging, Parser parser,
			PublicKeyDao publicKeyDao, QRCheckValidation validation,
			QRCheckCrypto crypto, QR qr, QRHistoryDao qRHistoryDao) {
		this.idCheckDao = idCheckDao;
		this.logging = logging;
		this.parser = parser;
		this.publicKeyDao = publicKeyDao;
		this.validation = validation;
		this.crypto = crypto;
		this.qr = qr;
		this.qRHistoryDao = qRHistoryDao;
	}
	
	// application.properties 파일에서 encryption.enabled 값을 읽어옴
	@Value("${qr.timeout.sec}")
	private int qRSec;
	
	@Value("${qr.timeout.margin.sec}")
	private int qRMarginSec;

	
	/**
	 * QR 코드 검증 요청을 처리하고 응답 데이터를 반환하는 메서드.
	 *
	 * 이 메서드는 주어진 QRCheckReqVO 요청 객체와 HTTP 요청 정보를 사용하여 
	 * QR 코드 검증을 수행합니다. 각 단계에서 로그를 비동기적으로 저장하며, 
	 * 검증된 QR 코드와 관련된 신분 정보를 반환합니다. 
	 * 오류가 발생할 경우 CustomException을 발생시킵니다.
	 *
	 * @param reqVO QR 코드 검증을 위한 요청 데이터를 담고 있는 QRCheckReqVO 객체
	 * @param request 클라이언트의 HTTP 요청 정보가 담긴 HttpServletRequest 객체
	 * @param <T> 응답 데이터의 제네릭 타입
	 * @return QR 코드 검증 결과를 포함한 ResVO 객체
	 * @throws CustomException 다음과 같은 경우 예외를 발생시킴:
	 *                         - F104: 유효하지 않은 기관 또는 앱 정보
	 *                         - F201: 신분 정보가 존재하지 않음 (미가입 사용자)
	 *                         - F499: QR 코드 이력 업데이트 실패
	 *                         - F901: 예상치 못한 시스템 오류 발생
	 */
	@Transactional
	@Override
	public <T> ResVO<T> checkQRRequest(@Valid QRCheckReqVO reqVO, HttpServletRequest request) throws CustomException {
		
		log.info("step1. start : checkQRRequest");
		String logPk = "";
		QRHistory qrHist = new QRHistory();
		
		try {
			log.info("step2. createLogPrimaryKey");
			// logPrimaryKey 값 가져오기 (현재시간(시분초밀리초) + 4자리 난수)
			logPk = logging.createLogPrimaryKey();

			log.info("step3. saveLogAsync ({})", reqVO);
			// 로그 생성 비동기 : reqVO 정보
			logging.saveLogAsync(logPk, reqVO);

			log.info("step4. getRequestInfo ({})", request);
			//RequestInfo 생성(url, ip 등 request 정보)
			RequestInfo requestInfo = parser.getRequestInfo(request);

			log.info("step5. saveLogAsync ({})", requestInfo);
			// 로그 생성 비동기 : requestInfo 정보
			logging.saveLogAsync(logPk, requestInfo);
			
			log.info("step6. selectInstAndAppInfo (agencyToken: {}, appToken: {})", reqVO.getAgencyToken(),
					reqVO.getApplicationToken());
			// 기관앱정보 데이터베이서 select
			InstitutionAndApplicationInfo instAndAppInfo = publicKeyDao.selectInstAndAppInfo(reqVO.getAgencyToken(), reqVO.getApplicationToken());
			if (instAndAppInfo == null) 
				throw new CustomException(ErrorCode.F104);
			
			log.info("step7. inputDataToQRHistory (qrCd: {}, instAndAppInfo: {}", reqVO.getQrCd(), instAndAppInfo);
			qrHist = inputDataToQRHistory(qrHist, reqVO.getQrCd(), instAndAppInfo);
			
			log.info("step8. insertQRHistory");
			qRHistoryDao.insertQRHistory(qrHist);
			
			log.info("step9. isValid ({}, ip: {}, url: {})", instAndAppInfo, requestInfo.getReqIp(),
					requestInfo.getReqUrl());
			// 기관앱정보 유효성 검사
			validation.isValid(instAndAppInfo, requestInfo.getReqIp(), requestInfo.getReqUrl());

			log.info("step10. saveLogAsync ({})", instAndAppInfo);
			// 로그 갱신 기관앱정보 비동기
			logging.saveLogAsync(logPk, instAndAppInfo);

			log.info("step11. decryptReqVO ({})", reqVO);
			// 신분등록ReqVO 데이터 복호화
			ReqVO decryptReqVO = crypto.decryptReqVO(reqVO, instAndAppInfo.getPrivateKey());

			log.info("step12. isValid ({})", decryptReqVO);
			// 복호화된 IdCheckReqVO정보 유효성 검사
			validation.isValid(decryptReqVO);

			log.info("step13. saveLogAsync ({})", decryptReqVO);
			// 로그 갱신 reqVO 비동기
			logging.saveLogAsync(logPk, decryptReqVO);
			
			log.info("step14. decodeQRcode ({})", decryptReqVO.getQrCd());
			// QR코드 내용 해석 
			Map<String, String> qrMap = qr.decodeQRcode(decryptReqVO.getQrCd());
			
			//qrHist에 유저아이디 정보 담기
			qrHist.setEmpId(qrMap.get("userId"));
			
			log.info("step15. isValid ({})", qrMap);
			//QR코드 유효기간 만료 검사
			validation.isValid(qrMap, qRSec + qRMarginSec);
			
			log.info("step16. selectIdInfoWithQR ({})", qrHist.getEmpId());
			// 직원정보 가져오기
			IdInfo idInfo = idCheckDao.selectIdInfoWithUserId(qrHist.getEmpId());
			if (idInfo == null) 
				throw new CustomException(ErrorCode.F201);
			
			log.info("step17. saveLogAsync ({})", idInfo);
			// 로그 갱신 idInfo 비동기
			logging.saveLogAsync(logPk, idInfo);
			
			log.info("step18. checkIdInfoStatus ({})", idInfo);
			// idInfo 신분상태 유효성 검사
			validation.checkIdInfoStatus(idInfo);
			
			log.info("step19. encryptIdInfo ({}, appKey: {}, encKey: {})", idInfo, idInfo.getAppKey(), decryptReqVO.getEncKey());
			//idInfo 중 resVO로 전달해야하는 데이터 id정보는 appkey로 암호화, 앱키 단말정보는 encKey로 암호화
			idInfo = crypto.encryptIdInfo(idInfo, idInfo.getAppKey(), decryptReqVO.getEncKey());
			
			log.info("step20. updateQRHistorySuccess ({})", qrHist);
			if (qRHistoryDao.updateQRHistorySuccess(qrHist) == 0) 
				throw new CustomException(ErrorCode.F499);
			
			log.info("step21. createIdInfoRes ({})", idInfo);
			//QRCheckRes객체 생성 
			QRCheckRes qRCheckRes = createIdInfoRes(idInfo);
			
			SuccessCode sCode = SuccessCode.S00000;
			
			log.info("step22. createResVO<QRCheckRes> (sCode: {}, sMsg: {}, {})", sCode, sCode.getDescription(), qRCheckRes);
			// resVO성공 객체 생성 (idInfoRes)
			@SuppressWarnings("unchecked")
			ResVO<T> resVO = (ResVO<T>) new ResVO<QRCheckRes>(sCode.name(), sCode.getDescription(), qRCheckRes);
			
			log.info("step23. saveLogAsync ({})", resVO);
			// 로그갱신 resVO 비동기
			logging.saveLogAsync(logPk, resVO.getResultCode(), resVO.getResultMessage(), parser.toJson(resVO));
			
			log.info("step24. End : checkQRRequest");
			return resVO;
			
		} catch (CustomException e) {
			log.error("에러 : {}", e.getMessage(), e);
			// 에러 로그갱신(에러정보)
			logging.saveLogAsync(logPk, e.getErrorCode().name(), e.getMessage(), null);
			qRHistoryDao.updateQRHistoryFail(qrHist);
			
			throw e;
		} catch (SQLException e) {
			log.error("sql 에러 : {}", e.getMessage(), e);
			
			throw new CustomException(ErrorCode.F801, ErrorCode.F801.formatMessage("sql에러"));
		} catch (Exception e) {
			log.error("기타 에러 : {}", e.getMessage(), e);
			ErrorCode errorCode = ErrorCode.F901;
			// 에러 로그갱신(에러정보)
			logging.saveLogAsync(logPk, errorCode.name(), errorCode.getDescription(), null);
			qRHistoryDao.updateQRHistoryFail(qrHist);

			throw new CustomException(errorCode);
		}
	}

	/**
	 * QR 코드와 앱 번호를 사용하여 QRHistory 객체에 데이터를 설정하는 메서드.
	 *
	 * @param qrHist QRHistory 객체
	 * @param qrCd 설정할 QR 코드 문자열
	 * @param appNo 설정할 앱 번호
	 * @return 데이터가 설정된 QRHistory 객체
	 */
	private QRHistory inputDataToQRHistory(QRHistory qrHist, String qrCd, InstitutionAndApplicationInfo instAndAppInfo) {
		
		qrHist.setQrCd(qrCd);
		qrHist.setAppNo(instAndAppInfo.getAppNo());
		qrHist.setInstNo(instAndAppInfo.getInstNo());
		
		return qrHist;
	}

	/**
	 * IdInfo 객체를 기반으로 QRCheckRes 응답 객체를 생성하는 메서드.
	 *
	 * @param idInfo 신분 정보가 담긴 IdInfo 객체
	 * @return 생성된 QRCheckRes 응답 객체
	 * @throws Exception 신분 정보 응답 객체 생성 실패 시 예외를 발생시킴
	 */
	private QRCheckRes createIdInfoRes(IdInfo idInfo) throws Exception {
		
		try {
			QRCheckRes qRCheckRes = new QRCheckRes();
			
			qRCheckRes.setBirthDay(idInfo.getBirthDay());
			qRCheckRes.setSubCode(idInfo.getSubCode());
			qRCheckRes.setUserName(idInfo.getUserName());
			qRCheckRes.setIssuedYmd(idInfo.getIssuedYmd());
			qRCheckRes.setAddress(idInfo.getAddress());
			qRCheckRes.setDetailAddress(idInfo.getDetailAddress());
			qRCheckRes.setPhoto(idInfo.getPhoto());
			qRCheckRes.setIssuOrgName(idInfo.getIssuedInstNm());
			qRCheckRes.setAppKey(idInfo.getAppKey());
			qRCheckRes.setDeviceInfo(idInfo.getDeviceInfo());
			
			return qRCheckRes;
			
		} catch (Exception e) {
			log.error("신분정보조회Res 생성 실패, 신분정보: {}", idInfo);
			throw new Exception("[내부오류]신분정보조회Res 생성 실패");
		}
	}


}
