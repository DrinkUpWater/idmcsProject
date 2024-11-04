package com.inside.idmcs.api.common.error;

public enum SuccessCode {
	
	S00000("SUCCESS");

	private String description;

	SuccessCode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
