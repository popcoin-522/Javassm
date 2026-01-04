package com.enterprise.document.service;

import com.enterprise.document.exception.ValidationException;
import com.enterprise.document.mapper.BorrowingRecordMapper;
import com.enterprise.document.mapper.DocumentMapper;
import com.enterprise.document.model.BorrowingRecord;
import com.enterprise.document.model.Document;
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
 * **Feature: enterprise-document-management, Property 3: 文档删除规则**
 * **验证: 需求 2.3, 2.4, 2.5**
 * 
 * 属性测试：对于任何文档，只有当文档状态为"归档"且无关联借阅记录时，删除操作才能成功；
 * 非归档文档或有借阅记录的归档文档删除应该失败并返回相应错误信息
 */
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class DocumentDeletionRulesPropertyTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private BorrowingRecordMapper borrowingRecordMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Random random = new Random();

    /**
     * 数据提供器：生成随机的文档删除测试场景
     * 每次测试运行100次迭代，使用不同的随机文档和借阅记录数据
     */
    @DataProvider(name = "randomDeletionScenarios")
    public Object[][] provideRandomDeletionScenarios() {
        Object[][] dataSets = new Object[100][];
        for (int i = 0; i < 100; i++) {
            dataSets[i] = new Object[]{generateRandomDeletionScenario()};
        }
        return dataSets;
    }

    /**
     * 属性测试：文档删除规则
     * 
     * 测试逻辑：
     * 1. 在数据库中插入随机生成的文档和借阅记录
     * 2. 尝试删除文档
     * 3. 验证删除结果符合业务规则：
     *    - 只有归档状态的文档才能被删除
     *    - 有借阅记录的归档文档不能被删除
     *    - 无借阅记录的归档文档可以被删除
     */
    @Test(dataProvider = "randomDeletionScenarios")
    public void testDocumentDeletionRules(DeletionScenario scenario) {
        // 清理数据库，确保测试环境干净
        cleanDatabase();

        // 插入测试文档
        insertTestDocument(scenario.document);

        // 插入借阅记录（如果有）
        if (scenario.hasBorrowingRecords) {
            insertTestBorrowingRecords(scenario.document.documentCode, scenario.borrowingRecords);
        }

        // 尝试删除文档并验证结果
        if (scenario.shouldDeleteSucceed) {
            // 删除应该成功
            try {
                documentService.deleteDocument(scenario.document.documentCode);
                
                // 验证文档已被删除
                Document deletedDoc = documentMapper.findByCode(scenario.document.documentCode);
                assertNull(deletedDoc, "删除成功后，文档应该不存在于数据库中");
                
            } catch (ValidationException e) {
                fail("删除归档状态且无借阅记录的文档应该成功，但抛出了异常: " + e.getMessage());
            }
        } else {
            // 删除应该失败
            try {
                documentService.deleteDocument(scenario.document.documentCode);
                fail("删除应该失败，但没有抛出异常。文档状态: " + scenario.document.status + 
                     ", 有借阅记录: " + scenario.hasBorrowingRecords);
            } catch (ValidationException e) {
                // 验证错误信息正确
                if (!"归档".equals(scenario.document.status)) {
                    assertEquals(e.getMessage(), "只能删除归档状态的文档", 
                        "非归档文档删除失败时应该返回正确的错误信息");
                } else if (scenario.hasBorrowingRecords) {
                    assertEquals(e.getMessage(), "存在借阅记录，无法删除", 
                        "有借阅记录的归档文档删除失败时应该返回正确的错误信息");
                }
                
                // 验证文档仍然存在
                Document stillExistsDoc = documentMapper.findByCode(scenario.document.documentCode);
                assertNotNull(stillExistsDoc, "删除失败后，文档应该仍然存在于数据库中");
            }
        }
    }

    /**
     * 边界测试：删除不存在的文档
     */
    @Test(invocationCount = 10)
    public void testDeleteNonExistentDocument() {
        // 清理数据库
        cleanDatabase();

        // 生成随机的不存在的文档编号
        String nonExistentCode = "NONEXIST" + random.nextInt(1000);

        // 尝试删除不存在的文档
        try {
            documentService.deleteDocument(nonExistentCode);
            fail("删除不存在的文档应该抛出ValidationException");
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "文档不存在: " + nonExistentCode, 
                "删除不存在的文档应该返回正确的错误信息");
        }
    }

    /**
     * 边界测试：删除空编号文档
     */
    @Test
    public void testDeleteDocumentWithEmptyCode() {
        // 测试空字符串
        try {
            documentService.deleteDocument("");
            fail("删除空编号文档应该抛出ValidationException");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("不能为空") || e.getMessage().contains("无效"), 
                "删除空编号文档应该返回验证错误信息");
        }

        // 测试null
        try {
            documentService.deleteDocument(null);
            fail("删除null编号文档应该抛出ValidationException");
        } catch (ValidationException e) {
            assertTrue(e.getMessage().contains("不能为空") || e.getMessage().contains("无效"), 
                "删除null编号文档应该返回验证错误信息");
        }
    }

    /**
     * 专项测试：归档文档无借阅记录的删除成功场景
     */
    @Test(invocationCount = 20)
    public void testSuccessfulDeletionOfArchivedDocumentWithoutBorrowingRecords() {
        // 清理数据库
        cleanDatabase();

        // 创建归档状态的文档
        TestDocument archivedDoc = generateRandomDocument("ARCH" + random.nextInt(1000));
        archivedDoc.status = "归档";
        insertTestDocument(archivedDoc);

        // 确认没有借阅记录（不插入任何借阅记录）
        int recordCount = borrowingRecordMapper.countByDocumentCode(archivedDoc.documentCode);
        assertEquals(recordCount, 0, "测试开始前应该没有借阅记录");

        // 删除应该成功
        try {
            documentService.deleteDocument(archivedDoc.documentCode);
            
            // 验证文档已被删除
            Document deletedDoc = documentMapper.findByCode(archivedDoc.documentCode);
            assertNull(deletedDoc, "归档状态且无借阅记录的文档删除后应该不存在");
            
        } catch (ValidationException e) {
            fail("删除归档状态且无借阅记录的文档应该成功，但抛出了异常: " + e.getMessage());
        }
    }

    /**
     * 专项测试：非归档文档的删除失败场景
     */
    @Test(invocationCount = 20)
    public void testFailedDeletionOfNonArchivedDocument() {
        // 清理数据库
        cleanDatabase();

        // 创建非归档状态的文档
        String[] nonArchivedStatuses = {"可下载", "已借出"};
        String status = nonArchivedStatuses[random.nextInt(nonArchivedStatuses.length)];
        
        TestDocument nonArchivedDoc = generateRandomDocument("NONARCH" + random.nextInt(1000));
        nonArchivedDoc.status = status;
        insertTestDocument(nonArchivedDoc);

        // 删除应该失败
        try {
            documentService.deleteDocument(nonArchivedDoc.documentCode);
            fail("删除非归档文档应该失败，但没有抛出异常。文档状态: " + status);
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "只能删除归档状态的文档", 
                "删除非归档文档应该返回正确的错误信息");
            
            // 验证文档仍然存在
            Document stillExistsDoc = documentMapper.findByCode(nonArchivedDoc.documentCode);
            assertNotNull(stillExistsDoc, "删除失败后，文档应该仍然存在");
            assertEquals(stillExistsDoc.getStatus(), status, "文档状态应该保持不变");
        }
    }

    /**
     * 专项测试：有借阅记录的归档文档删除失败场景
     */
    @Test(invocationCount = 20)
    public void testFailedDeletionOfArchivedDocumentWithBorrowingRecords() {
        // 清理数据库
        cleanDatabase();

        // 创建归档状态的文档
        TestDocument archivedDoc = generateRandomDocument("ARCHBORROW" + random.nextInt(1000));
        archivedDoc.status = "归档";
        insertTestDocument(archivedDoc);

        // 创建借阅记录
        List<TestBorrowingRecord> borrowingRecords = generateRandomBorrowingRecords(archivedDoc.documentCode, 1 + random.nextInt(3));
        insertTestBorrowingRecords(archivedDoc.documentCode, borrowingRecords);

        // 确认有借阅记录
        int recordCount = borrowingRecordMapper.countByDocumentCode(archivedDoc.documentCode);
        assertTrue(recordCount > 0, "测试开始前应该有借阅记录");

        // 删除应该失败
        try {
            documentService.deleteDocument(archivedDoc.documentCode);
            fail("删除有借阅记录的归档文档应该失败，但没有抛出异常");
        } catch (ValidationException e) {
            assertEquals(e.getMessage(), "存在借阅记录，无法删除", 
                "删除有借阅记录的归档文档应该返回正确的错误信息");
            
            // 验证文档仍然存在
            Document stillExistsDoc = documentMapper.findByCode(archivedDoc.documentCode);
            assertNotNull(stillExistsDoc, "删除失败后，文档应该仍然存在");
            assertEquals(stillExistsDoc.getStatus(), "归档", "文档状态应该保持归档");
        }
    }

    /**
     * 生成随机删除场景
     */
    private DeletionScenario generateRandomDeletionScenario() {
        DeletionScenario scenario = new DeletionScenario();
        
        // 生成随机文档
        String documentCode = "DEL" + String.format("%04d", random.nextInt(10000));
        scenario.document = generateRandomDocument(documentCode);
        
        // 随机决定是否有借阅记录
        scenario.hasBorrowingRecords = random.nextBoolean();
        
        if (scenario.hasBorrowingRecords) {
            // 生成1-5个借阅记录
            int recordCount = 1 + random.nextInt(5);
            scenario.borrowingRecords = generateRandomBorrowingRecords(documentCode, recordCount);
        } else {
            scenario.borrowingRecords = new ArrayList<>();
        }
        
        // 确定删除是否应该成功
        // 只有归档状态且无借阅记录的文档才能成功删除
        scenario.shouldDeleteSucceed = "归档".equals(scenario.document.status) && !scenario.hasBorrowingRecords;
        
        return scenario;
    }

    /**
     * 生成随机文档
     */
    private TestDocument generateRandomDocument(String documentCode) {
        TestDocument doc = new TestDocument();
        doc.documentCode = documentCode;
        doc.documentName = generateRandomDocumentName();
        doc.documentType = getRandomDocumentType();
        doc.status = getRandomDocumentStatus();
        return doc;
    }

    /**
     * 生成随机借阅记录列表
     */
    private List<TestBorrowingRecord> generateRandomBorrowingRecords(String documentCode, int count) {
        List<TestBorrowingRecord> records = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            TestBorrowingRecord record = new TestBorrowingRecord();
            record.documentCode = documentCode;
            record.userId = "USER" + String.format("%03d", random.nextInt(1000));
            record.userName = generateRandomUserName();
            record.borrowTime = new Timestamp(System.currentTimeMillis() - random.nextInt(30) * 24 * 60 * 60 * 1000L);
            
            // 随机决定是否已归还
            if (random.nextBoolean()) {
                record.returnTime = new Timestamp(record.borrowTime.getTime() + random.nextInt(14) * 24 * 60 * 60 * 1000L);
                record.status = "已归还";
            } else {
                record.returnTime = null;
                record.status = "借阅中";
            }
            
            records.add(record);
        }
        
        return records;
    }

    /**
     * 生成随机文档名称
     */
    private String generateRandomDocumentName() {
        String[] prefixes = {"测试", "项目", "技术", "业务", "系统", "用户", "管理", "开发"};
        String[] suffixes = {"文档", "方案", "报告", "规范", "手册", "指南", "说明", "资料"};
        
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        int number = random.nextInt(999) + 1;
        
        return prefix + suffix + number;
    }

    /**
     * 获取随机文档类型
     */
    private String getRandomDocumentType() {
        String[] types = {"合同", "方案", "报告", "技术资料", "规范文档", "用户手册", "设计文档", "测试报告"};
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
     * 生成随机用户名
     */
    private String generateRandomUserName() {
        String[] surnames = {"张", "王", "李", "赵", "刘", "陈", "杨", "黄", "周", "吴"};
        String[] names = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋"};
        
        return surnames[random.nextInt(surnames.length)] + names[random.nextInt(names.length)];
    }

    /**
     * 清理数据库
     */
    private void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM borrowing_records");
        jdbcTemplate.update("DELETE FROM documents");
    }

    /**
     * 插入测试文档到数据库
     */
    private void insertTestDocument(TestDocument doc) {
        jdbcTemplate.update(
            "INSERT INTO documents (document_code, document_name, document_type, status) VALUES (?, ?, ?, ?)",
            doc.documentCode, doc.documentName, doc.documentType, doc.status
        );
    }

    /**
     * 插入测试借阅记录到数据库
     */
    private void insertTestBorrowingRecords(String documentCode, List<TestBorrowingRecord> records) {
        for (TestBorrowingRecord record : records) {
            jdbcTemplate.update(
                "INSERT INTO borrowing_records (document_code, user_id, user_name, borrow_time, return_time, status) VALUES (?, ?, ?, ?, ?, ?)",
                record.documentCode, record.userId, record.userName, record.borrowTime, record.returnTime, record.status
            );
        }
    }

    /**
     * 删除场景类
     */
    private static class DeletionScenario {
        TestDocument document;
        boolean hasBorrowingRecords;
        List<TestBorrowingRecord> borrowingRecords;
        boolean shouldDeleteSucceed;
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
        Timestamp borrowTime;
        Timestamp returnTime;
        String status;
    }
}