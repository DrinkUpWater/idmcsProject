package com.inside.idmcs.test;

import com.inside.idmcs.api.common.model.dto.IdInfo;
import com.inside.idmcs.api.common.model.vo.req.ReqVO;
import com.inside.idmcs.test.model.TestApiReqCi;
import com.inside.idmcs.test.model.TestApiRequestBody;

public interface TestApiService {
	
	IdInfo selectIdinfoApi(TestApiReqCi reqVO);

	ReqVO encryptoDataApi(ReqVO reqVO) throws Exception;

	IdInfo decryptoDataApi(TestApiRequestBody reqVO) throws Exception;

}
