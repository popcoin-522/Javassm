package com.enterprise.document.exception;

/**
 * 业务逻辑异常类
 * 用于处理业务规则违反的情况
 */
public class BusinessException extends Exception {
    
    public BusinessException(String message) {
        super(message);
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}