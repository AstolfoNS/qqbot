package com.timeleafing.qqbot.exception;

public class MinioFileUrlExtractException extends RuntimeException {

    public MinioFileUrlExtractException() {
        super();
    }

    public MinioFileUrlExtractException(String message) {
        super(message);
    }

    public MinioFileUrlExtractException(String message, Throwable cause) {
        super(message, cause);
    }

    public MinioFileUrlExtractException(Throwable cause) {
        super(cause);
    }
}
