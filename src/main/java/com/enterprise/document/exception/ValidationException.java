package com.enterprise.document.exception;

/**
 * 输入验证异常类
 * 用于处理用户输入验证失败的情况
 */
public class ValidationException extends Exception {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}