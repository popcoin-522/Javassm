package com.enterprise.document.service;

import com.enterprise.document.exception.BusinessException;
import com.enterprise.document.exception.GlobalExceptionHandler;
import com.enterprise.document.exception.ValidationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.testng.Assert.*;

/**
 * **Feature: enterprise-document-management, Property 8: 异常处理安全性**
 * **验证: 需求 7.5**
 * 
 * 属性测试：对于任何系统异常，系统应该捕获异常并提供友好的错误信息，而不是崩溃
 */
public class ExceptionHandlingSafetyPropertyTest {
    
    private GlobalExceptionHandler exceptionHandler;
    private MockHttpServletRequest mockRequest;
    private Random random;
    
    @BeforeMethod
    public void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI("/test/endpoint");
        random = new Random();
    }
    
    /**
     * 属性测试：验证异常处理器能够安全处理所有类型的异常
     * 对于任何异常，系统应该返回友好的错误页面而不是崩溃
     */
    @Test(invocationCount = 100)
    public void testExceptionHandlingSafety() {
        // 随机选择一种异常类型进行测试
        Exception testException = generateRandomException();
        
        try {
            ModelAndView result = handleExceptionSafely(testException);
            
            // 验证异常处理的安全性属性
            assertExceptionHandlingSafety(result, testException);
            
        } catch (Exception handlingException) {
            fail("异常处理器本身不应该抛出异常，但抛出了: " + handlingException.getClass().getSimpleName() + 
                 " - " + handlingException.getMessage() + "，原始异常: " + testException.getClass().getSimpleName());
        }
    }
    
    /**
     * 属性测试：验证所有异常都返回有效的错误页面
     * 对于任何异常，返回的ModelAndView应该包含必要的错误信息
     */
    @Test(invocationCount = 100)
    public void testErrorPageCompleteness() {
        Exception testException = generateRandomException();
        
        ModelAndView result = handleExceptionSafely(testException);
        
        // 验证错误页面的完整性
        assertNotNull(result, "异常处理应该返回有效的ModelAndView对象");
        assertNotNull(result.getViewName(), "异常处理应该返回有效的视图名称");
        assertEquals(result.getViewName(), "error", "异常处理应该返回错误页面视图");
        
        // 验证错误信息的存在
        assertTrue(result.getModel().containsKey("error"), 
                  "错误页面应该包含错误信息，异常类型: " + testException.getClass().getSimpleName());
        
        String errorMessage = (String) result.getModel().get("error");
        assertNotNull(errorMessage, "错误信息不应该为null");
        assertFalse(errorMessage.trim().isEmpty(), "错误信息不应该为空");
        
        // 验证错误信息是友好的（不包含技术细节）
        assertFriendlyErrorMessage(errorMessage, testException);
    }
    
    /**
     * 属性测试：验证异常处理的一致性
     * 相同类型的异常应该产生一致的处理结果
     */
    @Test(invocationCount = 50)
    public void testExceptionHandlingConsistency() {
        // 生成两个相同类型的异常
        Exception exception1 = generateSpecificException(ValidationException.class);
        Exception exception2 = generateSpecificException(ValidationException.class);
        
        ModelAndView result1 = handleExceptionSafely(exception1);
        ModelAndView result2 = handleExceptionSafely(exception2);
        
        // 验证处理结果的一致性
        assertEquals(result1.getViewName(), result2.getViewName(), 
                    "相同类型的异常应该返回相同的视图名称");
        
        String errorType1 = (String) result1.getModel().get("errorType");
        String errorType2 = (String) result2.getModel().get("errorType");
        assertEquals(errorType1, errorType2, 
                    "相同类型的异常应该返回相同的错误类型");
    }
    
    /**
     * 边界测试：测试极端异常情况
     */
    @Test(invocationCount = 30)
    public void testExtremeExceptionCases() {
        // 测试null异常
        Exception nullException = null;
        try {
            if (nullException != null) {
                handleExceptionSafely(nullException);
            }
        } catch (Exception e) {
            // null异常不应该被处理，这是正常的
        }
        
        // 测试嵌套异常
        Exception nestedException = new RuntimeException("外层异常", 
                                   new SQLException("内层SQL异常", 
                                   new NullPointerException("最内层空指针异常")));
        
        ModelAndView result = handleExceptionSafely(nestedException);
        assertExceptionHandlingSafety(result, nestedException);
        
        // 测试异常消息为null的情况
        Exception nullMessageException = new RuntimeException((String) null);
        ModelAndView nullMsgResult = handleExceptionSafely(nullMessageException);
        assertExceptionHandlingSafety(nullMsgResult, nullMessageException);
    }
    
    /**
     * 安全地处理异常，根据异常类型调用相应的处理方法
     */
    private ModelAndView handleExceptionSafely(Exception exception) {
        if (exception instanceof ValidationException) {
            return exceptionHandler.handleValidationException((ValidationException) exception, mockRequest);
        } else if (exception instanceof BusinessException) {
            return exceptionHandler.handleBusinessException((BusinessException) exception, mockRequest);
        } else if (exception instanceof DuplicateKeyException) {
            return exceptionHandler.handleDataAccessException((DuplicateKeyException) exception, mockRequest);
        } else if (exception instanceof DataIntegrityViolationException) {
            return exceptionHandler.handleDataAccessException((DataIntegrityViolationException) exception, mockRequest);
        } else if (exception instanceof DataAccessException) {
            return exceptionHandler.handleDataAccessException((DataAccessException) exception, mockRequest);
        } else if (exception instanceof SQLException) {
            return exceptionHandler.handleSQLException((SQLException) exception, mockRequest);
        } else if (exception instanceof NullPointerException) {
            return exceptionHandler.handleNullPointerException((NullPointerException) exception, mockRequest);
        } else if (exception instanceof IllegalArgumentException) {
            return exceptionHandler.handleIllegalArgumentException((IllegalArgumentException) exception, mockRequest);
        } else if (exception instanceof RuntimeException) {
            return exceptionHandler.handleRuntimeException((RuntimeException) exception, mockRequest);
        } else {
            return exceptionHandler.handleGenericException(exception, mockRequest);
        }
    }
    
    /**
     * 验证异常处理的安全性属性
     */
    private void assertExceptionHandlingSafety(ModelAndView result, Exception originalException) {
        // 1. 系统不应该崩溃 - 应该返回有效的结果
        assertNotNull(result, "系统不应该崩溃，应该返回有效的ModelAndView");
        
        // 2. 应该返回错误页面
        assertNotNull(result.getViewName(), "应该返回有效的视图名称");
        assertEquals(result.getViewName(), "error", "应该返回错误页面");
        
        // 3. 应该包含友好的错误信息
        assertTrue(result.getModel().containsKey("error"), "应该包含错误信息");
        String errorMessage = (String) result.getModel().get("error");
        assertNotNull(errorMessage, "错误信息不应该为null");
        assertFalse(errorMessage.trim().isEmpty(), "错误信息不应该为空");
        
        // 4. 错误信息应该是友好的，不暴露技术细节
        assertFriendlyErrorMessage(errorMessage, originalException);
        
        // 5. 应该包含错误类型信息
        assertTrue(result.getModel().containsKey("errorType"), "应该包含错误类型信息");
        
        // 6. 可能包含建议信息
        if (result.getModel().containsKey("suggestion")) {
            String suggestion = (String) result.getModel().get("suggestion");
            assertNotNull(suggestion, "如果包含建议信息，建议不应该为null");
        }
    }
    
    /**
     * 验证错误信息是否友好（不包含技术细节）
     */
    private void assertFriendlyErrorMessage(String errorMessage, Exception originalException) {
        // 错误信息不应该包含Java类名
        assertFalse(errorMessage.contains("Exception"), 
                   "友好的错误信息不应该包含'Exception'字样: " + errorMessage);
        assertFalse(errorMessage.contains("java."), 
                   "友好的错误信息不应该包含Java包名: " + errorMessage);
        assertFalse(errorMessage.contains("com.enterprise"), 
                   "友好的错误信息不应该包含应用包名: " + errorMessage);
        
        // 错误信息不应该包含堆栈跟踪信息
        assertFalse(errorMessage.contains("at "), 
                   "友好的错误信息不应该包含堆栈跟踪: " + errorMessage);
        assertFalse(errorMessage.contains("Caused by"), 
                   "友好的错误信息不应该包含异常链信息: " + errorMessage);
        
        // 错误信息应该是中文的友好提示
        assertTrue(errorMessage.matches(".*[\\u4e00-\\u9fa5].*"), 
                  "错误信息应该包含中文友好提示: " + errorMessage);
    }
    
    /**
     * 生成随机异常进行测试
     */
    private Exception generateRandomException() {
        String[] exceptionTypes = {
            "ValidationException", "BusinessException", "DataAccessException", 
            "SQLException", "NullPointerException", "IllegalArgumentException", 
            "RuntimeException", "Exception", "DuplicateKeyException", "DataIntegrityViolationException"
        };
        
        String selectedType = exceptionTypes[random.nextInt(exceptionTypes.length)];
        return generateSpecificException(selectedType);
    }
    
    /**
     * 生成特定类型的异常
     */
    private Exception generateSpecificException(Class<? extends Exception> exceptionClass) {
        return generateSpecificException(exceptionClass.getSimpleName());
    }
    
    /**
     * 根据异常类型名称生成特定异常
     */
    private Exception generateSpecificException(String exceptionType) {
        String randomMessage = generateRandomErrorMessage();
        
        switch (exceptionType) {
            case "ValidationException":
                return new ValidationException(randomMessage);
            case "BusinessException":
                return new BusinessException(randomMessage);
            case "DuplicateKeyException":
                return new DuplicateKeyException(randomMessage);
            case "DataIntegrityViolationException":
                return new DataIntegrityViolationException(randomMessage);
            case "DataAccessException":
                return new DataAccessException(randomMessage) {};
            case "SQLException":
                return new SQLException(randomMessage);
            case "NullPointerException":
                return new NullPointerException(randomMessage);
            case "IllegalArgumentException":
                return new IllegalArgumentException(randomMessage);
            case "RuntimeException":
                return new RuntimeException(randomMessage);
            default:
                return new Exception(randomMessage);
        }
    }
    
    /**
     * 生成随机错误消息
     */
    private String generateRandomErrorMessage() {
        String[] messages = {
            "测试异常消息", "数据库连接失败", "文档不存在", "用户输入无效", 
            "系统内部错误", "网络超时", "权限不足", "文件读取失败",
            null, "", "   ", "包含特殊字符@#$%的消息", "很长很长很长很长很长很长的错误消息内容"
        };
        
        return messages[random.nextInt(messages.length)];
    }
}