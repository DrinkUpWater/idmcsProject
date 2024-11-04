package com.inside.idmcs.api.common.util.crypto;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.util.parser.Parser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IdCheckCrypto extends IdCrypto {
	
	public IdCheckCrypto(Parser parser) {
		super(parser);
	}

}
