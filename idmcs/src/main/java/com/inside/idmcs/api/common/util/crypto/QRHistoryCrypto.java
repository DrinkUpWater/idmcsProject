package com.inside.idmcs.api.common.util.crypto;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class QRHistoryCrypto extends QRCrypto {

	public QRHistoryCrypto(Parser parser) {
		super(parser);
	}

}
