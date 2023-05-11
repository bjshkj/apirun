package io.apirun.common.exception;

public class ControllerException extends RuntimeException {
    private int code;

    public ControllerException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public ControllerException(int code, String msg, Exception e) {
        super(msg, e);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
