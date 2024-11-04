package com.inside.idmcs.api.id.terminate.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IdTerminationDao {

	/**
	 * 주어진 CI 값을 사용하여 신분 정보를 해지하는 메서드.
	 *
	 * @param ci 해지할 신분 정보의 CI 값
	 * @return 해지된 행의 개수 (성공 시 1, 실패 시 0)
	 */
	int terminateId(String ci);

	/**
	 * 주어진 사용자 ID를 사용하여 사진 정보를 해지하는 메서드.
	 *
	 * @param userId 해지할 사용자의 ID
	 * @return 해지된 행의 개수 (성공 시 1, 실패 시 0)
	 */
	int terminatePhoto(String userId);

}
