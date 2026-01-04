package com.enterprise.document.mapper;

import com.enterprise.document.model.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.*;

/**
 * DocumentMapper单元测试类
 * 测试文档数据访问层的所有CRUD操作
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@Transactional
public class DocumentMapperTest {

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Document testDocument;

    @Before
    public void setUp() {
        // 清理测试数据
        jdbcTemplate.execute("DELETE FROM borrowing_records");
        jdbcTemplate.execute("DELETE FROM documents");
        
        // 创建测试文档
        testDocument = new Document("DOC001", "测试文档", "技术文档");
        testDocument.setStatus("可下载");
    }

    @After
    public void tearDown() {
        // 清理测试数据
        jdbcTemplate.execute("DELETE FROM borrowing_records");
        jdbcTemplate.execute("DELETE FROM documents");
    }

    @Test
    public void testCountDocuments_EmptyTable() {
        // 测试空表计数
        int count = documentMapper.countDocuments();
        assertEquals(0, count);
    }

    @Test
    public void testInsert_ValidDocument() {
        // 测试插入有效文档
        int result = documentMapper.insert(testDocument);
        assertEquals(1, result);
        
        // 验证插入后的计数
        int count = documentMapper.countDocuments();
        assertEquals(1, count);
    }

    @Test
    public void testFindAll_EmptyTable() {
        // 测试查询空表
        List<Document> documents = documentMapper.findAll();
        assertNotNull(documents);
        assertTrue(documents.isEmpty());
    }

    @Test
    public void testFindAll_WithDocuments() {
        // 插入测试数据
        documentMapper.insert(testDocument);
        
        Document doc2 = new Document("DOC002", "第二个文档", "合同");
        documentMapper.insert(doc2);
        
        // 测试查询所有文档
        List<Document> documents = documentMapper.findAll();
        assertNotNull(documents);
        assertEquals(2, documents.size());
        
        // 验证按编号排序
        assertEquals("DOC001", documents.get(0).getDocumentCode());
        assertEquals("DOC002", documents.get(1).getDocumentCode());
    }

    @Test
    public void testFindByCode_ExistingDocument() {
        // 插入测试数据
        documentMapper.insert(testDocument);
        
        // 测试根据编号查询
        Document found = documentMapper.findByCode("DOC001");
        assertNotNull(found);
        assertEquals("DOC001", found.getDocumentCode());
        assertEquals("测试文档", found.getDocumentName());
        assertEquals("技术文档", found.getDocumentType());
        assertEquals("可下载", found.getStatus());
    }

    @Test
    public void testFindByCode_NonExistingDocument() {
        // 测试查询不存在的文档
        Document found = documentMapper.findByCode("NONEXISTENT");
        assertNull(found);
    }

    @Test
    public void testUpdate_ExistingDocument() {
        // 插入测试数据
        documentMapper.insert(testDocument);
        
        // 查询插入的文档获取ID
        Document inserted = documentMapper.findByCode("DOC001");
        assertNotNull(inserted);
        
        // 更新文档信息
        inserted.setDocumentName("更新后的文档名");
        inserted.setDocumentType("更新后的类型");
        inserted.setStatus("已借出");
        
        int result = documentMapper.update(inserted);
        assertEquals(1, result);
        
        // 验证更新结果
        Document updated = documentMapper.findByCode("DOC001");
        assertNotNull(updated);
        assertEquals("更新后的文档名", updated.getDocumentName());
        assertEquals("更新后的类型", updated.getDocumentType());
        assertEquals("已借出", updated.getStatus());
    }

    @Test
    public void testUpdate_NonExistingDocument() {
        // 测试更新不存在的文档
        Document nonExisting = new Document("NONEXISTENT", "不存在", "类型");
        nonExisting.setId(999L);
        
        int result = documentMapper.update(nonExisting);
        assertEquals(0, result);
    }

    @Test
    public void testDeleteByCode_ExistingDocument() {
        // 插入测试数据
        documentMapper.insert(testDocument);
        
        // 验证文档存在
        Document found = documentMapper.findByCode("DOC001");
        assertNotNull(found);
        
        // 删除文档
        int result = documentMapper.deleteByCode("DOC001");
        assertEquals(1, result);
        
        // 验证删除结果
        Document deleted = documentMapper.findByCode("DOC001");
        assertNull(deleted);
        
        int count = documentMapper.countDocuments();
        assertEquals(0, count);
    }

    @Test
    public void testDeleteByCode_NonExistingDocument() {
        // 测试删除不存在的文档
        int result = documentMapper.deleteByCode("NONEXISTENT");
        assertEquals(0, result);
    }

    @Test
    public void testFindWithPagination() {
        // 插入多个测试文档
        for (int i = 1; i <= 5; i++) {
            Document doc = new Document("DOC00" + i, "文档" + i, "类型" + i);
            documentMapper.insert(doc);
        }
        
        // 测试分页查询
        List<Document> page1 = documentMapper.findWithPagination(0, 2);
        assertNotNull(page1);
        assertEquals(2, page1.size());
        assertEquals("DOC001", page1.get(0).getDocumentCode());
        assertEquals("DOC002", page1.get(1).getDocumentCode());
        
        List<Document> page2 = documentMapper.findWithPagination(2, 2);
        assertNotNull(page2);
        assertEquals(2, page2.size());
        assertEquals("DOC003", page2.get(0).getDocumentCode());
        assertEquals("DOC004", page2.get(1).getDocumentCode());
        
        List<Document> page3 = documentMapper.findWithPagination(4, 2);
        assertNotNull(page3);
        assertEquals(1, page3.size());
        assertEquals("DOC005", page3.get(0).getDocumentCode());
    }

    @Test
    public void testFindByStatus() {
        // 插入不同状态的文档
        Document doc1 = new Document("DOC001", "可下载文档", "类型1");
        doc1.setStatus("可下载");
        documentMapper.insert(doc1);
        
        Document doc2 = new Document("DOC002", "已借出文档", "类型2");
        doc2.setStatus("已借出");
        documentMapper.insert(doc2);
        
        Document doc3 = new Document("DOC003", "归档文档", "类型3");
        doc3.setStatus("归档");
        documentMapper.insert(doc3);
        
        Document doc4 = new Document("DOC004", "另一个可下载文档", "类型4");
        doc4.setStatus("可下载");
        documentMapper.insert(doc4);
        
        // 测试按状态查询
        List<Document> availableDocs = documentMapper.findByStatus("可下载");
        assertNotNull(availableDocs);
        assertEquals(2, availableDocs.size());
        
        List<Document> borrowedDocs = documentMapper.findByStatus("已借出");
        assertNotNull(borrowedDocs);
        assertEquals(1, borrowedDocs.size());
        assertEquals("DOC002", borrowedDocs.get(0).getDocumentCode());
        
        List<Document> archivedDocs = documentMapper.findByStatus("归档");
        assertNotNull(archivedDocs);
        assertEquals(1, archivedDocs.size());
        assertEquals("DOC003", archivedDocs.get(0).getDocumentCode());
        
        List<Document> nonExistentStatus = documentMapper.findByStatus("不存在的状态");
        assertNotNull(nonExistentStatus);
        assertTrue(nonExistentStatus.isEmpty());
    }

    @Test
    public void testInsert_WithTimestamps() {
        // 测试插入文档后时间戳是否正确设置
        documentMapper.insert(testDocument);
        
        Document inserted = documentMapper.findByCode("DOC001");
        assertNotNull(inserted);
        assertNotNull(inserted.getCreatedTime());
        assertNotNull(inserted.getUpdatedTime());
        
        // 验证时间戳在合理范围内（最近1分钟内）
        long now = System.currentTimeMillis();
        long createdTime = inserted.getCreatedTime().getTime();
        assertTrue("创建时间应该在最近1分钟内", now - createdTime < 60000);
    }
}