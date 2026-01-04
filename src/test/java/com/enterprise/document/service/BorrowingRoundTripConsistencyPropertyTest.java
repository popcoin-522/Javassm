package com.enterprise.document.service;

import com.enterprise.document.exception.BusinessException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;

/**
 * **Feature: enterprise-document-management, Property 4: 借阅往返一致性**
 * **验证: 需求 3.2, 4.2**
 * 
 * 属性测试：对于任何状态为"可下载"的文档，执行借阅然后归还操作后，
 * 文档状态应该恢复为"可下载"
 */
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class BorrowingRoundTripConsistencyPropertyTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private BorrowingService borrowingService;

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
     * 数据提供器：生成随机的借阅往返测试场景
     * 每次测试运行100次迭代，使用不同的随机文档和用户数据
     */
    @DataProvider(name = "randomBorrowingScenarios")
    public Object[][] provideRandomBorrowingScenarios() {
        Object[][] dataSets = new Object[100][];
        for (int i = 0; i < 100; i++) {
            dataSets[i] = new Object[]{generateRandomBorrowingScenario()};
        }
        return dataSets;
    }

    /**
     * 属性测试：借阅往返一致性
     * 
     * 测试逻辑：
     * 1. 在数据库中插入状态为"可下载"的随机文档
     * 2. 使用随机用户信息借阅该文档
     * 3. 验证文档状态变为"已借出"
     * 4. 使用相同用户信息归还该文档
     * 5. 验证文档状态恢复为"可下载"
     * 6. 验证借阅记录状态为"已归还"
     */
    @Test(dataProvider = "randomBorrowingScenarios")
    public void testBorrowingRoundTripConsistency(BorrowingScenario scenario) {
        // 清理数据库，确保测试环境干净
        cleanDatabase();

        // 插入状态为"可下载"的测试文档
        insertAvailableDocument(scenario.document);

        try {
            // 第一步：借阅文档
            BorrowingRecord borrowingRecord = borrowingService.borrowDocument(
                scenario.document.documentCode, 
                scenario.user.userId, 
                scenario.user.userName
            );

            // 验证借阅操作成功
            assertNotNull(borrowingRecord, "借阅操作应该返回借阅记录");
            assertEquals(borrowingRecord.getDocumentCode(), scenario.document.documentCode, 
                "借阅记录的文档编号应该正确");
            assertEquals(borrowingRecord.getUserId(), scenario.user.userId, 
                "借阅记录的用户ID应该正确");
            assertEquals(borrowingRecord.getUserName(), scenario.user.userName, 
                "借阅记录的用户姓名应该正确");
            assertEquals(borrowingRecord.getStatus(), "借阅中", 
                "借阅记录的状态应该为'借阅中'");
            assertNotNull(borrowingRecord.getBorrowTime(), "借阅时间不应该为空");

            // 验证文档状态已更新为"已借出"
            Document borrowedDocument = documentMapper.findByCode(scenario.document.documentCode);
            assertNotNull(borrowedDocument, "借阅后文档应该仍然存在");
            assertEquals(borrowedDocument.getStatus(), "已借出", 
                "借阅后文档状态应该为'已借出'");

            // 第二步：归还文档
            BorrowingRecord returnedRecord = borrowingService.returnDocument(
                scenario.document.documentCode, 
                scenario.user.userId, 
                scenario.user.userName
            );

            // 验证归还操作成功
            assertNotNull(returnedRecord, "归还操作应该返回更新后的借阅记录");
            assertEquals(returnedRecord.getDocumentCode(), scenario.document.documentCode, 
                "归还记录的文档编号应该正确");
            assertEquals(returnedRecord.getUserId(), scenario.user.userId, 
                "归还记录的用户ID应该正确");
            assertEquals(returnedRecord.getUserName(), scenario.user.userName, 
                "归还记录的用户姓名应该正确");
            assertEquals(returnedRecord.getStatus(), "已归还", 
                "归还后借阅记录的状态应该为'已归还'");
            assertNotNull(returnedRecord.getReturnTime(), "归还时间不应该为空");

            // 核心验证：文档状态应该恢复为"可下载"（往返一致性）
            Document restoredDocument = documentMapper.findByCode(scenario.document.documentCode);
            assertNotNull(restoredDocument, "归还后文档应该仍然存在");
            assertEquals(restoredDocument.getStatus(), "可下载", 
                "往返操作后文档状态应该恢复为'可下载'");

            // 验证文档的其他属性保持不变
            assertEquals(restoredDocument.getDocumentName(), scenario.document.documentName, 
                "往返操作后文档名称应该保持不变");
            assertEquals(restoredDocument.getDocumentType(), scenario.document.documentType, 
                "往返操作后文档类型应该保持不变");

            // 验证借阅记录的完整性
            List<BorrowingRecord> allRecords = borrowingRecordMapper.findByDocumentCode(scenario.document.documentCode);
            assertEquals(allRecords.size(), 1, "应该只有一条借阅记录");
            BorrowingRecord finalRecord = allRecords.get(0);
            assertEquals(finalRecord.getStatus(), "已归还", "最终借阅记录状态应该为'已归还'");
            assertNotNull(finalRecord.getBorrowTime(), "借阅时间应该被记录");
            assertNotNull(finalRecord.getReturnTime(), "归还时间应该被记录");
            assertTrue(finalRecord.getReturnTime().getTime() >= finalRecord.getBorrowTime().getTime(), 
                "归还时间应该晚于或等于借阅时间");

        } catch (ValidationException | BusinessException e) {
            fail("对于状态为'可下载'的文档，借阅往返操作应该成功，但抛出了异常: " + e.getMessage() + 
                 ", 文档: " + scenario.document.documentCode + ", 用户: " + scenario.user.userId);
        }
    }

    /**
     * 边界测试：单字符文档编号和用户信息的往返一致性
     */
    @Test(invocationCount = 10)
    public void testBorrowingRoundTripWithMinimalInput() {
        // 清理数据库
        cleanDatabase();

        // 创建最小长度的有效输入
        TestDocument minimalDoc = new TestDocument();
        minimalDoc.documentCode = "A";
        minimalDoc.documentName = "最小文档";
        minimalDoc.documentType = "测试";

        TestUser minimalUser = new TestUser();
        minimalUser.userId = "1";
        minimalUser.userName = "测";

        // 插入文档
        insertAvailableDocument(minimalDoc);

        try {
            // 借阅和归还
            borrowingService.borrowDocument(minimalDoc.documentCode, minimalUser.userId, minimalUser.userName);
            borrowingService.returnDocument(minimalDoc.documentCode, minimalUser.userId, minimalUser.userName);

            // 验证往返一致性
            Document restoredDocument = documentMapper.findByCode(minimalDoc.documentCode);
            assertEquals(restoredDocument.getStatus(), "可下载", 
                "最小输入的往返操作后文档状态应该恢复为'可下载'");

        } catch (ValidationException | BusinessException e) {
            fail("最小有效输入的借阅往返操作应该成功，但抛出了异常: " + e.getMessage());
        }
    }

    /**
     * 边界测试：最大长度文档编号和用户信息的往返一致性
     */
    @Test(invocationCount = 10)
    public void testBorrowingRoundTripWithMaximalInput() {
        // 清理数据库
        cleanDatabase();

        // 创建最大长度的有效输入
        TestDocument maximalDoc = new TestDocument();
        maximalDoc.documentCode = generateRandomString(20); // 最大20字符
        maximalDoc.documentName = "最大长度文档名称测试";
        maximalDoc.documentType = "最大长度类型";

        TestUser maximalUser = new TestUser();
        maximalUser.userId = generateRandomString(20); // 最大20字符
        maximalUser.userName = generateValidNameString(10); // 最大10字符

        // 插入文档
        insertAvailableDocument(maximalDoc);

        try {
            // 借阅和归还
            borrowingService.borrowDocument(maximalDoc.documentCode, maximalUser.userId, maximalUser.userName);
            borrowingService.returnDocument(maximalDoc.documentCode, maximalUser.userId, maximalUser.userName);

            // 验证往返一致性
            Document restoredDocument = documentMapper.findByCode(maximalDoc.documentCode);
            assertEquals(restoredDocument.getStatus(), "可下载", 
                "最大输入的往返操作后文档状态应该恢复为'可下载'");

        } catch (ValidationException | BusinessException e) {
            fail("最大有效输入的借阅往返操作应该成功，但抛出了异常: " + e.getMessage());
        }
    }

    /**
     * 多次往返测试：验证多次借阅归还的一致性
     */
    @Test(invocationCount = 5)
    public void testMultipleBorrowingRoundTrips() {
        // 清理数据库
        cleanDatabase();

        // 创建测试文档和用户
        TestDocument doc = generateRandomDocument("MULTI" + random.nextInt(1000));
        TestUser user = generateRandomUser();

        // 插入文档
        insertAvailableDocument(doc);

        try {
            // 执行多次往返操作
            int roundTripCount = 3 + random.nextInt(3); // 3-5次往返
            
            for (int i = 0; i < roundTripCount; i++) {
                // 借阅
                borrowingService.borrowDocument(doc.documentCode, user.userId, user.userName);
                
                // 验证借阅后状态
                Document borrowedDoc = documentMapper.findByCode(doc.documentCode);
                assertEquals(borrowedDoc.getStatus(), "已借出", 
                    "第" + (i + 1) + "次借阅后文档状态应该为'已借出'");
                
                // 归还
                borrowingService.returnDocument(doc.documentCode, user.userId, user.userName);
                
                // 验证归还后状态
                Document returnedDoc = documentMapper.findByCode(doc.documentCode);
                assertEquals(returnedDoc.getStatus(), "可下载", 
                    "第" + (i + 1) + "次归还后文档状态应该恢复为'可下载'");
            }

            // 验证最终状态
            Document finalDoc = documentMapper.findByCode(doc.documentCode);
            assertEquals(finalDoc.getStatus(), "可下载", 
                "多次往返操作后文档最终状态应该为'可下载'");

            // 验证借阅记录数量
            List<BorrowingRecord> allRecords = borrowingRecordMapper.findByDocumentCode(doc.documentCode);
            assertEquals(allRecords.size(), roundTripCount, 
                "应该有" + roundTripCount + "条借阅记录");

            // 验证所有记录都已归还
            for (BorrowingRecord record : allRecords) {
                assertEquals(record.getStatus(), "已归还", "所有借阅记录都应该已归还");
                assertNotNull(record.getReturnTime(), "所有借阅记录都应该有归还时间");
            }

        } catch (ValidationException | BusinessException e) {
            fail("多次借阅往返操作应该成功，但抛出了异常: " + e.getMessage());
        }
    }

    /**
     * 生成随机借阅场景
     */
    private BorrowingScenario generateRandomBorrowingScenario() {
        BorrowingScenario scenario = new BorrowingScenario();
        
        // 生成随机文档（状态固定为"可下载"）
        String documentCode = "RT" + String.format("%04d", random.nextInt(10000));
        scenario.document = generateRandomDocument(documentCode);
        
        // 生成随机用户
        scenario.user = generateRandomUser();
        
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
        // 状态固定为"可下载"以满足属性测试的前提条件
        return doc;
    }

    /**
     * 生成随机用户
     */
    private TestUser generateRandomUser() {
        TestUser user = new TestUser();
        user.userId = "U" + String.format("%04d", random.nextInt(10000));
        user.userName = generateRandomUserName();
        return user;
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
     * 生成随机用户名
     */
    private String generateRandomUserName() {
        String[] surnames = {"张", "王", "李", "赵", "刘", "陈", "杨", "黄", "周", "吴"};
        String[] names = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋"};
        
        return surnames[random.nextInt(surnames.length)] + names[random.nextInt(names.length)];
    }

    /**
     * 生成随机字符串
     */
    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }

    /**
     * 生成有效的姓名字符串
     */
    private String generateValidNameString(int length) {
        StringBuilder sb = new StringBuilder();
        String validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789中文测试";
        
        for (int i = 0; i < length; i++) {
            sb.append(validChars.charAt(random.nextInt(validChars.length())));
        }
        
        return sb.toString();
    }

    /**
     * 清理数据库
     */
    private void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM borrowing_records");
        jdbcTemplate.update("DELETE FROM documents");
    }

    /**
     * 插入状态为"可下载"的测试文档到数据库
     */
    private void insertAvailableDocument(TestDocument doc) {
        jdbcTemplate.update(
            "INSERT INTO documents (document_code, document_name, document_type, status) VALUES (?, ?, ?, ?)",
            doc.documentCode, doc.documentName, doc.documentType, "可下载"
        );
    }

    /**
     * 借阅场景类
     */
    private static class BorrowingScenario {
        TestDocument document;
        TestUser user;
    }

    /**
     * 测试文档类
     */
    private static class TestDocument {
        String documentCode;
        String documentName;
        String documentType;
    }

    /**
     * 测试用户类
     */
    private static class TestUser {
        String userId;
        String userName;
    }
}