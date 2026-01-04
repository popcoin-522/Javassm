package com.enterprise.document.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * 全局异常处理器
 * 统一处理系统中的各种异常，提供友好的错误页面显示
 * 需求: 7.4, 7.5, 6.3, 6.4
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理输入验证异常
     * 需求: 7.4 - 输入验证失败时提示具体错误信息
     * 
     * @param e 验证异常
     * @param request HTTP请求对象
     * @return 错误页面模型和视图
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleValidationException(ValidationException e, HttpServletRequest request) {
        logger.warn("输入验证失败: {} - 请求URL: {}", e.getMessage(), request.getRequestURL());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "输入验证失败: " + e.getMessage());
        modelAndView.addObject("errorType", "validation");
        modelAndView.addObject("suggestion", "请检查输入信息是否符合要求，然后重新提交");
        
        return modelAndView;
    }
    
    /**
     * 处理业务逻辑异常
     * 需求: 7.4 - 业务规则违反时提示具体错误信息
     * 
     * @param e 业务异常
     * @param request HTTP请求对象
     * @return 错误页面模型和视图
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ModelAndView handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.warn("业务逻辑异常: {} - 请求URL: {}", e.getMessage(), request.getRequestURL());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "操作失败: " + e.getMessage());
        modelAndView.addObject("errorType", "business");
        modelAndView.addObject("suggestion", "请检查操作条件是否满足，或联系管理员获取帮助");
        
        return modelAndView;
    }
    
    /**
     * 处理数据库连接异常
     * 需求: 6.3 - 数据库连接失败时提示友好错误信息
     * 
     * @param e 数据访问异常
     * @param request HTTP请求对象
     * @return 错误页面模型和视图
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ModelAndView handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        logger.error("数据库访问异常: {} - 请求URL: {}", e.getMessage(), request.getRequestURL(), e);
        
        ModelAndView modelAndView = new ModelAndView("error");
        
        // 根据具体的数据库异常类型提供不同的错误信息
        if (e instanceof DuplicateKeyException) {
            modelAndView.addObject("error", "数据保存失败: 记录已存在，请检查输入信息");
            modelAndView.addObject("suggestion", "请检查文档编号或用户信息是否重复");
        } else if (e instanceof DataIntegrityViolationException) {
            modelAndView.addObject("error", "数据保存失败: 数据完整性约束违反");
            modelAndView.addObject("suggestion", "请检查输入数据是否符合系统要求");
        } else {
            modelAndView.addObject("error", "数据库连接失败，请稍后重试");
            modelAndView.addObject("suggestion", "系统正在维护中，请稍后重试或联系管理员");
        }
        
        modelAndView.addObject("errorType", "database");
        return modelAndView;
    }
    
    /**
     * 处理SQL异常
     * 需求: 6.4 - 数据保存失败时提示友好错误信息
     * 
     * @param e SQL异常
     * @param request HTTP请求对象
     * @return 错误页面模型和视图
     */
    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleSQLException(SQLException e, HttpServletRequest request) {
        logger.error("SQL执行异常: {} - 请求URL: {}", e.getMessage(), request.getRequestURL(), e);
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "数据保存失败，请检查数据库连接");
        modelAndView.addObject("errorType", "sql");
        modelAndView.addObject("suggestion", "请稍后重试，如问题持续存在请联系管理员");
        
        return modelAndView;
    }
    
    /**
     * 处理空指针异常
     * 需求: 7.5 - 系统异常时提供友好错误信息
     * 
     * @param e 空指针异常
     * @param request HTTP请求对象
     * @return 错误页面模型和视图
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        logger.error("空指针异常: {} - 请求URL: {}", e.getMessage(), request.getRequestURL(), e);
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "系统内部错误，请稍后重试");
        modelAndView.addObject("errorType", "system");
        modelAndView.addObject("suggestion", "请刷新页面重新尝试，如问题持续存在请联系管理员");
        
        return modelAndView;
    }
    
    /**
     * 处理非法参数异常
     * 需求: 7.4 - 输入验证失败时提示具体错误信息
     * 
     * @param e 非法参数异常
     * @param request HTTP请求对象
     * @return 错误页面模型和视图
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        logger.warn("非法参数异常: {} - 请求URL: {}", e.getMessage(), request.getRequestURL());
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "参数错误: " + e.getMessage());
        modelAndView.addObject("errorType", "parameter");
        modelAndView.addObject("suggestion", "请检查输入参数是否正确，然后重新提交");
        
        return modelAndView;
    }
    
    /**
     * 处理运行时异常
     * 需求: 7.5 - 系统异常时提供友好错误信息
     * 
     * @param e 运行时异常
     * @param request HTTP请求对象
     * @return 错误页面模型和视图
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        logger.error("运行时异常: {} - 请求URL: {}", e.getMessage(), request.getRequestURL(), e);
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "系统运行异常，请稍后重试");
        modelAndView.addObject("errorType", "runtime");
        modelAndView.addObject("suggestion", "请稍后重试，如问题持续存在请联系管理员");
        
        return modelAndView;
    }
    
    /**
     * 处理所有其他未捕获的异常
     * 需求: 7.5 - 避免程序崩溃，提供友好错误信息
     * 
     * @param e 通用异常
     * @param request HTTP请求对象
     * @return 错误页面模型和视图
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception e, HttpServletRequest request) {
        logger.error("未预期异常: {} - 请求URL: {}", e.getMessage(), request.getRequestURL(), e);
        
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "系统发生错误，请稍后重试");
        modelAndView.addObject("errorType", "general");
        modelAndView.addObject("suggestion", "请刷新页面重新尝试，如问题持续存在请联系管理员");
        
        return modelAndView;
    }
    
    /**
     * 获取异常的根本原因
     * 
     * @param throwable 异常对象
     * @return 根本原因异常
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
    
    /**
     * 判断是否为数据库连接相关异常
     * 
     * @param throwable 异常对象
     * @return 是否为连接异常
     */
    private boolean isConnectionException(Throwable throwable) {
        String message = throwable.getMessage();
        if (message == null) {
            return false;
        }
        
        String lowerMessage = message.toLowerCase();
        return lowerMessage.contains("connection") || 
               lowerMessage.contains("timeout") || 
               lowerMessage.contains("refused") ||
               lowerMessage.contains("unreachable");
    }
}