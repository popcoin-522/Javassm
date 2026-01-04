package com.enterprise.document.service;

import com.enterprise.document.mapper.DocumentMapper;
import com.enterprise.document.mapper.BorrowingRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;

/**
 * **Feature: enterprise-document-management, Property 9: 数据加载一致性**
 * **验证: 需求 6.2**
 * 
 * 属性测试：验证系统启动时能够正确加载存储在数据库中的所有文档和借阅记录
 */
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class DataLoadingConsistencyPropertyTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private DatabaseInitializationService databaseInitializationService;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private BorrowingRecordMapper borrowingRecordMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Random random = new Random();

    /**
     * 数据提供器：生成随机的测试数据集
     * 每次测试运行100次迭代，使用不同的随机数据
     */
    @DataProvider(name = "randomDataSets")
    public Object[][] provideRandomDataSets() {
        Object[][] dataSets = new Object[100][];
        for (int i = 0; i < 100; i++) {
            dataSets[i] = new Object[]{generateRandomTestData()};
        }
        return dataSets;
    }

    /**
     * 属性测试：数据加载一致性
     * 
     * 测试逻辑：
     * 1. 在数据库中插入随机生成的文档和借阅记录数据
     * 2. 重新初始化数据库服务（模拟系统重启）
     * 3. 验证系统能够正确读取和统计所有数据
     * 4. 确保数据加载的一致性和完整性
     */
    @Test(dataProvider = "randomDataSets")
    public void testDataLoadingConsistency(TestDataSet testData) {
        // 清理数据库，确保测试环境干净
        cleanDatabase();

        // 插入测试数据到数据库
        insertTestData(testData);

        // 验证数据插入成功
        int actualDocumentCount = documentMapper.countDocuments();
        int actualRecordCount = borrowingRecordMapper.countRecords();

        assertEquals(actualDocumentCount, testData.documents.size(), 
            "插入的文档数量应该与预期一致");
        assertEquals(actualRecordCount, testData.borrowingRecords.size(), 
            "插入的借阅记录数量应该与预期一致");

        // 重新初始化数据库服务（模拟系统重启时的数据加载）
        try {
            databaseInitializationService.reinitializeDatabase();
        } catch (Exception e) {
            fail("数据库重新初始化失败: " + e.getMessage());
        }

        // 验证重新初始化后数据仍然一致
        int reloadedDocumentCount = documentMapper.countDocuments();
        int reloadedRecordCount = borrowingRecordMapper.countRecords();

        assertEquals(reloadedDocumentCount, testData.documents.size(), 
            "重新加载后的文档数量应该与原始数据一致");
        assertEquals(reloadedRecordCount, testData.borrowingRecords.size(), 
            "重新加载后的借阅记录数量应该与原始数据一致");

        // 验证数据库连接状态正常
        assertTrue(databaseInitializationService.isDatabaseConnected(), 
            "重新初始化后数据库连接应该正常");
    }

    /**
     * 边界测试：空数据库的数据加载一致性
     */
    @Test
    public void testEmptyDatabaseLoadingConsistency() {
        // 清理数据库
        cleanDatabase();

        // 验证空数据库状态
        assertEquals(documentMapper.countDocuments(), 0, "数据库应该为空");
        assertEquals(borrowingRecordMapper.countRecords(), 0, "借阅记录应该为空");

        // 重新初始化数据库服务
        try {
            databaseInitializationService.reinitializeDatabase();
        } catch (Exception e) {
            fail("空数据库重新初始化失败: " + e.getMessage());
        }

        // 验证空数据库重新初始化后状态一致
        assertEquals(documentMapper.countDocuments(), 0, "重新初始化后数据库应该仍为空");
        assertEquals(borrowingRecordMapper.countRecords(), 0, "重新初始化后借阅记录应该仍为空");
        assertTrue(databaseInitializationService.isDatabaseConnected(), "数据库连接应该正常");
    }

    /**
     * 生成随机测试数据集
     */
    private TestDataSet generateRandomTestData() {
        TestDataSet testData = new TestDataSet();
        
        // 生成随机数量的文档（1-10个）
        int documentCount = random.nextInt(10) + 1;
        for (int i = 0; i < documentCount; i++) {
            TestDocument doc = new TestDocument();
            doc.documentCode = "DOC" + String.format("%03d", i + 1);
            doc.documentName = "测试文档" + (i + 1);
            doc.documentType = getRandomDocumentType();
            doc.status = getRandomDocumentStatus();
            testData.documents.add(doc);
        }

        // 生成随机数量的借阅记录（0-15个）
        int recordCount = random.nextInt(16);
        for (int i = 0; i < recordCount; i++) {
            TestBorrowingRecord record = new TestBorrowingRecord();
            // 随机选择一个已存在的文档编号
            if (!testData.documents.isEmpty()) {
                record.documentCode = testData.documents.get(random.nextInt(testData.documents.size())).documentCode;
            } else {
                record.documentCode = "DOC001"; // 默认文档编号
            }
            record.userId = "U" + String.format("%03d", i + 1);
            record.userName = "用户" + (i + 1);
            record.status = getRandomBorrowingStatus();
            testData.borrowingRecords.add(record);
        }

        return testData;
    }

    /**
     * 获取随机文档类型
     */
    private String getRandomDocumentType() {
        String[] types = {"合同", "方案", "报告", "技术资料", "规范文档"};
        return types[random.nextInt(types.length)];
    }

    /**
     * 获取随机文档状态
     */
    private String getRandomDocumentStatus() {
        String[] statuses = {"可下载", "已借出", "归档"};
        return statuses[random.nextInt(statuses.length)];
    }

    /**
     * 获取随机借阅状态
     */
    private String getRandomBorrowingStatus() {
        String[] statuses = {"借阅中", "已归还"};
        return statuses[random.nextInt(statuses.length)];
    }

    /**
     * 清理数据库
     */
    private void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM borrowing_records");
        jdbcTemplate.update("DELETE FROM documents");
    }

    /**
     * 插入测试数据到数据库
     */
    private void insertTestData(TestDataSet testData) {
        // 插入文档数据
        for (TestDocument doc : testData.documents) {
            jdbcTemplate.update(
                "INSERT INTO documents (document_code, document_name, document_type, status) VALUES (?, ?, ?, ?)",
                doc.documentCode, doc.documentName, doc.documentType, doc.status
            );
        }

        // 插入借阅记录数据
        for (TestBorrowingRecord record : testData.borrowingRecords) {
            jdbcTemplate.update(
                "INSERT INTO borrowing_records (document_code, user_id, user_name, status) VALUES (?, ?, ?, ?)",
                record.documentCode, record.userId, record.userName, record.status
            );
        }
    }

    /**
     * 测试数据集类
     */
    private static class TestDataSet {
        List<TestDocument> documents = new ArrayList<>();
        List<TestBorrowingRecord> borrowingRecords = new ArrayList<>();
    }

    /**
     * 测试文档类
     */
    private static class TestDocument {
        String documentCode;
        String documentName;
        String documentType;
        String status;
    }

    /**
     * 测试借阅记录类
     */
    private static class TestBorrowingRecord {
        String documentCode;
        String userId;
        String userName;
        String status;
    }
}