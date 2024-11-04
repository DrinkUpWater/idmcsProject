package com.inside.idmcs.api.id.check.dao;

import org.apache.ibatis.annotations.Mapper;

import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.vo.db.Employee;

@Mapper
public interface IdCheckDao {

	/**
	 * CI 값을 사용하여 Employee 객체를 조회하는 메서드.
	 *
	 * @param ci 조회할 직원의 CI 값
	 * @return 해당 CI 값을 가진 Employee 객체, 존재하지 않으면 null
	 */
	Employee selectEmployee(String ci);

	/**
	 * CI 값을 사용하여 IdInfo 객체를 조회하는 메서드.
	 *
	 * @param ci 조회할 IdInfo 객체의 CI 값
	 * @return 해당 CI 값을 가진 IdInfo 객체, 존재하지 않으면 null
	 */
	IdInfo selectIdInfo(String ci);

	/**
	 * 주어진 IdInfo 객체의 정보를 업데이트하는 메서드.
	 *
	 * @param idInfo 업데이트할 IdInfo 객체
	 * @return 업데이트된 행의 개수 (성공 시 1, 실패 시 0)
	 */
	int updateIdInfo(IdInfo idInfo);

	/**
	 * QR 코드와 연관된 IdInfo 객체를 조회하는 메서드.
	 *
	 * @param map QR 코드와 관련된 매개변수를 담은 맵
	 * @return 해당 QR 코드에 연관된 IdInfo 객체, 존재하지 않으면 null
	 */
	IdInfo selectIdInfoWithUserId(String userId);

	String selectPhotoData(String userId);

}
	