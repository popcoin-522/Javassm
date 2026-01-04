package com.enterprise.document.service;

import com.enterprise.document.exception.ValidationException;
import com.enterprise.document.mapper.BorrowingRecordMapper;
import com.enterprise.document.mapper.DocumentMapper;
import com.enterprise.document.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文档业务服务类
 * 提供文档管理的核心业务逻辑
 */
@Service
@Transactional
public class DocumentService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);
    
    @Autowired
    private DocumentMapper documentMapper;
    
    @Autowired
    private BorrowingRecordMapper borrowingRecordMapper;
    
    @Autowired
    private ValidationService validationService;
    
    /**
     * 查看文档列表功能（按编号排序）
     * 需求: 1.1, 1.3
     * 
     * @return 按编号排序的文档列表
     */
    public List<Document> getAllDocuments() {
        logger.info("查询所有文档列表");
        List<Document> documents = documentMapper.findAll();
        logger.info("查询到 {} 个文档", documents.size());
        return documents;
    }
    
    /**
     * 添加文档功能（默认状态为"可下载"）
     * 需求: 2.1
     * 
     * @param documentCode 文档编号
     * @param documentName 文档名称
     * @param documentType 文档类型
     * @return 创建的文档对象
     * @throws ValidationException 输入验证失败时抛出
     */
    public Document addDocument(String documentCode, String documentName, String documentType) 
            throws ValidationException {
        logger.info("添加新文档: 编号={}, 名称={}, 类型={}", documentCode, documentName, documentType);
        
        // 输入验证
        validationService.validateDocumentCode(documentCode);
        validationService.validateDocumentName(documentName);
        validationService.validateDocumentType(documentType);
        
        // 检查文档编号是否已存在
        Document existingDocument = documentMapper.findByCode(documentCode);
        if (existingDocument != null) {
            throw new ValidationException("文档编号已存在: " + documentCode);
        }
        
        // 创建新文档，默认状态为"可下载"
        Document document = new Document(documentCode, documentName, documentType);
        
        int result = documentMapper.insert(document);
        if (result > 0) {
            logger.info("文档添加成功: {}", document);
            return document;
        } else {
            throw new RuntimeException("文档添加失败");
        }
    }
    
    /**
     * 修改文档信息功能
     * 需求: 2.2
     * 
     * @param documentCode 文档编号
     * @param documentName 新的文档名称
     * @param documentType 新的文档类型
     * @param status 新的文档状态
     * @return 更新后的文档对象
     * @throws ValidationException 输入验证失败时抛出
     */
    public Document updateDocument(String documentCode, String documentName, 
                                 String documentType, String status) throws ValidationException {
        logger.info("修改文档信息: 编号={}", documentCode);
        
        // 输入验证
        validationService.validateDocumentCode(documentCode);
        if (documentName != null && !documentName.trim().isEmpty()) {
            validationService.validateDocumentName(documentName);
        }
        if (documentType != null && !documentType.trim().isEmpty()) {
            validationService.validateDocumentType(documentType);
        }
        
        // 检查文档是否存在
        Document existingDocument = documentMapper.findByCode(documentCode);
        if (existingDocument == null) {
            throw new ValidationException("文档不存在: " + documentCode);
        }
        
        // 更新文档信息
        if (documentName != null && !documentName.trim().isEmpty()) {
            existingDocument.setDocumentName(documentName);
        }
        if (documentType != null && !documentType.trim().isEmpty()) {
            existingDocument.setDocumentType(documentType);
        }
        if (status != null && !status.trim().isEmpty()) {
            // 验证状态值
            if (!isValidStatus(status)) {
                throw new ValidationException("无效的文档状态: " + status);
            }
            existingDocument.setStatus(status);
        }
        
        int result = documentMapper.update(existingDocument);
        if (result > 0) {
            logger.info("文档修改成功: {}", existingDocument);
            return existingDocument;
        } else {
            throw new RuntimeException("文档修改失败");
        }
    }
    
    /**
     * 删除归档文档功能（检查状态和借阅记录）
     * 需求: 2.3, 2.4, 2.5
     * 
     * @param documentCode 文档编号
     * @throws ValidationException 删除条件不满足时抛出
     */
    public void deleteDocument(String documentCode) throws ValidationException {
        logger.info("删除文档: 编号={}", documentCode);
        
        // 输入验证
        validationService.validateDocumentCode(documentCode);
        
        // 检查文档是否存在
        Document document = documentMapper.findByCode(documentCode);
        if (document == null) {
            throw new ValidationException("文档不存在: " + documentCode);
        }
        
        // 检查文档状态是否为"归档"
        if (!"归档".equals(document.getStatus())) {
            throw new ValidationException("只能删除归档状态的文档");
        }
        
        // 检查是否有关联的借阅记录
        int borrowingRecordCount = borrowingRecordMapper.countByDocumentCode(documentCode);
        if (borrowingRecordCount > 0) {
            throw new ValidationException("存在借阅记录，无法删除");
        }
        
        // 执行删除
        int result = documentMapper.deleteByCode(documentCode);
        if (result > 0) {
            logger.info("文档删除成功: 编号={}", documentCode);
        } else {
            throw new RuntimeException("文档删除失败");
        }
    }
    
    /**
     * 根据文档编号查询文档
     * 
     * @param documentCode 文档编号
     * @return 文档对象，如果不存在返回null
     */
    public Document getDocumentByCode(String documentCode) {
        logger.debug("查询文档: 编号={}", documentCode);
        return documentMapper.findByCode(documentCode);
    }
    
    /**
     * 验证文档状态是否有效
     * 
     * @param status 状态值
     * @return 是否有效
     */
    private boolean isValidStatus(String status) {
        return "可下载".equals(status) || "已借出".equals(status) || "归档".equals(status);
    }
}