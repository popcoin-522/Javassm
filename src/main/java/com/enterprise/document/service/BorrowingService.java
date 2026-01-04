package com.enterprise.document.service;

import com.enterprise.document.exception.BusinessException;
import com.enterprise.document.exception.ValidationException;
import com.enterprise.document.mapper.BorrowingRecordMapper;
import com.enterprise.document.mapper.DocumentMapper;
import com.enterprise.document.model.BorrowingRecord;
import com.enterprise.document.model.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

/**
 * 借阅业务服务类
 * 提供文档借阅和归还的核心业务逻辑
 */
@Service
@Transactional
public class BorrowingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BorrowingService.class);
    
    @Autowired
    private DocumentMapper documentMapper;
    
    @Autowired
    private BorrowingRecordMapper borrowingRecordMapper;
    
    @Autowired
    private ValidationService validationService;
    
    /**
     * 文档借阅功能（状态检查、状态更新、记录创建）
     * 需求: 3.1, 3.2, 3.3, 3.4, 3.5
     * 
     * @param documentCode 文档编号
     * @param userId 工号/学号
     * @param userName 用户姓名
     * @return 创建的借阅记录
     * @throws ValidationException 输入验证失败时抛出
     * @throws BusinessException 业务规则违反时抛出
     */
    public BorrowingRecord borrowDocument(String documentCode, String userId, String userName) 
            throws ValidationException, BusinessException {
        logger.info("借阅文档: 编号={}, 用户={}({})", documentCode, userName, userId);
        
        // 输入验证
        validationService.validateDocumentCode(documentCode);
        validationService.validateUserId(userId);
        validationService.validateUserName(userName);
        
        // 检查文档是否存在
        Document document = documentMapper.findByCode(documentCode);
        if (document == null) {
            throw new ValidationException("文档不存在: " + documentCode);
        }
        
        // 检查文档状态
        String status = document.getStatus();
        if ("已借出".equals(status)) {
            throw new BusinessException("文档已借出");
        } else if ("归档".equals(status)) {
            throw new BusinessException("文档已归档不可借阅");
        } else if (!"可下载".equals(status)) {
            throw new BusinessException("文档状态异常，无法借阅");
        }
        
        // 更新文档状态为"已借出"
        document.setStatus("已借出");
        int updateResult = documentMapper.update(document);
        if (updateResult <= 0) {
            throw new RuntimeException("更新文档状态失败");
        }
        
        // 创建借阅记录
        BorrowingRecord borrowingRecord = new BorrowingRecord(documentCode, userId, userName);
        borrowingRecord.setBorrowTime(new Timestamp(System.currentTimeMillis()));
        
        int insertResult = borrowingRecordMapper.insert(borrowingRecord);
        if (insertResult > 0) {
            logger.info("文档借阅成功: {}", borrowingRecord);
            return borrowingRecord;
        } else {
            // 如果插入借阅记录失败，需要回滚文档状态
            document.setStatus("可下载");
            documentMapper.update(document);
            throw new RuntimeException("创建借阅记录失败");
        }
    }
    
    /**
     * 文档归还功能（记录验证、状态更新、时间记录）
     * 需求: 4.1, 4.2, 4.3, 4.4
     * 
     * @param documentCode 文档编号
     * @param userId 工号/学号
     * @param userName 用户姓名
     * @return 更新后的借阅记录
     * @throws ValidationException 输入验证失败时抛出
     * @throws BusinessException 业务规则违反时抛出
     */
    public BorrowingRecord returnDocument(String documentCode, String userId, String userName) 
            throws ValidationException, BusinessException {
        logger.info("归还文档: 编号={}, 用户={}({})", documentCode, userName, userId);
        
        // 输入验证
        validationService.validateDocumentCode(documentCode);
        validationService.validateUserId(userId);
        validationService.validateUserName(userName);
        
        // 查找对应的借阅记录
        BorrowingRecord borrowingRecord = borrowingRecordMapper.findActiveBorrowingRecord(
                documentCode, userId, userName);
        
        if (borrowingRecord == null) {
            throw new BusinessException("无此借阅记录");
        }
        
        // 检查文档是否存在
        Document document = documentMapper.findByCode(documentCode);
        if (document == null) {
            throw new ValidationException("文档不存在: " + documentCode);
        }
        
        // 检查文档状态是否为"已借出"
        if (!"已借出".equals(document.getStatus())) {
            throw new BusinessException("文档状态异常，无法归还");
        }
        
        // 更新文档状态为"可下载"
        document.setStatus("可下载");
        int updateDocResult = documentMapper.update(document);
        if (updateDocResult <= 0) {
            throw new RuntimeException("更新文档状态失败");
        }
        
        // 更新借阅记录的归还时间和状态
        borrowingRecord.setReturnTime(new Timestamp(System.currentTimeMillis()));
        borrowingRecord.setStatus("已归还");
        
        int updateRecordResult = borrowingRecordMapper.update(borrowingRecord);
        if (updateRecordResult > 0) {
            logger.info("文档归还成功: {}", borrowingRecord);
            return borrowingRecord;
        } else {
            // 如果更新借阅记录失败，需要回滚文档状态
            document.setStatus("已借出");
            documentMapper.update(document);
            throw new RuntimeException("更新借阅记录失败");
        }
    }
    
    /**
     * 查看借阅记录功能
     * 需求: 5.1, 5.2
     * 
     * @return 所有借阅记录列表
     */
    public List<BorrowingRecord> getAllBorrowingRecords() {
        logger.info("查询所有借阅记录");
        List<BorrowingRecord> records = borrowingRecordMapper.findAll();
        logger.info("查询到 {} 条借阅记录", records.size());
        return records;
    }
    
    /**
     * 根据文档编号查询借阅记录
     * 
     * @param documentCode 文档编号
     * @return 借阅记录列表
     */
    public List<BorrowingRecord> getBorrowingRecordsByDocument(String documentCode) {
        logger.debug("查询文档借阅记录: 编号={}", documentCode);
        return borrowingRecordMapper.findByDocumentCode(documentCode);
    }
    
    /**
     * 根据用户ID查询借阅记录
     * 
     * @param userId 工号/学号
     * @return 借阅记录列表
     */
    public List<BorrowingRecord> getBorrowingRecordsByUser(String userId) {
        logger.debug("查询用户借阅记录: 用户ID={}", userId);
        return borrowingRecordMapper.findByUserId(userId);
    }
    
    /**
     * 查询未归还的借阅记录
     * 
     * @return 未归还的借阅记录列表
     */
    public List<BorrowingRecord> getActiveBorrowingRecords() {
        logger.debug("查询未归还的借阅记录");
        return borrowingRecordMapper.findByStatus("借阅中");
    }
}