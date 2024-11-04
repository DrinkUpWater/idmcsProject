package com.inside.idmcs.api.common.util.validation;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class PublicKeyValidation extends InstitutionAndApplicationValidation{

	public PublicKeyValidation(Parser parser) {
		super(parser);
	}
	
	
}
