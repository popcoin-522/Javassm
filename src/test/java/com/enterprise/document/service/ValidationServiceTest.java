package com.enterprise.document.service;

import com.enterprise.document.exception.ValidationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ValidationService单元测试
 */
public class ValidationServiceTest {
    
    private ValidationService validationService;
    
    @Before
    public void setUp() {
        validationService = new ValidationService();
    }
    
    // 文档编号验证测试
    @Test
    public void testValidateDocumentCode_Valid() throws ValidationException {
        // 正常情况不应抛出异常
        validationService.validateDocumentCode("DOC001");
        validationService.validateDocumentCode("文档编号123");
        validationService.validateDocumentCode("12345678901234567890"); // 20个字符
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentCode_Null() throws ValidationException {
        validationService.validateDocumentCode(null);
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentCode_Empty() throws ValidationException {
        validationService.validateDocumentCode("");
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentCode_WhitespaceOnly() throws ValidationException {
        validationService.validateDocumentCode("   ");
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentCode_TooLong() throws ValidationException {
        validationService.validateDocumentCode("123456789012345678901"); // 21个字符
    }
    
    // 工号/学号验证测试
    @Test
    public void testValidateUserId_Valid() throws ValidationException {
        validationService.validateUserId("EMP001");
        validationService.validateUserId("学号123456");
        validationService.validateUserId("12345678901234567890"); // 20个字符
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateUserId_Null() throws ValidationException {
        validationService.validateUserId(null);
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateUserId_Empty() throws ValidationException {
        validationService.validateUserId("");
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateUserId_TooLong() throws ValidationException {
        validationService.validateUserId("123456789012345678901"); // 21个字符
    }
    
    // 姓名验证测试
    @Test
    public void testValidateUserName_Valid() throws ValidationException {
        validationService.validateUserName("张三");
        validationService.validateUserName("John");
        validationService.validateUserName("张三John");
        validationService.validateUserName("张三123");
        validationService.validateUserName("1234567890"); // 10个字符
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateUserName_Null() throws ValidationException {
        validationService.validateUserName(null);
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateUserName_Empty() throws ValidationException {
        validationService.validateUserName("");
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateUserName_TooLong() throws ValidationException {
        validationService.validateUserName("12345678901"); // 11个字符
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateUserName_InvalidCharacters() throws ValidationException {
        validationService.validateUserName("张三@#$"); // 包含特殊字符
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateUserName_WithSpaces() throws ValidationException {
        validationService.validateUserName("张 三"); // 包含空格
    }
    
    // 文档名称验证测试
    @Test
    public void testValidateDocumentName_Valid() throws ValidationException {
        validationService.validateDocumentName("项目方案");
        validationService.validateDocumentName("Project Plan");
        // 创建100个字符的字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("a");
        }
        validationService.validateDocumentName(sb.toString());
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentName_Null() throws ValidationException {
        validationService.validateDocumentName(null);
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentName_Empty() throws ValidationException {
        validationService.validateDocumentName("");
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentName_TooLong() throws ValidationException {
        // 创建101个字符的字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            sb.append("a");
        }
        validationService.validateDocumentName(sb.toString());
    }
    
    // 文档类型验证测试
    @Test
    public void testValidateDocumentType_Valid() throws ValidationException {
        validationService.validateDocumentType("合同");
        validationService.validateDocumentType("Contract");
        // 创建50个字符的字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            sb.append("a");
        }
        validationService.validateDocumentType(sb.toString());
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentType_Null() throws ValidationException {
        validationService.validateDocumentType(null);
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentType_Empty() throws ValidationException {
        validationService.validateDocumentType("");
    }
    
    @Test(expected = ValidationException.class)
    public void testValidateDocumentType_TooLong() throws ValidationException {
        // 创建51个字符的字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 51; i++) {
            sb.append("a");
        }
        validationService.validateDocumentType(sb.toString());
    }
}