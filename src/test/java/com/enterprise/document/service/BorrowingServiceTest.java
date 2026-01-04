package com.enterprise.document.service;

import com.enterprise.document.exception.BusinessException;
import com.enterprise.document.exception.ValidationException;
import com.enterprise.document.mapper.BorrowingRecordMapper;
import com.enterprise.document.mapper.DocumentMapper;
import com.enterprise.document.model.BorrowingRecord;
import com.enterprise.document.model.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

/**
 * BorrowingService单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@Transactional
public class BorrowingServiceTest {
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private DocumentMapper documentMapper;
    
    @Autowired
    private BorrowingRecordMapper borrowingRecordMapper;
    
    @Before
    public void setUp() {
        // 清理测试数据
        // 由于使用了@Transactional和defaultRollback=true，每个测试后会自动回滚
    }
    
    @Test
    public void testBorrowDocument_ValidInput() throws ValidationException, BusinessException {
        // 先添加一个可下载的文档
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        
        // 借阅文档
        String userId = "U001";
        String userName = "张三";
        BorrowingRecord result = borrowingService.borrowDocument(documentCode, userId, userName);
        
        assertNotNull(result);
        assertEquals(documentCode, result.getDocumentCode());
        assertEquals(userId, result.getUserId());
        assertEquals(userName, result.getUserName());
        assertEquals("借阅中", result.getStatus());
        assertNotNull(result.getBorrowTime());
        
        // 验证文档状态已更新为"已借出"
        Document document = documentMapper.findByCode(documentCode);
        assertEquals("已借出", document.getStatus());
    }
    
    @Test(expected = ValidationException.class)
    public void testBorrowDocument_NonExistingDocument() throws ValidationException, BusinessException {
        // 尝试借阅不存在的文档
        borrowingService.borrowDocument("NONEXISTENT", "U001", "张三");
    }
    
    @Test(expected = BusinessException.class)
    public void testBorrowDocument_AlreadyBorrowedDocument() throws ValidationException, BusinessException {
        // 先添加一个文档并借出
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        borrowingService.borrowDocument(documentCode, "U001", "张三");
        
        // 尝试再次借阅同一文档，应该抛出异常
        borrowingService.borrowDocument(documentCode, "U002", "李四");
    }
    
    @Test(expected = BusinessException.class)
    public void testBorrowDocument_ArchivedDocument() throws ValidationException, BusinessException {
        // 先添加一个归档文档
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        documentService.updateDocument(documentCode, null, null, "归档");
        
        // 尝试借阅归档文档，应该抛出异常
        borrowingService.borrowDocument(documentCode, "U001", "张三");
    }
    
    @Test
    public void testReturnDocument_ValidInput() throws ValidationException, BusinessException {
        // 先添加文档并借出
        String documentCode = "DOC001";
        String userId = "U001";
        String userName = "张三";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        borrowingService.borrowDocument(documentCode, userId, userName);
        
        // 归还文档
        BorrowingRecord result = borrowingService.returnDocument(documentCode, userId, userName);
        
        assertNotNull(result);
        assertEquals(documentCode, result.getDocumentCode());
        assertEquals(userId, result.getUserId());
        assertEquals(userName, result.getUserName());
        assertEquals("已归还", result.getStatus());
        assertNotNull(result.getReturnTime());
        
        // 验证文档状态已更新为"可下载"
        Document document = documentMapper.findByCode(documentCode);
        assertEquals("可下载", document.getStatus());
    }
    
    @Test(expected = BusinessException.class)
    public void testReturnDocument_NoActiveBorrowingRecord() throws ValidationException, BusinessException {
        // 先添加一个文档但不借出
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        
        // 尝试归还未借出的文档，应该抛出异常
        borrowingService.returnDocument(documentCode, "U001", "张三");
    }
    
    @Test
    public void testGetAllBorrowingRecords_EmptyList() {
        // 测试空借阅记录列表
        List<BorrowingRecord> records = borrowingService.getAllBorrowingRecords();
        assertNotNull(records);
        assertTrue(records.isEmpty());
    }
    
    @Test
    public void testGetAllBorrowingRecords_WithRecords() throws ValidationException, BusinessException {
        // 先添加文档并借出
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        borrowingService.borrowDocument(documentCode, "U001", "张三");
        
        // 查询借阅记录
        List<BorrowingRecord> records = borrowingService.getAllBorrowingRecords();
        
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals(documentCode, records.get(0).getDocumentCode());
    }
    
    @Test
    public void testGetBorrowingRecordsByDocument() throws ValidationException, BusinessException {
        // 先添加文档并借出
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        borrowingService.borrowDocument(documentCode, "U001", "张三");
        
        // 查询特定文档的借阅记录
        List<BorrowingRecord> records = borrowingService.getBorrowingRecordsByDocument(documentCode);
        
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals(documentCode, records.get(0).getDocumentCode());
    }
    
    @Test
    public void testGetActiveBorrowingRecords() throws ValidationException, BusinessException {
        // 先添加文档并借出
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        borrowingService.borrowDocument(documentCode, "U001", "张三");
        
        // 查询未归还的借阅记录
        List<BorrowingRecord> activeRecords = borrowingService.getActiveBorrowingRecords();
        
        assertNotNull(activeRecords);
        assertEquals(1, activeRecords.size());
        assertEquals("借阅中", activeRecords.get(0).getStatus());
        
        // 归还文档后再查询
        borrowingService.returnDocument(documentCode, "U001", "张三");
        activeRecords = borrowingService.getActiveBorrowingRecords();
        assertTrue(activeRecords.isEmpty());
    }
}