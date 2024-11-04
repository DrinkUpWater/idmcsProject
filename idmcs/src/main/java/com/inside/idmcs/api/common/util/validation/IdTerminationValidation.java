package com.inside.idmcs.api.common.util.validation;

import org.springframework.stereotype.Component;

import com.inside.idmcs.api.common.util.parser.Parser;

@Component
public class IdTerminationValidation extends IdValidation {

	public IdTerminationValidation(Parser parser) {
		super(parser);
	}
}
