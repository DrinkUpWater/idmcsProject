package com.inside.idmcs.api.id.regist.dao;

import org.apache.ibatis.annotations.Mapper;

import com.inside.idmcs.api.common.model.dto.IdInfo;

@Mapper
public interface IdRegistrationDao {

	IdInfo selectIdInfo(String ci);

	int saveIdInfo(IdInfo idInfo);

	String checkRegistrationStatus(String ci);

	int savePhoto(IdInfo idInfo);

}
