package com.inside.idmcs.api.common.error;

public class CustomException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ErrorCode errorCode;

	public CustomException(ErrorCode errorCode) {
		super(errorCode.getDescription());
		this.errorCode = errorCode;
	}

	// ErrorCode와 메시지를 받는 생성자
	public CustomException(ErrorCode errorCode, String message) {
		super(message); // RuntimeException의 생성자에 메시지를 전달
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
