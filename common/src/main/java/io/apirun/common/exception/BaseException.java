package io.apirun.common.exception;


public class BaseException extends RuntimeException {

	private static final long serialVersionUID = 6373803593159032662L;

	public BaseException(String message, Exception e) {
		super(message, e);
	}

	public BaseException() {
		super();
	}

	public BaseException(Exception e) {
		super(e);
	}

	public BaseException(String message) {
		super(message);
	}
}
