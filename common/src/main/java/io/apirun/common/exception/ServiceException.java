package io.apirun.common.exception;

public class ServiceException extends RuntimeException {
    private int code;

    public ServiceException (int code, String msg) {
        super(msg);
        this.code = code;
    }

    public ServiceException (int code, String msg, Exception e) {
        super(msg, e);
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
