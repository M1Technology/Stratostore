package com.m1technology.stratostore;

public class StratostoreException extends RuntimeException {
    public StratostoreException() {
        super();
    }

    public StratostoreException(String message) {
        super(message);
    }

    public StratostoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public StratostoreException(Throwable cause) {
        super(cause);
    }

    protected StratostoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
