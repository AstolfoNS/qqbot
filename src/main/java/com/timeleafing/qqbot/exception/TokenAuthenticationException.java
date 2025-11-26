package com.timeleafing.qqbot.exception;

public class TokenAuthenticationException extends Exception {
    public TokenAuthenticationException() {
        super();
    }

    public TokenAuthenticationException(String message) {
        super(message);
    }

    public TokenAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenAuthenticationException(Throwable cause) {
        super(cause);
    }
}
