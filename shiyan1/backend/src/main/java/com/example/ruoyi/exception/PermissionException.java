package com.example.ruoyi.exception;

/**
 * 权限异常
 */
public class PermissionException extends RuntimeException {

    public PermissionException(String message) {
        super(message);
    }
}