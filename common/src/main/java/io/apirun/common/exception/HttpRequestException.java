package io.apirun.common.exception;

public class HttpRequestException extends BaseException {

	private static final long serialVersionUID = -5511008923884007234L;

	
	public HttpRequestException(String msg) {
		super(msg);
	}
	
	public HttpRequestException(String msg, Exception e) {
		super(msg, e);
	}
}
