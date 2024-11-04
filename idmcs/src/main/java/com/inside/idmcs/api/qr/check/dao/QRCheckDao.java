package com.inside.idmcs.api.qr.check.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QRCheckDao {

	/**
	 * QR 코드 이력을 데이터베이스에 저장하는 메서드.
	 *
	 * 이 메서드는 QR 코드 스캔 또는 검증에 대한 이력을 데이터베이스에 삽입합니다.
	 *
	 * @return 삽입된 이력의 개수 (성공 시 1, 실패 시 0)
	 */
	int insertQrHistory();

}
