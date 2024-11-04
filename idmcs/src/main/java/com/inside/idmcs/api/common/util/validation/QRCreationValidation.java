package com.inside.idmcs.api.common.util.validation;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class QRCreationValidation extends QRValidation{

	public QRCreationValidation(Parser parser) {
		super(parser);
	}

}
