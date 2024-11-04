package com.inside.idmcs.api.enc.pk.dao;

import org.apache.ibatis.annotations.Mapper;

import com.inside.idmcs.api.common.model.dto.InstitutionAndApplicationInfo;

@Mapper
public interface PublicKeyDao {
	
	/**
	 * 주어진 기관 토큰과 애플리케이션 토큰을 사용하여 기관 및 애플리케이션 정보를 조회하는 메서드.
	 * 
	 * 이 메서드는 데이터베이스에서 주어진 기관 토큰과 애플리케이션 토큰에 해당하는 기관 및 애플리케이션 정보를 조회하여 반환합니다.
	 * 
	 * @param agencyToken 기관을 식별하는 토큰
	 * @param applicationToken 애플리케이션을 식별하는 토큰
	 * @return 주어진 토큰에 해당하는 기관 및 애플리케이션 정보, 없으면 null 반환
	 */
	InstitutionAndApplicationInfo selectInstAndAppInfo(String agencyToken, String applicationToken);

	/**
	 * 주어진 기관 및 애플리케이션 정보와 키 페어 정보를 사용하여 애플리케이션의 키 페어 정보를 갱신하는 메서드.
	 * 
	 * 이 메서드는 데이터베이스에서 주어진 기관 및 애플리케이션 정보에 해당하는 레코드를 찾아,
	 * 해당 애플리케이션의 키 페어 정보를 갱신합니다.
	 * 
	 * @param instAndAppInfo 기관 및 애플리케이션 정보를 포함하는 객체
	 * @param keyPairInfo 갱신할 공개키 및 개인키 정보를 포함하는 객체
	 * @return 갱신된 레코드의 개수 (성공 시 1, 실패 시 0)
	 */
	int updateKeyPairInfoToApplication(InstitutionAndApplicationInfo instAndAppInfo);
	
}
