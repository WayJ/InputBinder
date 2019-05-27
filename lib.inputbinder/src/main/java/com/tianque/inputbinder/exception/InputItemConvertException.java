package com.tianque.inputbinder.exception;

public class InputItemConvertException extends RuntimeException {

    public InputItemConvertException() {
    }

    public InputItemConvertException(String message) {
        super(message);
    }

    public InputItemConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputItemConvertException(Throwable cause) {
        super(cause);
    }

    public InputItemConvertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
