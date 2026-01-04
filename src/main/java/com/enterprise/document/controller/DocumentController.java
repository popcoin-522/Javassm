package com.enterprise.document.controller;

import com.enterprise.document.exception.ValidationException;
import com.enterprise.document.model.Document;
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
 * 文档管理控制器
 * 处理文档相关的Web请求
 */
@Controller
@RequestMapping("/documents")
public class DocumentController {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    
    @Autowired
    private DocumentService documentService;
    
    /**
     * 查看文档列表的GET请求处理
     * 需求: 1.1
     * 
     * @param model Spring MVC模型对象
     * @return 文档列表视图名称
     */
    @GetMapping
    public String listDocuments(Model model) {
        logger.info("处理查看文档列表请求");
        try {
            List<Document> documents = documentService.getAllDocuments();
            model.addAttribute("documents", documents);
            model.addAttribute("title", "文档列表");
            
            if (documents.isEmpty()) {
                model.addAttribute("message", "暂无文档");
            }
            
            logger.info("成功获取文档列表，共 {} 个文档", documents.size());
            return "documents/list";
        } catch (Exception e) {
            logger.error("获取文档列表失败", e);
            model.addAttribute("error", "获取文档列表失败: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 显示文档管理页面
     * 需求: 8.1
     * 
     * @param model Spring MVC模型对象
     * @return 文档管理视图名称
     */
    @GetMapping("/manage")
    public String manageDocuments(Model model) {
        logger.info("处理文档管理页面请求");
        try {
            List<Document> documents = documentService.getAllDocuments();
            model.addAttribute("documents", documents);
            model.addAttribute("title", "文档管理");
            return "documents/manage";
        } catch (Exception e) {
            logger.error("获取文档管理页面失败", e);
            model.addAttribute("error", "获取文档管理页面失败: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 显示添加文档表单
     * 需求: 2.1
     * 
     * @param model Spring MVC模型对象
     * @return 添加文档表单视图名称
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.info("显示添加文档表单");
        model.addAttribute("title", "添加文档");
        model.addAttribute("document", new Document());
        return "documents/add";
    }
    
    /**
     * 添加文档的POST请求处理
     * 需求: 2.1
     * 
     * @param documentCode 文档编号
     * @param documentName 文档名称
     * @param documentType 文档类型
     * @param redirectAttributes 重定向属性
     * @return 重定向到文档管理页面
     */
    @PostMapping("/add")
    public String addDocument(@RequestParam("documentCode") String documentCode,
                             @RequestParam("documentName") String documentName,
                             @RequestParam("documentType") String documentType,
                             RedirectAttributes redirectAttributes) {
        logger.info("处理添加文档请求: 编号={}, 名称={}, 类型={}", documentCode, documentName, documentType);
        
        try {
            Document document = documentService.addDocument(documentCode, documentName, documentType);
            redirectAttributes.addFlashAttribute("successMessage", 
                "文档添加成功！编号: " + document.getDocumentCode() + ", 名称: " + document.getDocumentName());
            logger.info("文档添加成功: {}", document);
            return "redirect:/documents/manage";
        } catch (ValidationException e) {
            logger.warn("文档添加验证失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "添加失败: " + e.getMessage());
            redirectAttributes.addFlashAttribute("documentCode", documentCode);
            redirectAttributes.addFlashAttribute("documentName", documentName);
            redirectAttributes.addFlashAttribute("documentType", documentType);
            return "redirect:/documents/add";
        } catch (Exception e) {
            logger.error("文档添加失败", e);
            redirectAttributes.addFlashAttribute("errorMessage", "添加失败: " + e.getMessage());
            return "redirect:/documents/add";
        }
    }
    
    /**
     * 显示修改文档表单
     * 需求: 2.2
     * 
     * @param documentCode 文档编号
     * @param model Spring MVC模型对象
     * @return 修改文档表单视图名称
     */
    @GetMapping("/edit/{documentCode}")
    public String showEditForm(@PathVariable("documentCode") String documentCode, Model model) {
        logger.info("显示修改文档表单: 编号={}", documentCode);
        
        try {
            Document document = documentService.getDocumentByCode(documentCode);
            if (document == null) {
                model.addAttribute("error", "文档不存在: " + documentCode);
                return "error";
            }
            
            model.addAttribute("title", "修改文档");
            model.addAttribute("document", document);
            return "documents/edit";
        } catch (Exception e) {
            logger.error("获取文档信息失败", e);
            model.addAttribute("error", "获取文档信息失败: " + e.getMessage());
            return "error";
        }
    }
    
    /**
     * 修改文档的PUT请求处理
     * 需求: 2.2
     * 
     * @param documentCode 文档编号
     * @param documentName 文档名称
     * @param documentType 文档类型
     * @param status 文档状态
     * @param redirectAttributes 重定向属性
     * @return 重定向到文档管理页面
     */
    @PostMapping("/edit/{documentCode}")
    public String updateDocument(@PathVariable("documentCode") String documentCode,
                                @RequestParam("documentName") String documentName,
                                @RequestParam("documentType") String documentType,
                                @RequestParam("status") String status,
                                RedirectAttributes redirectAttributes) {
        logger.info("处理修改文档请求: 编号={}", documentCode);
        
        try {
            Document document = documentService.updateDocument(documentCode, documentName, documentType, status);
            redirectAttributes.addFlashAttribute("successMessage", 
                "文档修改成功！编号: " + document.getDocumentCode() + ", 名称: " + document.getDocumentName());
            logger.info("文档修改成功: {}", document);
            return "redirect:/documents/manage";
        } catch (ValidationException e) {
            logger.warn("文档修改验证失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "修改失败: " + e.getMessage());
            return "redirect:/documents/edit/" + documentCode;
        } catch (Exception e) {
            logger.error("文档修改失败", e);
            redirectAttributes.addFlashAttribute("errorMessage", "修改失败: " + e.getMessage());
            return "redirect:/documents/edit/" + documentCode;
        }
    }
    
    /**
     * 删除文档的DELETE请求处理
     * 需求: 2.3, 2.4, 2.5
     * 
     * @param documentCode 文档编号
     * @param redirectAttributes 重定向属性
     * @return 重定向到文档管理页面
     */
    @PostMapping("/delete/{documentCode}")
    public String deleteDocument(@PathVariable("documentCode") String documentCode,
                                RedirectAttributes redirectAttributes) {
        logger.info("处理删除文档请求: 编号={}", documentCode);
        
        try {
            documentService.deleteDocument(documentCode);
            redirectAttributes.addFlashAttribute("successMessage", "文档删除成功！编号: " + documentCode);
            logger.info("文档删除成功: 编号={}", documentCode);
            return "redirect:/documents/manage";
        } catch (ValidationException e) {
            logger.warn("文档删除验证失败: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "删除失败: " + e.getMessage());
            return "redirect:/documents/manage";
        } catch (Exception e) {
            logger.error("文档删除失败", e);
            redirectAttributes.addFlashAttribute("errorMessage", "删除失败: " + e.getMessage());
            return "redirect:/documents/manage";
        }
    }
    
    /**
     * 获取文档详情的AJAX请求处理
     * 
     * @param documentCode 文档编号
     * @return 文档对象的JSON表示
     */
    @GetMapping("/api/{documentCode}")
    @ResponseBody
    public Document getDocumentApi(@PathVariable("documentCode") String documentCode) {
        logger.info("处理获取文档详情API请求: 编号={}", documentCode);
        return documentService.getDocumentByCode(documentCode);
    }
}