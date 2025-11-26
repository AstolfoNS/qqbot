package com.timeleafing.qqbot.exception;

public class JwtGenerateException extends RuntimeException {

    public JwtGenerateException() {
        super();
    }

    public JwtGenerateException(String message) {
        super(message);
    }

    public JwtGenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtGenerateException(Throwable cause) {
        super(cause);
    }
}
