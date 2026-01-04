package com.enterprise.document.service;

import com.enterprise.document.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 输入验证服务
 * 提供文档编号、工号/学号、姓名等字段的验证功能
 */
@Service
public class ValidationService {
    
    // 中英文组合的正则表达式：允许中文字符、英文字母、数字
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9]+$");
    
    /**
     * 验证文档编号
     * 要求：非空，长度不超过20个字符
     * 
     * @param documentCode 文档编号
     * @throws ValidationException 验证失败时抛出异常
     */
    public void validateDocumentCode(String documentCode) throws ValidationException {
        if (documentCode == null || documentCode.trim().isEmpty()) {
            throw new ValidationException("文档编号不能为空");
        }
        
        if (documentCode.length() > 20) {
            throw new ValidationException("文档编号长度不能超过20个字符");
        }
    }
    
    /**
     * 验证工号/学号
     * 要求：非空，长度不超过20个字符
     * 
     * @param userId 工号/学号
     * @throws ValidationException 验证失败时抛出异常
     */
    public void validateUserId(String userId) throws ValidationException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new ValidationException("工号/学号不能为空");
        }
        
        if (userId.length() > 20) {
            throw new ValidationException("工号/学号长度不能超过20个字符");
        }
    }
    
    /**
     * 验证姓名
     * 要求：中英文组合，长度不超过10个字符
     * 
     * @param userName 姓名
     * @throws ValidationException 验证失败时抛出异常
     */
    public void validateUserName(String userName) throws ValidationException {
        if (userName == null || userName.trim().isEmpty()) {
            throw new ValidationException("姓名不能为空");
        }
        
        if (userName.length() > 10) {
            throw new ValidationException("姓名长度不能超过10个字符");
        }
        
        if (!NAME_PATTERN.matcher(userName).matches()) {
            throw new ValidationException("姓名只能包含中文、英文字母和数字");
        }
    }
    
    /**
     * 验证文档名称
     * 要求：非空，长度合理
     * 
     * @param documentName 文档名称
     * @throws ValidationException 验证失败时抛出异常
     */
    public void validateDocumentName(String documentName) throws ValidationException {
        if (documentName == null || documentName.trim().isEmpty()) {
            throw new ValidationException("文档名称不能为空");
        }
        
        if (documentName.length() > 100) {
            throw new ValidationException("文档名称长度不能超过100个字符");
        }
    }
    
    /**
     * 验证文档类型
     * 要求：非空，长度合理
     * 
     * @param documentType 文档类型
     * @throws ValidationException 验证失败时抛出异常
     */
    public void validateDocumentType(String documentType) throws ValidationException {
        if (documentType == null || documentType.trim().isEmpty()) {
            throw new ValidationException("文档类型不能为空");
        }
        
        if (documentType.length() > 50) {
            throw new ValidationException("文档类型长度不能超过50个字符");
        }
    }
}