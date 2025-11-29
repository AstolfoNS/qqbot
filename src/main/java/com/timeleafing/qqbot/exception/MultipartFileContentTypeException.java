package com.timeleafing.qqbot.exception;

public class MultipartFileContentTypeException extends RuntimeException {

    public MultipartFileContentTypeException() {
        super();
    }

    public MultipartFileContentTypeException(String message) {
        super(message);
    }

    public MultipartFileContentTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipartFileContentTypeException(Throwable cause) {
        super(cause);
    }
}
