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
 * **Feature: enterprise-document-management, Property 6: 借阅记录显示一致性**
 * **验证: 需求 5.1, 5.2**
 * 
 * 属性测试：对于任何借阅记录集合，查看借阅记录应该显示所有记录的完整信息（文档编号、工号/学号、姓名、借阅时间、归还时间），
 * 其中未归还的记录在归还时间字段必须显示"-"（即为null）
 */
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class BorrowingRecordDisplayConsistencyPropertyTest extends AbstractTransactionalTestNGSpringContextTests {

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
     * 数据提供器：生成随机的借阅记录场景
     * 每次测试运行100次迭代，使用不同的随机借阅记录组合
     */
    @DataProvider(name = "randomBorrowingRecordScenarios")
    public Object[][] provideRandomBorrowingRecordScenarios() {
        Object[][] dataSets = new Object[100][];
        for (int i = 0; i < 100; i++) {
            dataSets[i] = new Object[]{generateRandomBorrowingRecordScenario()};
        }
        return dataSets;
    }

    /**
     * 属性测试：借阅记录显示一致性
     * 
     * 测试逻辑：
     * 1. 创建多个文档和用户
     * 2. 执行多个借阅操作，部分归还，部分未归还
     * 3. 查询所有借阅记录
     * 4. 验证每条记录都包含完整的显示信息
     * 5. 验证未归还记录的归还时间为null
     * 6. 验证已归还记录的归还时间不为null
     */
    @Test(dataProvider = "randomBorrowingRecordScenarios")
    public void testBorrowingRecordDisplayConsistency(BorrowingRecordScenario scenario) {
        // 清理数据库，确保测试环境干净
        cleanDatabase();

        try {
            // 插入测试文档
            for (TestDocument doc : scenario.documents) {
                insertAvailableDocument(doc);
            }

            // 执行借阅操作
            for (BorrowingOperation operation : scenario.borrowingOperations) {
                borrowingService.borrowDocument(
                    operation.documentCode, 
                    operation.userId, 
                    operation.userName
                );
            }

            // 执行归还操作（只归还部分文档）
            for (ReturnOperation operation : scenario.returnOperations) {
                borrowingService.returnDocument(
                    operation.documentCode, 
                    operation.userId, 
                    operation.userName
                );
            }

            // 查询所有借阅记录
            List<BorrowingRecord> allRecords = borrowingService.getAllBorrowingRecords();

            // 验证记录数量正确
            assertEquals(allRecords.size(), scenario.borrowingOperations.length, 
                "借阅记录数量应该与借阅操作数量一致");

            // 验证每条借阅记录的显示一致性
            for (BorrowingRecord record : allRecords) {
                // 验证必需的显示字段都不为空
                assertNotNull(record.getDocumentCode(), 
                    "借阅记录的文档编号不应该为空");
                assertNotNull(record.getUserId(), 
                    "借阅记录的工号/学号不应该为空");
                assertNotNull(record.getUserName(), 
                    "借阅记录的姓名不应该为空");
                assertNotNull(record.getBorrowTime(), 
                    "借阅记录的借阅时间不应该为空");

                // 验证字段长度符合要求
                assertTrue(record.getDocumentCode().length() <= 20, 
                    "文档编号长度不应该超过20个字符");
                assertTrue(record.getUserId().length() <= 20, 
                    "工号/学号长度不应该超过20个字符");
                assertTrue(record.getUserName().length() <= 10, 
                    "姓名长度不应该超过10个字符");

                // 验证归还时间字段的显示一致性
                boolean isReturned = isRecordReturned(record, scenario.returnOperations);
                
                if (isReturned) {
                    // 已归还的记录，归还时间不应该为空
                    assertNotNull(record.getReturnTime(), 
                        "已归还记录的归还时间不应该为空（不应该显示'-'）");
                    assertEquals(record.getStatus(), "已归还", 
                        "已归还记录的状态应该为'已归还'");
                    
                    // 验证归还时间晚于借阅时间
                    assertTrue(record.getReturnTime().getTime() >= record.getBorrowTime().getTime(), 
                        "归还时间应该不早于借阅时间");
                } else {
                    // 未归还的记录，归还时间应该为空（显示为"-"）
                    assertNull(record.getReturnTime(), 
                        "未归还记录的归还时间应该为空（应该显示'-'）");
                    assertEquals(record.getStatus(), "借阅中", 
                        "未归还记录的状态应该为'借阅中'");
                }

                // 验证借阅时间在合理范围内（不能是未来时间）
                assertTrue(record.getBorrowTime().getTime() <= System.currentTimeMillis(), 
                    "借阅时间不应该是未来时间");

                // 验证记录与原始操作的一致性
                boolean foundMatchingOperation = false;
                for (BorrowingOperation operation : scenario.borrowingOperations) {
                    if (operation.documentCode.equals(record.getDocumentCode()) &&
                        operation.userId.equals(record.getUserId()) &&
                        operation.userName.equals(record.getUserName())) {
                        foundMatchingOperation = true;
                        break;
                    }
                }
                assertTrue(foundMatchingOperation, 
                    "每条借阅记录都应该对应一个借阅操作");
            }

            // 验证显示信息的完整性（所有字段都应该有值或符合显示规则）
            for (BorrowingRecord record : allRecords) {
                // 验证显示字段不包含特殊字符或格式问题
                assertFalse(record.getDocumentCode().trim().isEmpty(), 
                    "文档编号不应该为空白字符串");
                assertFalse(record.getUserId().trim().isEmpty(), 
                    "工号/学号不应该为空白字符串");
                assertFalse(record.getUserName().trim().isEmpty(), 
                    "姓名不应该为空白字符串");

                // 验证时间格式的一致性（应该是有效的时间戳）
                assertTrue(record.getBorrowTime().getTime() > 0, 
                    "借阅时间应该是有效的时间戳");
                
                if (record.getReturnTime() != null) {
                    assertTrue(record.getReturnTime().getTime() > 0, 
                        "归还时间应该是有效的时间戳");
                }
            }

        } catch (ValidationException | BusinessException e) {
            fail("借阅记录显示一致性测试不应该抛出异常: " + e.getMessage() + 
                 ", 场景: " + scenario.toString());
        }
    }

    /**
     * 边界测试：空借阅记录列表的显示一致性
     */
    @Test(invocationCount = 10)
    public void testEmptyBorrowingRecordListDisplayConsistency() {
        // 清理数据库，确保没有借阅记录
        cleanDatabase();

        // 查询借阅记录
        List<BorrowingRecord> records = borrowingService.getAllBorrowingRecords();

        // 验证空列表的显示一致性
        assertNotNull(records, "借阅记录列表不应该为null");
        assertEquals(records.size(), 0, "空数据库应该返回空的借阅记录列表");
        
        // 验证空列表的处理符合需求5.3（应该显示"暂无借阅记录"提示）
        // 这里我们验证服务层返回空列表，UI层应该根据空列表显示相应提示
        assertTrue(records.isEmpty(), "空的借阅记录列表应该为空");
    }

    /**
     * 边界测试：单条借阅记录的显示一致性
     */
    @Test(invocationCount = 10)
    public void testSingleBorrowingRecordDisplayConsistency() {
        // 清理数据库
        cleanDatabase();

        // 创建单个文档和用户
        TestDocument doc = generateRandomDocument("SINGLE_" + random.nextInt(1000));
        TestUser user = generateRandomUser();
        
        insertAvailableDocument(doc);

        try {
            // 执行单次借阅
            borrowingService.borrowDocument(doc.documentCode, user.userId, user.userName);

            // 查询借阅记录
            List<BorrowingRecord> records = borrowingService.getAllBorrowingRecords();

            // 验证单条记录的显示一致性
            assertEquals(records.size(), 1, "应该有一条借阅记录");
            
            BorrowingRecord record = records.get(0);
            
            // 验证完整的显示信息
            assertEquals(record.getDocumentCode(), doc.documentCode, 
                "文档编号应该正确显示");
            assertEquals(record.getUserId(), user.userId, 
                "工号/学号应该正确显示");
            assertEquals(record.getUserName(), user.userName, 
                "姓名应该正确显示");
            assertNotNull(record.getBorrowTime(), 
                "借阅时间应该正确显示");
            assertNull(record.getReturnTime(), 
                "未归还记录的归还时间应该为空（显示'-'）");
            assertEquals(record.getStatus(), "借阅中", 
                "未归还记录的状态应该为'借阅中'");

        } catch (ValidationException | BusinessException e) {
            fail("单条借阅记录显示一致性测试不应该抛出异常: " + e.getMessage());
        }
    }

    /**
     * 边界测试：大量借阅记录的显示一致性
     */
    @Test(invocationCount = 3)
    public void testLargeBorrowingRecordListDisplayConsistency() {
        // 清理数据库
        cleanDatabase();

        int recordCount = 50 + random.nextInt(50); // 50-99条记录
        TestDocument[] documents = new TestDocument[recordCount];
        TestUser[] users = new TestUser[recordCount];

        // 创建大量文档和用户
        for (int i = 0; i < recordCount; i++) {
            documents[i] = generateRandomDocument("LARGE_" + i + "_" + random.nextInt(1000));
            users[i] = generateRandomUser();
            insertAvailableDocument(documents[i]);
        }

        try {
            // 执行大量借阅操作
            for (int i = 0; i < recordCount; i++) {
                borrowingService.borrowDocument(
                    documents[i].documentCode, 
                    users[i].userId, 
                    users[i].userName
                );
            }

            // 随机归还一些文档
            int returnCount = recordCount / 3; // 归还约1/3的文档
            boolean[] returned = new boolean[recordCount];
            for (int i = 0; i < returnCount; i++) {
                int index = random.nextInt(recordCount);
                if (!returned[index]) {
                    borrowingService.returnDocument(
                        documents[index].documentCode, 
                        users[index].userId, 
                        users[index].userName
                    );
                    returned[index] = true;
                }
            }

            // 查询所有借阅记录
            List<BorrowingRecord> records = borrowingService.getAllBorrowingRecords();

            // 验证大量记录的显示一致性
            assertEquals(records.size(), recordCount, 
                "借阅记录数量应该与借阅操作数量一致");

            // 验证每条记录的显示一致性
            for (BorrowingRecord record : records) {
                // 验证基本显示字段
                assertNotNull(record.getDocumentCode(), "文档编号不应该为空");
                assertNotNull(record.getUserId(), "工号/学号不应该为空");
                assertNotNull(record.getUserName(), "姓名不应该为空");
                assertNotNull(record.getBorrowTime(), "借阅时间不应该为空");

                // 验证归还时间的显示一致性
                if ("已归还".equals(record.getStatus())) {
                    assertNotNull(record.getReturnTime(), 
                        "已归还记录的归还时间不应该为空");
                } else if ("借阅中".equals(record.getStatus())) {
                    assertNull(record.getReturnTime(), 
                        "未归还记录的归还时间应该为空（显示'-'）");
                }

                // 验证字段长度限制
                assertTrue(record.getDocumentCode().length() <= 20, 
                    "文档编号长度应该符合显示要求");
                assertTrue(record.getUserId().length() <= 20, 
                    "工号/学号长度应该符合显示要求");
                assertTrue(record.getUserName().length() <= 10, 
                    "姓名长度应该符合显示要求");
            }

        } catch (ValidationException | BusinessException e) {
            fail("大量借阅记录显示一致性测试不应该抛出异常: " + e.getMessage());
        }
    }

    /**
     * 检查记录是否已归还
     */
    private boolean isRecordReturned(BorrowingRecord record, ReturnOperation[] returnOperations) {
        for (ReturnOperation operation : returnOperations) {
            if (operation.documentCode.equals(record.getDocumentCode()) &&
                operation.userId.equals(record.getUserId()) &&
                operation.userName.equals(record.getUserName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 生成随机借阅记录场景
     */
    private BorrowingRecordScenario generateRandomBorrowingRecordScenario() {
        BorrowingRecordScenario scenario = new BorrowingRecordScenario();
        
        // 生成3-8个文档和用户
        int count = 3 + random.nextInt(6);
        scenario.documents = new TestDocument[count];
        scenario.borrowingOperations = new BorrowingOperation[count];
        
        for (int i = 0; i < count; i++) {
            scenario.documents[i] = generateRandomDocument("DISP_" + i + "_" + random.nextInt(1000));
            
            TestUser user = generateRandomUser();
            scenario.borrowingOperations[i] = new BorrowingOperation();
            scenario.borrowingOperations[i].documentCode = scenario.documents[i].documentCode;
            scenario.borrowingOperations[i].userId = user.userId;
            scenario.borrowingOperations[i].userName = user.userName;
        }
        
        // 随机选择一些记录进行归还（约50%的概率）
        int returnCount = random.nextInt(count + 1);
        scenario.returnOperations = new ReturnOperation[returnCount];
        
        boolean[] selected = new boolean[count];
        for (int i = 0; i < returnCount; i++) {
            int index;
            do {
                index = random.nextInt(count);
            } while (selected[index]);
            
            selected[index] = true;
            scenario.returnOperations[i] = new ReturnOperation();
            scenario.returnOperations[i].documentCode = scenario.borrowingOperations[index].documentCode;
            scenario.returnOperations[i].userId = scenario.borrowingOperations[index].userId;
            scenario.returnOperations[i].userName = scenario.borrowingOperations[index].userName;
        }
        
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
     * 借阅记录场景类
     */
    private static class BorrowingRecordScenario {
        TestDocument[] documents;
        BorrowingOperation[] borrowingOperations;
        ReturnOperation[] returnOperations;
        
        @Override
        public String toString() {
            return "BorrowingRecordScenario{" +
                    "documents=" + documents.length +
                    ", borrowingOperations=" + borrowingOperations.length +
                    ", returnOperations=" + returnOperations.length +
                    '}';
        }
    }

    /**
     * 借阅操作类
     */
    private static class BorrowingOperation {
        String documentCode;
        String userId;
        String userName;
    }

    /**
     * 归还操作类
     */
    private static class ReturnOperation {
        String documentCode;
        String userId;
        String userName;
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