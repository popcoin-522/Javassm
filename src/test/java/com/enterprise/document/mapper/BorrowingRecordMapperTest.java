package com.enterprise.document.mapper;

import com.enterprise.document.model.BorrowingRecord;
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
 * BorrowingRecordMapper单元测试类
 * 测试借阅记录数据访问层的所有操作
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@Transactional
public class BorrowingRecordMapperTest {

    @Autowired
    private BorrowingRecordMapper borrowingRecordMapper;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Document testDocument;
    private BorrowingRecord testRecord;

    @Before
    public void setUp() {
        // 清理测试数据
        jdbcTemplate.execute("DELETE FROM borrowing_records");
        jdbcTemplate.execute("DELETE FROM documents");
        
        // 创建测试文档
        testDocument = new Document("DOC001", "测试文档", "技术文档");
        documentMapper.insert(testDocument);
        
        // 创建测试借阅记录
        testRecord = new BorrowingRecord("DOC001", "U001", "张三");
        testRecord.setStatus("借阅中");
    }

    @After
    public void tearDown() {
        // 清理测试数据
        jdbcTemplate.execute("DELETE FROM borrowing_records");
        jdbcTemplate.execute("DELETE FROM documents");
    }

    @Test
    public void testCountRecords_EmptyTable() {
        // 测试空表计数
        int count = borrowingRecordMapper.countRecords();
        assertEquals(0, count);
    }

    @Test
    public void testInsert_ValidRecord() {
        // 测试插入有效借阅记录
        int result = borrowingRecordMapper.insert(testRecord);
        assertEquals(1, result);
        
        // 验证插入后的计数
        int count = borrowingRecordMapper.countRecords();
        assertEquals(1, count);
    }

    @Test
    public void testFindAll_EmptyTable() {
        // 测试查询空表
        List<BorrowingRecord> records = borrowingRecordMapper.findAll();
        assertNotNull(records);
        assertTrue(records.isEmpty());
    }

    @Test
    public void testFindAll_WithRecords() {
        // 插入测试数据
        borrowingRecordMapper.insert(testRecord);
        
        // 创建第二个文档和借阅记录
        Document doc2 = new Document("DOC002", "第二个文档", "合同");
        documentMapper.insert(doc2);
        
        BorrowingRecord record2 = new BorrowingRecord("DOC002", "U002", "李四");
        borrowingRecordMapper.insert(record2);
        
        // 测试查询所有记录
        List<BorrowingRecord> records = borrowingRecordMapper.findAll();
        assertNotNull(records);
        assertEquals(2, records.size());
        
        // 验证记录内容
        boolean foundRecord1 = false, foundRecord2 = false;
        for (BorrowingRecord record : records) {
            if ("DOC001".equals(record.getDocumentCode()) && "U001".equals(record.getUserId())) {
                foundRecord1 = true;
                assertEquals("张三", record.getUserName());
                assertEquals("借阅中", record.getStatus());
            } else if ("DOC002".equals(record.getDocumentCode()) && "U002".equals(record.getUserId())) {
                foundRecord2 = true;
                assertEquals("李四", record.getUserName());
                assertEquals("借阅中", record.getStatus());
            }
        }
        assertTrue("应该找到第一条记录", foundRecord1);
        assertTrue("应该找到第二条记录", foundRecord2);
    }

    @Test
    public void testFindByDocumentCode() {
        // 插入测试数据
        borrowingRecordMapper.insert(testRecord);
        
        // 创建同一文档的另一条借阅记录
        BorrowingRecord record2 = new BorrowingRecord("DOC001", "U002", "李四");
        borrowingRecordMapper.insert(record2);
        
        // 创建不同文档的借阅记录
        Document doc2 = new Document("DOC002", "第二个文档", "合同");
        documentMapper.insert(doc2);
        BorrowingRecord record3 = new BorrowingRecord("DOC002", "U003", "王五");
        borrowingRecordMapper.insert(record3);
        
        // 测试根据文档编号查询
        List<BorrowingRecord> doc001Records = borrowingRecordMapper.findByDocumentCode("DOC001");
        assertNotNull(doc001Records);
        assertEquals(2, doc001Records.size());
        
        List<BorrowingRecord> doc002Records = borrowingRecordMapper.findByDocumentCode("DOC002");
        assertNotNull(doc002Records);
        assertEquals(1, doc002Records.size());
        assertEquals("U003", doc002Records.get(0).getUserId());
        
        List<BorrowingRecord> nonExistentRecords = borrowingRecordMapper.findByDocumentCode("NONEXISTENT");
        assertNotNull(nonExistentRecords);
        assertTrue(nonExistentRecords.isEmpty());
    }

    @Test
    public void testFindByUserId() {
        // 插入测试数据
        borrowingRecordMapper.insert(testRecord);
        
        // 创建同一用户的另一条借阅记录
        Document doc2 = new Document("DOC002", "第二个文档", "合同");
        documentMapper.insert(doc2);
        BorrowingRecord record2 = new BorrowingRecord("DOC002", "U001", "张三");
        borrowingRecordMapper.insert(record2);
        
        // 创建不同用户的借阅记录
        BorrowingRecord record3 = new BorrowingRecord("DOC001", "U002", "李四");
        borrowingRecordMapper.insert(record3);
        
        // 测试根据用户ID查询
        List<BorrowingRecord> u001Records = borrowingRecordMapper.findByUserId("U001");
        assertNotNull(u001Records);
        assertEquals(2, u001Records.size());
        
        List<BorrowingRecord> u002Records = borrowingRecordMapper.findByUserId("U002");
        assertNotNull(u002Records);
        assertEquals(1, u002Records.size());
        assertEquals("李四", u002Records.get(0).getUserName());
        
        List<BorrowingRecord> nonExistentRecords = borrowingRecordMapper.findByUserId("NONEXISTENT");
        assertNotNull(nonExistentRecords);
        assertTrue(nonExistentRecords.isEmpty());
    }

    @Test
    public void testFindByStatus() {
        // 插入不同状态的借阅记录
        borrowingRecordMapper.insert(testRecord);
        
        BorrowingRecord record2 = new BorrowingRecord("DOC001", "U002", "李四");
        record2.setStatus("已归还");
        borrowingRecordMapper.insert(record2);
        
        Document doc2 = new Document("DOC002", "第二个文档", "合同");
        documentMapper.insert(doc2);
        BorrowingRecord record3 = new BorrowingRecord("DOC002", "U003", "王五");
        record3.setStatus("借阅中");
        borrowingRecordMapper.insert(record3);
        
        // 测试按状态查询
        List<BorrowingRecord> borrowingRecords = borrowingRecordMapper.findByStatus("借阅中");
        assertNotNull(borrowingRecords);
        assertEquals(2, borrowingRecords.size());
        
        List<BorrowingRecord> returnedRecords = borrowingRecordMapper.findByStatus("已归还");
        assertNotNull(returnedRecords);
        assertEquals(1, returnedRecords.size());
        assertEquals("U002", returnedRecords.get(0).getUserId());
        
        List<BorrowingRecord> nonExistentStatus = borrowingRecordMapper.findByStatus("不存在的状态");
        assertNotNull(nonExistentStatus);
        assertTrue(nonExistentStatus.isEmpty());
    }

    @Test
    public void testFindActiveBorrowingRecord_Existing() {
        // 插入测试数据
        borrowingRecordMapper.insert(testRecord);
        
        // 测试查询活跃借阅记录
        BorrowingRecord found = borrowingRecordMapper.findActiveBorrowingRecord("DOC001", "U001", "张三");
        assertNotNull(found);
        assertEquals("DOC001", found.getDocumentCode());
        assertEquals("U001", found.getUserId());
        assertEquals("张三", found.getUserName());
        assertEquals("借阅中", found.getStatus());
    }

    @Test
    public void testFindActiveBorrowingRecord_NonExisting() {
        // 测试查询不存在的活跃借阅记录
        BorrowingRecord found = borrowingRecordMapper.findActiveBorrowingRecord("DOC001", "U999", "不存在");
        assertNull(found);
    }

    @Test
    public void testFindActiveBorrowingRecord_ReturnedRecord() {
        // 插入已归还的记录
        testRecord.setStatus("已归还");
        testRecord.setReturnTime(new Timestamp(System.currentTimeMillis()));
        borrowingRecordMapper.insert(testRecord);
        
        // 测试查询活跃借阅记录（应该找不到已归还的记录）
        BorrowingRecord found = borrowingRecordMapper.findActiveBorrowingRecord("DOC001", "U001", "张三");
        assertNull(found);
    }

    @Test
    public void testUpdate_ReturnDocument() {
        // 插入测试数据
        borrowingRecordMapper.insert(testRecord);
        
        // 查询插入的记录获取ID
        BorrowingRecord inserted = borrowingRecordMapper.findActiveBorrowingRecord("DOC001", "U001", "张三");
        assertNotNull(inserted);
        
        // 更新为已归还状态
        inserted.setStatus("已归还");
        inserted.setReturnTime(new Timestamp(System.currentTimeMillis()));
        
        int result = borrowingRecordMapper.update(inserted);
        assertEquals(1, result);
        
        // 验证更新结果
        List<BorrowingRecord> returnedRecords = borrowingRecordMapper.findByStatus("已归还");
        assertEquals(1, returnedRecords.size());
        assertEquals("U001", returnedRecords.get(0).getUserId());
        assertNotNull(returnedRecords.get(0).getReturnTime());
        
        // 验证活跃记录查询不到已归还的记录
        BorrowingRecord activeRecord = borrowingRecordMapper.findActiveBorrowingRecord("DOC001", "U001", "张三");
        assertNull(activeRecord);
    }

    @Test
    public void testUpdate_NonExistingRecord() {
        // 测试更新不存在的记录
        BorrowingRecord nonExisting = new BorrowingRecord("NONEXISTENT", "U999", "不存在");
        nonExisting.setId(999L);
        nonExisting.setStatus("已归还");
        
        int result = borrowingRecordMapper.update(nonExisting);
        assertEquals(0, result);
    }

    @Test
    public void testCountByDocumentCode() {
        // 插入测试数据
        borrowingRecordMapper.insert(testRecord);
        
        // 创建同一文档的另一条记录
        BorrowingRecord record2 = new BorrowingRecord("DOC001", "U002", "李四");
        borrowingRecordMapper.insert(record2);
        
        // 创建不同文档的记录
        Document doc2 = new Document("DOC002", "第二个文档", "合同");
        documentMapper.insert(doc2);
        BorrowingRecord record3 = new BorrowingRecord("DOC002", "U003", "王五");
        borrowingRecordMapper.insert(record3);
        
        // 测试按文档编号计数
        int doc001Count = borrowingRecordMapper.countByDocumentCode("DOC001");
        assertEquals(2, doc001Count);
        
        int doc002Count = borrowingRecordMapper.countByDocumentCode("DOC002");
        assertEquals(1, doc002Count);
        
        int nonExistentCount = borrowingRecordMapper.countByDocumentCode("NONEXISTENT");
        assertEquals(0, nonExistentCount);
    }

    @Test
    public void testFindWithPagination() {
        // 插入多个测试记录（使用不同的文档编号避免与setUp中的DOC001冲突）
        for (int i = 2; i <= 6; i++) {
            Document doc = new Document("DOC00" + i, "文档" + i, "类型" + i);
            documentMapper.insert(doc);
            
            BorrowingRecord record = new BorrowingRecord("DOC00" + i, "U00" + i, "用户" + i);
            borrowingRecordMapper.insert(record);
        }
        
        // 测试分页查询
        List<BorrowingRecord> page1 = borrowingRecordMapper.findWithPagination(0, 2);
        assertNotNull(page1);
        assertEquals(2, page1.size());
        
        List<BorrowingRecord> page2 = borrowingRecordMapper.findWithPagination(2, 2);
        assertNotNull(page2);
        assertEquals(2, page2.size());
        
        List<BorrowingRecord> page3 = borrowingRecordMapper.findWithPagination(4, 2);
        assertNotNull(page3);
        assertEquals(1, page3.size());
        
        // 验证总记录数
        int totalCount = borrowingRecordMapper.countRecords();
        assertEquals(5, totalCount);
    }

    @Test
    public void testInsert_WithTimestamps() {
        // 测试插入记录后时间戳是否正确设置
        borrowingRecordMapper.insert(testRecord);
        
        BorrowingRecord inserted = borrowingRecordMapper.findActiveBorrowingRecord("DOC001", "U001", "张三");
        assertNotNull(inserted);
        assertNotNull(inserted.getBorrowTime());
        assertNull(inserted.getReturnTime()); // 新借阅记录归还时间应为空
        
        // 验证时间戳在合理范围内（最近1分钟内）
        long now = System.currentTimeMillis();
        long borrowTime = inserted.getBorrowTime().getTime();
        assertTrue("借阅时间应该在最近1分钟内", now - borrowTime < 60000);
    }

    @Test
    public void testCompleteWorkflow_BorrowAndReturn() {
        // 测试完整的借阅和归还流程
        
        // 1. 借阅文档
        borrowingRecordMapper.insert(testRecord);
        
        // 2. 验证借阅记录存在
        BorrowingRecord activeRecord = borrowingRecordMapper.findActiveBorrowingRecord("DOC001", "U001", "张三");
        assertNotNull(activeRecord);
        assertEquals("借阅中", activeRecord.getStatus());
        assertNull(activeRecord.getReturnTime());
        
        // 3. 归还文档
        activeRecord.setStatus("已归还");
        activeRecord.setReturnTime(new Timestamp(System.currentTimeMillis()));
        borrowingRecordMapper.update(activeRecord);
        
        // 4. 验证归还后状态
        BorrowingRecord returnedRecord = borrowingRecordMapper.findByUserId("U001").get(0);
        assertEquals("已归还", returnedRecord.getStatus());
        assertNotNull(returnedRecord.getReturnTime());
        
        // 5. 验证活跃记录查询为空
        BorrowingRecord noActiveRecord = borrowingRecordMapper.findActiveBorrowingRecord("DOC001", "U001", "张三");
        assertNull(noActiveRecord);
        
        // 6. 验证总记录数不变
        int totalCount = borrowingRecordMapper.countRecords();
        assertEquals(1, totalCount);
    }
}