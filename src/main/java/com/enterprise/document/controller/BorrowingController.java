package com.enterprise.document.controller;

import com.enterprise.document.exception.BusinessException;
import com.enterprise.document.exception.ValidationException;
import com.enterprise.document.model.BorrowingRecord;
import com.enterprise.document.model.Document;
import com.enterprise.document.service.BorrowingService;
import com.enterprise.document.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 借阅管理控制器
 * 处理文档借阅相关的Web请求
 */
@Controller
@RequestMapping("/borrowing")
public class BorrowingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BorrowingController.class);
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private DocumentService documentService;
    
    /**
     * 显示借阅管理主页面
     * 需求: 8.1
     * 
     * @param model Spring MVC模型对象
     * @return 借阅管理视图名称
     */
    @GetMapping
    public String borrowingHome(Model model) {
        logger.info("显示借阅管理主页面");
        try {
            // 获取可借阅的文档列表
            List<Document> availableDocuments = documentService.getAllDocuments();
            model.addAttribute("documents", availableDocuments);
            model.addAttribute("title", "借阅管理");
            return "borrowing/index";
        } catch (Exception e) {
            logger.error("获取借阅管理页面失败", e);
            model.addAttribute("error", "获取借阅管理页面失败: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 显示文档借阅表单
     * 需求: 3.1
     * 
     * @param model Spring MVC模型对象
     * @return 借阅表单视图名称
     */
    @GetMapping("/borrow")
    public String showBorrowForm(Model model) {
        logger.info("显示文档借阅表单");
        try {
            List<Document> availableDocuments = documentService.getAllDocuments();
            model.addAttribute("documents", availableDocuments);
            model.addAttribute("title", "借阅文档");
            return "borrowing/borrow";
        } catch (Exception e) {
            logger.error("获取借阅表单失败", e);
            model.addAttribute("error", "获取借阅表单失败: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 文档借阅的POST请求处理
     * 需求: 3.1, 3.2
     * 
     * @param documentCode 文档编号
     * @param userId 工号/学号
     * @param userName 用户姓名
     * @param redirectAttributes 重定向属性
     * @return 重定向到借阅管理页面
     */
    @PostMapping("/borrow")
    public String borrowDocument(@RequestParam("documentCode") String documentCode,
                                @RequestParam("userId") String userId,
                                @RequestParam("userName") String userName,
                                RedirectAttributes redirectAttributes) {
        logger.info("处理文档借阅请求: 编号={}, 用户={}({})", documentCode, userName, userId);
        
        try {
            BorrowingRecord record = borrowingService.borrowDocument(documentCode, userId, userName);
            redirectAttributes.addFlashAttribute("successMessage", 
                String.format("文档借阅成功！文档编号: %s, 借阅人: %s(%s)", 
                    record.getDocumentCode(), record.getUserName(), record.getUserId()));
            logger.info("文档借阅成功: {}", record);
            return "redirect:/borrowing";
        } catch (ValidationException e) {
            logger.warn("文档借阅验证失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "借阅失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("documentCode", documentCode);
            redirectAttributes.addFlashAttribute("userId", userId);
            redirectAttributes.addFlashAttribute("userName", userName);
            return "redirect:/borrowing/borrow";
        } catch (BusinessException e) {
            logger.warn("文档借阅业务失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "借阅失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("documentCode", documentCode);
            redirectAttributes.addFlashAttribute("userId", userId);
            redirectAttributes.addFlashAttribute("userName", userName);
            return "redirect:/borrowing/borrow";
        } catch (Exception e) {
            logger.error("文档借阅失败", e);
            redirectAttributes.addFlashAttribute("errorMessage", "借阅失败: " + e.getMessage());
            return "redirect:/borrowing/borrow";
        }
    }
    
    /**
     * 显示文档归还表单
     * 需求: 4.1
     * 
     * @param model Spring MVC模型对象
     * @return 归还表单视图名称
     */
    @GetMapping("/return")
    public String showReturnForm(Model model) {
        logger.info("显示文档归还表单");
        try {
            // 获取未归还的借阅记录
            List<BorrowingRecord> activeRecords = borrowingService.getActiveBorrowingRecords();
            model.addAttribute("activeRecords", activeRecords);
            model.addAttribute("title", "归还文档");
            return "borrowing/return";
        } catch (Exception e) {
            logger.error("获取归还表单失败", e);
            model.addAttribute("error", "获取归还表单失败: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 文档归还的PUT请求处理
     * 需求: 4.1, 4.2
     * 
     * @param documentCode 文档编号
     * @param userId 工号/学号
     * @param userName 用户姓名
     * @param redirectAttributes 重定向属性
     * @return 重定向到借阅管理页面
     */
    @PostMapping("/return")
    public String returnDocument(@RequestParam("documentCode") String documentCode,
                                @RequestParam("userId") String userId,
                                @RequestParam("userName") String userName,
                                RedirectAttributes redirectAttributes) {
        logger.info("处理文档归还请求: 编号={}, 用户={}({})", documentCode, userName, userId);
        
        try {
            BorrowingRecord record = borrowingService.returnDocument(documentCode, userId, userName);
            redirectAttributes.addFlashAttribute("successMessage", 
                String.format("文档归还成功！文档编号: %s, 归还人: %s(%s)", 
                    record.getDocumentCode(), record.getUserName(), record.getUserId()));
            logger.info("文档归还成功: {}", record);
            return "redirect:/borrowing";
        } catch (ValidationException e) {
            logger.warn("文档归还验证失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "归还失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("documentCode", documentCode);
            redirectAttributes.addFlashAttribute("userId", userId);
            redirectAttributes.addFlashAttribute("userName", userName);
            return "redirect:/borrowing/return";
        } catch (BusinessException e) {
            logger.warn("文档归还业务失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "归还失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("documentCode", documentCode);
            redirectAttributes.addFlashAttribute("userId", userId);
            redirectAttributes.addFlashAttribute("userName", userName);
            return "redirect:/borrowing/return";
        } catch (Exception e) {
            logger.error("文档归还失败", e);
            redirectAttributes.addFlashAttribute("errorMessage", "归还失败: " + e.getMessage());
            return "redirect:/borrowing/return";
        }
    }
    
    /**
     * 查看借阅记录的GET请求处理
     * 需求: 5.1
     * 
     * @param model Spring MVC模型对象
     * @return 借阅记录视图名称
     */
    @GetMapping("/records")
    public String viewBorrowingRecords(Model model) {
        logger.info("处理查看借阅记录请求");
        try {
            List<BorrowingRecord> records = borrowingService.getAllBorrowingRecords();
            model.addAttribute("records", records);
            model.addAttribute("title", "借阅记录");
            
            if (records.isEmpty()) {
                model.addAttribute("message", "暂无借阅记录");
            }
            
            logger.info("成功获取借阅记录，共 {} 条记录", records.size());
            return "borrowing/records";
        } catch (Exception e) {
            logger.error("获取借阅记录失败", e);
            model.addAttribute("error", "获取借阅记录失败: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 根据文档编号查询借阅记录的API接口
     * 
     * @param documentCode 文档编号
     * @return 借阅记录列表的JSON表示
     */
    @GetMapping("/api/records/{documentCode}")
    @ResponseBody
    public List<BorrowingRecord> getBorrowingRecordsByDocument(@PathVariable("documentCode") String documentCode) {
        logger.info("处理查询文档借阅记录API请求: 编号={}", documentCode);
        return borrowingService.getBorrowingRecordsByDocument(documentCode);
    }
    
    /**
     * 根据用户ID查询借阅记录的API接口
     * 
     * @param userId 工号/学号
     * @return 借阅记录列表的JSON表示
     */
    @GetMapping("/api/user/{userId}")
    @ResponseBody
    public List<BorrowingRecord> getBorrowingRecordsByUser(@PathVariable("userId") String userId) {
        logger.info("处理查询用户借阅记录API请求: 用户ID={}", userId);
        return borrowingService.getBorrowingRecordsByUser(userId);
    }
    
    /**
     * 查询未归还借阅记录的API接口
     * 
     * @return 未归还借阅记录列表的JSON表示
     */
    @GetMapping("/api/active")
    @ResponseBody
    public List<BorrowingRecord> getActiveBorrowingRecords() {
        logger.info("处理查询未归还借阅记录API请求");
        return borrowingService.getActiveBorrowingRecords();
    }
}