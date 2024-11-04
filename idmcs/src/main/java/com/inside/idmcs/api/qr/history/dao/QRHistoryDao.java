package com.inside.idmcs.api.qr.history.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;
import com.inside.idmcs.api.common.model.dto.PageInfo;
import com.inside.idmcs.api.common.model.vo.db.QRHistory;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.api.common.model.vo.res.QRCheckHistory;



@Mapper
public interface QRHistoryDao {

	/**
	 * 주어진 요청 정보를 기반으로 QR 코드 이력의 개수를 조회하는 메서드.
	 *
	 * @param reqVO 조회 조건이 담긴 ReqVO 객체
	 * @param appNo 애플리케이션 번호
	 * @return 해당 조건에 맞는 QR 코드 이력의 개수
	 */
	int getCountHistoryQR(ReqVO reqVO, InstitutionAndApplicationInfo instAndAppInfo);

	/**
	 * 주어진 요청 정보를 기반으로 QR 코드 이력 목록을 조회하는 메서드.
	 *
	 * @param reqVO 조회 조건이 담긴 ReqVO 객체
	 * @param pageInfo 페이징 정보가 담긴 PageInfo 객체
	 * @return QRCheckHistory 객체의 리스트
	 */
	List<QRCheckHistory> selectQRHistoryList(ReqVO reqVO, PageInfo pageInfo, InstitutionAndApplicationInfo instAndAppInfo);

	/**
	 * QR 코드 이력을 데이터베이스에 삽입하는 메서드.
	 *
	 * @param qrHist 삽입할 QRHistory 객체
	 * @return 삽입된 이력의 기본 키 (성공 시, 실패 시 0)
	 */
	long insertQRHistory(QRHistory qrHist);

	/**
	 * QR 코드 이력의 상태를 성공으로 업데이트하는 메서드.
	 *
	 * @param qrHist 업데이트할 QRHistory 객체
	 * @return 업데이트된 행의 개수 (성공 시 1, 실패 시 0)
	 */
	int updateQRHistorySuccess(QRHistory qrHist);
	
	/**
	 * QR 코드 이력의 상태를 실패로 업데이트하는 메서드.
	 *
	 * @param qrHist 업데이트할 QRHistory 객체
	 * @return 업데이트된 행의 개수 (성공 시 1, 실패 시 0)
	 */
	int updateQRHistoryFail(QRHistory qrHist);

}
