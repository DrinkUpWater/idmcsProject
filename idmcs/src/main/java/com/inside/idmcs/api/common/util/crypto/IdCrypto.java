package com.inside.idmcs.api.common.util.crypto;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class IdCrypto extends Crypto{

	public IdCrypto(Parser parser) {
		super(parser);
	}
	
}
