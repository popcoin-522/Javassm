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

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;

/**
 * **Feature: enterprise-document-management, Property 5: 借阅记录完整性**
 * **验证: 需求 3.5**
 * 
 * 属性测试：对于任何成功的借阅操作，系统应该创建包含文档编号、用户信息和借阅时间的完整借阅记录
 */
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class BorrowingRecordCompletenessPropertyTest extends AbstractTransactionalTestNGSpringContextTests {

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
     * 数据提供器：生成随机的借阅场景
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
     * 属性测试：借阅记录完整性
     * 
     * 测试逻辑：
     * 1. 在数据库中插入状态为"可下载"的随机文档
     * 2. 使用随机用户信息借阅该文档
     * 3. 验证返回的借阅记录包含所有必需字段
     * 4. 验证数据库中的借阅记录包含所有必需字段
     * 5. 验证借阅时间在合理范围内
     */
    @Test(dataProvider = "randomBorrowingScenarios")
    public void testBorrowingRecordCompleteness(BorrowingScenario scenario) {
        // 清理数据库，确保测试环境干净
        cleanDatabase();

        // 插入状态为"可下载"的测试文档
        insertAvailableDocument(scenario.document);

        // 记录借阅操作前的时间戳
        long beforeBorrowTime = System.currentTimeMillis();

        try {
            // 执行借阅操作
            BorrowingRecord borrowingRecord = borrowingService.borrowDocument(
                scenario.document.documentCode, 
                scenario.user.userId, 
                scenario.user.userName
            );

            // 记录借阅操作后的时间戳
            long afterBorrowTime = System.currentTimeMillis();

            // 验证返回的借阅记录的完整性
            assertNotNull(borrowingRecord, "借阅操作应该返回借阅记录");
            
            // 验证文档编号字段
            assertNotNull(borrowingRecord.getDocumentCode(), "借阅记录的文档编号不应该为空");
            assertEquals(borrowingRecord.getDocumentCode(), scenario.document.documentCode, 
                "借阅记录的文档编号应该与请求的文档编号一致");
            
            // 验证用户ID字段
            assertNotNull(borrowingRecord.getUserId(), "借阅记录的用户ID不应该为空");
            assertEquals(borrowingRecord.getUserId(), scenario.user.userId, 
                "借阅记录的用户ID应该与请求的用户ID一致");
            
            // 验证用户姓名字段
            assertNotNull(borrowingRecord.getUserName(), "借阅记录的用户姓名不应该为空");
            assertEquals(borrowingRecord.getUserName(), scenario.user.userName, 
                "借阅记录的用户姓名应该与请求的用户姓名一致");
            
            // 验证借阅时间字段
            assertNotNull(borrowingRecord.getBorrowTime(), "借阅记录的借阅时间不应该为空");
            assertTrue(borrowingRecord.getBorrowTime().getTime() >= beforeBorrowTime, 
                "借阅时间应该不早于操作开始时间");
            assertTrue(borrowingRecord.getBorrowTime().getTime() <= afterBorrowTime, 
                "借阅时间应该不晚于操作结束时间");
            
            // 验证借阅状态字段
            assertNotNull(borrowingRecord.getStatus(), "借阅记录的状态不应该为空");
            assertEquals(borrowingRecord.getStatus(), "借阅中", 
                "新创建的借阅记录状态应该为'借阅中'");

            // 验证数据库中的借阅记录完整性
            List<BorrowingRecord> dbRecords = borrowingRecordMapper.findByDocumentCode(scenario.document.documentCode);
            assertEquals(dbRecords.size(), 1, "数据库中应该只有一条借阅记录");
            
            BorrowingRecord dbRecord = dbRecords.get(0);
            
            // 验证数据库记录的完整性
            assertNotNull(dbRecord.getId(), "数据库中的借阅记录应该有ID");
            assertNotNull(dbRecord.getDocumentCode(), "数据库中的借阅记录的文档编号不应该为空");
            assertEquals(dbRecord.getDocumentCode(), scenario.document.documentCode, 
                "数据库中的借阅记录的文档编号应该正确");
            
            assertNotNull(dbRecord.getUserId(), "数据库中的借阅记录的用户ID不应该为空");
            assertEquals(dbRecord.getUserId(), scenario.user.userId, 
                "数据库中的借阅记录的用户ID应该正确");
            
            assertNotNull(dbRecord.getUserName(), "数据库中的借阅记录的用户姓名不应该为空");
            assertEquals(dbRecord.getUserName(), scenario.user.userName, 
                "数据库中的借阅记录的用户姓名应该正确");
            
            assertNotNull(dbRecord.getBorrowTime(), "数据库中的借阅记录的借阅时间不应该为空");
            assertTrue(dbRecord.getBorrowTime().getTime() >= beforeBorrowTime, 
                "数据库中的借阅时间应该不早于操作开始时间");
            assertTrue(dbRecord.getBorrowTime().getTime() <= afterBorrowTime, 
                "数据库中的借阅时间应该不晚于操作结束时间");
            
            assertNotNull(dbRecord.getStatus(), "数据库中的借阅记录的状态不应该为空");
            assertEquals(dbRecord.getStatus(), "借阅中", 
                "数据库中的新借阅记录状态应该为'借阅中'");
            
            // 验证归还时间字段应该为空（因为还未归还）
            assertNull(dbRecord.getReturnTime(), "未归还的借阅记录的归还时间应该为空");

            // 验证返回的记录与数据库记录一致性
            assertEquals(borrowingRecord.getDocumentCode(), dbRecord.getDocumentCode(), 
                "返回记录与数据库记录的文档编号应该一致");
            assertEquals(borrowingRecord.getUserId(), dbRecord.getUserId(), 
                "返回记录与数据库记录的用户ID应该一致");
            assertEquals(borrowingRecord.getUserName(), dbRecord.getUserName(), 
                "返回记录与数据库记录的用户姓名应该一致");
            assertEquals(borrowingRecord.getStatus(), dbRecord.getStatus(), 
                "返回记录与数据库记录的状态应该一致");

        } catch (ValidationException | BusinessException e) {
            fail("对于有效的借阅请求，借阅操作应该成功并创建完整的借阅记录，但抛出了异常: " + e.getMessage() + 
                 ", 文档: " + scenario.document.documentCode + ", 用户: " + scenario.user.userId);
        }
    }

    /**
     * 边界测试：最小长度输入的借阅记录完整性
     */
    @Test(invocationCount = 10)
    public void testBorrowingRecordCompletenessWithMinimalInput() {
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

        long beforeBorrowTime = System.currentTimeMillis();

        try {
            // 借阅文档
            BorrowingRecord borrowingRecord = borrowingService.borrowDocument(
                minimalDoc.documentCode, minimalUser.userId, minimalUser.userName);

            long afterBorrowTime = System.currentTimeMillis();

            // 验证最小输入的借阅记录完整性
            assertNotNull(borrowingRecord, "最小输入的借阅操作应该返回借阅记录");
            assertEquals(borrowingRecord.getDocumentCode(), minimalDoc.documentCode, 
                "最小输入的借阅记录文档编号应该正确");
            assertEquals(borrowingRecord.getUserId(), minimalUser.userId, 
                "最小输入的借阅记录用户ID应该正确");
            assertEquals(borrowingRecord.getUserName(), minimalUser.userName, 
                "最小输入的借阅记录用户姓名应该正确");
            assertNotNull(borrowingRecord.getBorrowTime(), "最小输入的借阅记录借阅时间不应该为空");
            assertTrue(borrowingRecord.getBorrowTime().getTime() >= beforeBorrowTime && 
                      borrowingRecord.getBorrowTime().getTime() <= afterBorrowTime, 
                "最小输入的借阅时间应该在合理范围内");
            assertEquals(borrowingRecord.getStatus(), "借阅中", 
                "最小输入的借阅记录状态应该为'借阅中'");

        } catch (ValidationException | BusinessException e) {
            fail("最小有效输入的借阅操作应该成功并创建完整记录，但抛出了异常: " + e.getMessage());
        }
    }

    /**
     * 边界测试：最大长度输入的借阅记录完整性
     */
    @Test(invocationCount = 10)
    public void testBorrowingRecordCompletenessWithMaximalInput() {
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

        long beforeBorrowTime = System.currentTimeMillis();

        try {
            // 借阅文档
            BorrowingRecord borrowingRecord = borrowingService.borrowDocument(
                maximalDoc.documentCode, maximalUser.userId, maximalUser.userName);

            long afterBorrowTime = System.currentTimeMillis();

            // 验证最大输入的借阅记录完整性
            assertNotNull(borrowingRecord, "最大输入的借阅操作应该返回借阅记录");
            assertEquals(borrowingRecord.getDocumentCode(), maximalDoc.documentCode, 
                "最大输入的借阅记录文档编号应该正确");
            assertEquals(borrowingRecord.getUserId(), maximalUser.userId, 
                "最大输入的借阅记录用户ID应该正确");
            assertEquals(borrowingRecord.getUserName(), maximalUser.userName, 
                "最大输入的借阅记录用户姓名应该正确");
            assertNotNull(borrowingRecord.getBorrowTime(), "最大输入的借阅记录借阅时间不应该为空");
            assertTrue(borrowingRecord.getBorrowTime().getTime() >= beforeBorrowTime && 
                      borrowingRecord.getBorrowTime().getTime() <= afterBorrowTime, 
                "最大输入的借阅时间应该在合理范围内");
            assertEquals(borrowingRecord.getStatus(), "借阅中", 
                "最大输入的借阅记录状态应该为'借阅中'");

        } catch (ValidationException | BusinessException e) {
            fail("最大有效输入的借阅操作应该成功并创建完整记录，但抛出了异常: " + e.getMessage());
        }
    }

    /**
     * 多用户借阅测试：验证多个用户借阅不同文档时记录的完整性
     */
    @Test(invocationCount = 5)
    public void testMultipleUsersBorrowingRecordCompleteness() {
        // 清理数据库
        cleanDatabase();

        // 创建多个文档和用户
        int userCount = 3 + random.nextInt(3); // 3-5个用户
        TestDocument[] documents = new TestDocument[userCount];
        TestUser[] users = new TestUser[userCount];

        for (int i = 0; i < userCount; i++) {
            documents[i] = generateRandomDocument("MULTI" + i + "_" + random.nextInt(1000));
            users[i] = generateRandomUser();
            insertAvailableDocument(documents[i]);
        }

        long beforeBorrowTime = System.currentTimeMillis();

        try {
            // 每个用户借阅一个文档
            for (int i = 0; i < userCount; i++) {
                BorrowingRecord record = borrowingService.borrowDocument(
                    documents[i].documentCode, users[i].userId, users[i].userName);

                // 验证每个借阅记录的完整性
                assertNotNull(record, "第" + (i + 1) + "个用户的借阅记录不应该为空");
                assertEquals(record.getDocumentCode(), documents[i].documentCode, 
                    "第" + (i + 1) + "个用户的借阅记录文档编号应该正确");
                assertEquals(record.getUserId(), users[i].userId, 
                    "第" + (i + 1) + "个用户的借阅记录用户ID应该正确");
                assertEquals(record.getUserName(), users[i].userName, 
                    "第" + (i + 1) + "个用户的借阅记录用户姓名应该正确");
                assertNotNull(record.getBorrowTime(), 
                    "第" + (i + 1) + "个用户的借阅记录借阅时间不应该为空");
                assertEquals(record.getStatus(), "借阅中", 
                    "第" + (i + 1) + "个用户的借阅记录状态应该为'借阅中'");
            }

            long afterBorrowTime = System.currentTimeMillis();

            // 验证数据库中所有借阅记录的完整性
            List<BorrowingRecord> allRecords = borrowingRecordMapper.findAll();
            assertEquals(allRecords.size(), userCount, 
                "数据库中应该有" + userCount + "条借阅记录");

            for (BorrowingRecord dbRecord : allRecords) {
                assertNotNull(dbRecord.getId(), "数据库记录应该有ID");
                assertNotNull(dbRecord.getDocumentCode(), "数据库记录的文档编号不应该为空");
                assertNotNull(dbRecord.getUserId(), "数据库记录的用户ID不应该为空");
                assertNotNull(dbRecord.getUserName(), "数据库记录的用户姓名不应该为空");
                assertNotNull(dbRecord.getBorrowTime(), "数据库记录的借阅时间不应该为空");
                assertTrue(dbRecord.getBorrowTime().getTime() >= beforeBorrowTime && 
                          dbRecord.getBorrowTime().getTime() <= afterBorrowTime, 
                    "数据库记录的借阅时间应该在合理范围内");
                assertEquals(dbRecord.getStatus(), "借阅中", 
                    "数据库记录的状态应该为'借阅中'");
                assertNull(dbRecord.getReturnTime(), "未归还记录的归还时间应该为空");
            }

        } catch (ValidationException | BusinessException e) {
            fail("多用户借阅操作应该成功并创建完整记录，但抛出了异常: " + e.getMessage());
        }
    }

    /**
     * 生成随机借阅场景
     */
    private BorrowingScenario generateRandomBorrowingScenario() {
        BorrowingScenario scenario = new BorrowingScenario();
        
        // 生成随机文档（状态固定为"可下载"）
        String documentCode = "RC" + String.format("%04d", random.nextInt(10000));
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