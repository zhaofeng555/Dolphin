package com.haojg.id.exception;

public class UidGenerateException extends RuntimeException {

    private static final long serialVersionUID = -1;

    public UidGenerateException(String message) {
        super(message);
    }

    public UidGenerateException(String msgFormat, Object... args) {
        super(String.format(msgFormat, args));
    }

    public UidGenerateException(Throwable cause) {
        super(cause);
    }

}
