package com.example.ruoyi.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private T data;

    public R() {
    }

    public R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> R<T> success() {
        return new R<>(200, "操作成功");
    }

    public static <T> R<T> success(T data) {
        return new R<>(200, "操作成功", data);
    }

    public static <T> R<T> success(String msg, T data) {
        return new R<>(200, msg, data);
    }

    public static <T> R<T> error() {
        return new R<>(500, "操作失败");
    }

    public static <T> R<T> error(String msg) {
        return new R<>(500, msg);
    }

    public static <T> R<T> error(Integer code, String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> error(String msg, T data) {
        return new R<>(400, msg, data);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}