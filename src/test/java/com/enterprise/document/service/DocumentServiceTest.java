package com.enterprise.document.service;

import com.enterprise.document.exception.ValidationException;
import com.enterprise.document.mapper.BorrowingRecordMapper;
import com.enterprise.document.mapper.DocumentMapper;
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
 * DocumentService单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@Transactional
public class DocumentServiceTest {
    
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
    public void testGetAllDocuments_EmptyList() {
        // 测试空文档列表
        List<Document> documents = documentService.getAllDocuments();
        assertNotNull(documents);
        assertTrue(documents.isEmpty());
    }
    
    @Test
    public void testAddDocument_ValidInput() throws ValidationException {
        // 测试添加有效文档
        String documentCode = "DOC001";
        String documentName = "测试文档";
        String documentType = "技术文档";
        
        Document result = documentService.addDocument(documentCode, documentName, documentType);
        
        assertNotNull(result);
        assertEquals(documentCode, result.getDocumentCode());
        assertEquals(documentName, result.getDocumentName());
        assertEquals(documentType, result.getDocumentType());
        assertEquals("可下载", result.getStatus());
        
        // 验证文档已保存到数据库
        Document saved = documentMapper.findByCode(documentCode);
        assertNotNull(saved);
        assertEquals(documentCode, saved.getDocumentCode());
    }
    
    @Test(expected = ValidationException.class)
    public void testAddDocument_EmptyCode() throws ValidationException {
        // 测试空文档编号
        documentService.addDocument("", "测试文档", "技术文档");
    }
    
    @Test(expected = ValidationException.class)
    public void testAddDocument_DuplicateCode() throws ValidationException {
        // 测试重复文档编号
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "文档1", "类型1");
        documentService.addDocument(documentCode, "文档2", "类型2"); // 应该抛出异常
    }
    
    @Test
    public void testUpdateDocument_ValidInput() throws ValidationException {
        // 先添加一个文档
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "原始文档", "原始类型");
        
        // 更新文档
        Document updated = documentService.updateDocument(documentCode, "更新后的文档", "更新后的类型", "已借出");
        
        assertNotNull(updated);
        assertEquals("更新后的文档", updated.getDocumentName());
        assertEquals("更新后的类型", updated.getDocumentType());
        assertEquals("已借出", updated.getStatus());
    }
    
    @Test(expected = ValidationException.class)
    public void testUpdateDocument_NonExistingDocument() throws ValidationException {
        // 测试更新不存在的文档
        documentService.updateDocument("NONEXISTENT", "文档", "类型", "可下载");
    }
    
    @Test
    public void testDeleteDocument_ValidArchivedDocument() throws ValidationException {
        // 添加一个归档文档
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        documentService.updateDocument(documentCode, null, null, "归档");
        
        // 删除文档
        documentService.deleteDocument(documentCode);
        
        // 验证文档已删除
        Document deleted = documentMapper.findByCode(documentCode);
        assertNull(deleted);
    }
    
    @Test(expected = ValidationException.class)
    public void testDeleteDocument_NonArchivedDocument() throws ValidationException {
        // 添加一个可下载文档
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        
        // 尝试删除非归档文档，应该抛出异常
        documentService.deleteDocument(documentCode);
    }
    
    @Test
    public void testGetDocumentByCode_ExistingDocument() throws ValidationException {
        // 添加文档
        String documentCode = "DOC001";
        documentService.addDocument(documentCode, "测试文档", "技术文档");
        
        // 查询文档
        Document found = documentService.getDocumentByCode(documentCode);
        
        assertNotNull(found);
        assertEquals(documentCode, found.getDocumentCode());
    }
    
    @Test
    public void testGetDocumentByCode_NonExistingDocument() {
        // 查询不存在的文档
        Document found = documentService.getDocumentByCode("NONEXISTENT");
        assertNull(found);
    }
}