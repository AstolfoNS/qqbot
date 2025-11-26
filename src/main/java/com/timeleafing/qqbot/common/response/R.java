package com.timeleafing.qqbot.common.response;

import com.timeleafing.qqbot.domain.enumeration.HttpCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;


    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(HttpCode.OK.getCode(), HttpCode.OK.getMessage(), data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(HttpCode.OK.getCode(), message, data);
    }

    public static <T> R<T> ok(HttpCode httpCode, T data) {
        return new R<>(httpCode.getCode(), httpCode.getMessage(), data);
    }

    public static <T> R<T> okWithMsg(String message) {
        return ok(message, null);
    }

    public static <T> R<T> failed() {
        return failed(null);
    }

    public static <T> R<T> failed(T data) {
        return new R<>(HttpCode.FAILED.getCode(), HttpCode.FAILED.getMessage(), data);
    }

    public static <T> R<T> failed(String message, T data) {
        return new R<>(HttpCode.FAILED.getCode(), message, data);
    }

    public static <T> R<T> failed(HttpCode httpCode, T data) {
        return new R<>(httpCode.getCode(), httpCode.getMessage(), data);
    }

    public static <T> R<T> failedWithMsg(String message) {
        return failed(message, null);
    }

}
