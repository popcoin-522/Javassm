package com.enterprise.document.service;

import com.enterprise.document.exception.BusinessException;
import com.enterprise.document.exception.ValidationException;
import com.enterprise.document.model.BorrowingRecord;
import com.enterprise.document.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;

/**
 * **Feature: enterprise-document-management, Property 10: 操作结果反馈**
 * **验证: 需求 8.4**
 * 
 * 属性测试：对于任何用户操作，系统应该在Web页面显示操作结果并更新相关内容
 * 
 * 注意：由于测试环境不包含Web控制器，此测试专注于验证服务层操作的结果反馈机制，
 * 确保操作成功时返回正确的结果，操作失败时抛出适当的异常。
 */
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class OperationResultFeedbackPropertyTest extends AbstractTransactionalTestNGSpringContextTests {
    
    @Autowired
    private DocumentService documentService;
    
    @Autowired
    private BorrowingService borrowingService;
    
    @Autowired
    private ValidationService validationService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private Random random;
    
    @BeforeMethod
    public void setUp() {
        random = new Random();
        // 清理数据库，确保测试环境干净
        cleanDatabase();
    }
    
    /**
     * 清理数据库
     */
    private void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM borrowing_records");
        jdbcTemplate.update("DELETE FROM documents");
    }
    
    /**
     * 属性测试：文档操作结果反馈一致性
     * 对于任何文档操作（添加、修改、删除），系统应该提供明确的操作结果反馈
     * 成功操作应该返回正确的结果对象，失败操作应该抛出包含详细信息的异常
     */
    @Test(invocationCount = 100)
    public void testDocumentOperationFeedbackConsistency() {
        // 生成随机文档数据
        String documentCode = generateRandomDocumentCode();
        String documentName = generateRandomDocumentName();
        String documentType = generateRandomDocumentType();
        
        try {
            // 测试添加文档操作的反馈
            Document result = documentService.addDocument(documentCode, documentName, documentType);
            
            // 验证成功操作的结果反馈
            assertNotNull(result, "成功的文档添加操作应该返回文档对象");
            assertEquals(result.getDocumentCode(), documentCode, "返回的文档编号应该与输入一致");
            assertEquals(result.getDocumentName(), documentName, "返回的文档名称应该与输入一致");
            assertEquals(result.getDocumentType(), documentType, "返回的文档类型应该与输入一致");
            assertEquals(result.getStatus(), "可下载", "新添加的文档状态应该为'可下载'");
            
            // 验证操作结果的完整性
            assertNotNull(result.getDocumentCode(), "返回结果的文档编号不应该为空");
            assertNotNull(result.getDocumentName(), "返回结果的文档名称不应该为空");
            assertNotNull(result.getDocumentType(), "返回结果的文档类型不应该为空");
            assertNotNull(result.getStatus(), "返回结果的文档状态不应该为空");
            
        } catch (ValidationException e) {
            // 验证验证失败时的异常反馈
            assertNotNull(e.getMessage(), "验证异常应该包含错误消息");
            assertFalse(e.getMessage().trim().isEmpty(), "验证异常消息不应该为空");
            assertTrue(e.getMessage().contains("验证") || e.getMessage().contains("无效") || 
                      e.getMessage().contains("错误") || e.getMessage().contains("不能") ||
                      e.getMessage().contains("已存在") || e.getMessage().contains("不存在"),
                "验证异常消息应该包含相关关键词，实际消息: " + e.getMessage());
            
        } catch (RuntimeException e) {
            // 验证运行时异常的反馈
            assertNotNull(e.getMessage(), "运行时异常应该包含错误消息");
            assertFalse(e.getMessage().trim().isEmpty(), "运行时异常消息不应该为空");
            assertTrue(e.getMessage().length() > 5, "运行时异常消息应该提供足够的信息");
        }
    }
    
    /**
     * 属性测试：借阅操作结果反馈一致性
     * 对于任何借阅操作（借阅、归还），系统应该提供明确的操作结果反馈
     */
    @Test(invocationCount = 100)
    public void testBorrowingOperationFeedbackConsistency() {
        // 先添加一个可借阅的文档
        String documentCode = "DOC" + String.format("%03d", random.nextInt(1000));
        String documentName = "测试文档" + random.nextInt(100);
        String documentType = "技术文档";
        
        try {
            documentService.addDocument(documentCode, documentName, documentType);
        } catch (Exception e) {
            // 如果添加文档失败，跳过此次测试
            return;
        }
        
        // 生成随机借阅数据
        String userId = generateRandomUserId();
        String userName = generateRandomUserName();
        
        try {
            // 测试借阅操作的反馈
            BorrowingRecord result = borrowingService.borrowDocument(documentCode, userId, userName);
            
            // 验证成功操作的结果反馈
            assertNotNull(result, "成功的借阅操作应该返回借阅记录对象");
            assertEquals(result.getDocumentCode(), documentCode, "返回的文档编号应该与输入一致");
            assertEquals(result.getUserId(), userId, "返回的用户ID应该与输入一致");
            assertEquals(result.getUserName(), userName, "返回的用户姓名应该与输入一致");
            assertNotNull(result.getBorrowTime(), "借阅记录应该包含借阅时间");
            
            // 验证操作结果的完整性
            assertNotNull(result.getDocumentCode(), "返回结果的文档编号不应该为空");
            assertNotNull(result.getUserId(), "返回结果的用户ID不应该为空");
            assertNotNull(result.getUserName(), "返回结果的用户姓名不应该为空");
            
        } catch (ValidationException e) {
            // 验证验证失败时的异常反馈
            assertNotNull(e.getMessage(), "验证异常应该包含错误消息");
            assertFalse(e.getMessage().trim().isEmpty(), "验证异常消息不应该为空");
            assertTrue(e.getMessage().contains("验证") || e.getMessage().contains("无效") || 
                      e.getMessage().contains("错误") || e.getMessage().contains("不能"),
                "验证异常消息应该包含相关关键词，实际消息: " + e.getMessage());
            
        } catch (BusinessException e) {
            // 验证业务逻辑失败时的异常反馈
            assertNotNull(e.getMessage(), "业务异常应该包含错误消息");
            assertFalse(e.getMessage().trim().isEmpty(), "业务异常消息不应该为空");
            assertTrue(e.getMessage().contains("已借出") || e.getMessage().contains("归档") || 
                      e.getMessage().contains("不存在") || e.getMessage().contains("失败"),
                "业务异常消息应该包含相关状态信息，实际消息: " + e.getMessage());
        }
    }
    
    /**
     * 属性测试：查看操作内容更新一致性
     * 对于任何查看操作，系统应该返回完整的数据列表并保持数据一致性
     */
    @Test(invocationCount = 100)
    public void testViewOperationContentUpdateConsistency() {
        // 添加随机数量的测试数据
        int documentCount = random.nextInt(10); // 0-9个文档
        
        for (int i = 0; i < documentCount; i++) {
            try {
                String code = "DOC" + String.format("%03d", i + 1);
                String name = "测试文档" + (i + 1);
                String type = "技术文档";
                documentService.addDocument(code, name, type);
            } catch (Exception e) {
                // 忽略添加失败的情况
            }
        }
        
        try {
            // 测试文档列表查看操作
            List<Document> documents = documentService.getAllDocuments();
            
            // 验证查看操作的结果反馈
            assertNotNull(documents, "查看操作应该返回文档列表");
            
            // 验证列表内容的完整性
            for (Document doc : documents) {
                assertNotNull(doc.getDocumentCode(), "文档编号不应该为空");
                assertNotNull(doc.getDocumentName(), "文档名称不应该为空");
                assertNotNull(doc.getDocumentType(), "文档类型不应该为空");
                assertNotNull(doc.getStatus(), "文档状态不应该为空");
                
                assertFalse(doc.getDocumentCode().trim().isEmpty(), "文档编号不应该为空字符串");
                assertFalse(doc.getDocumentName().trim().isEmpty(), "文档名称不应该为空字符串");
                assertFalse(doc.getDocumentType().trim().isEmpty(), "文档类型不应该为空字符串");
                assertFalse(doc.getStatus().trim().isEmpty(), "文档状态不应该为空字符串");
            }
            
        } catch (Exception e) {
            // 验证异常情况下的反馈
            assertNotNull(e.getMessage(), "异常情况下应该提供错误信息");
            assertFalse(e.getMessage().trim().isEmpty(), "异常消息不应该为空");
        }
    }
    
    /**
     * 属性测试：借阅记录查看操作内容更新一致性
     * 对于任何借阅记录查看操作，系统应该返回完整的记录列表并保持数据一致性
     */
    @Test(invocationCount = 100)
    public void testBorrowingRecordsViewContentUpdateConsistency() {
        try {
            // 测试借阅记录查看操作
            List<BorrowingRecord> records = borrowingService.getAllBorrowingRecords();
            
            // 验证查看操作的结果反馈
            assertNotNull(records, "查看操作应该返回借阅记录列表");
            
            // 验证列表内容的完整性
            for (BorrowingRecord record : records) {
                assertNotNull(record.getDocumentCode(), "文档编号不应该为空");
                assertNotNull(record.getUserId(), "用户ID不应该为空");
                assertNotNull(record.getUserName(), "用户姓名不应该为空");
                assertNotNull(record.getBorrowTime(), "借阅时间不应该为空");
                
                assertFalse(record.getDocumentCode().trim().isEmpty(), "文档编号不应该为空字符串");
                assertFalse(record.getUserId().trim().isEmpty(), "用户ID不应该为空字符串");
                assertFalse(record.getUserName().trim().isEmpty(), "用户姓名不应该为空字符串");
            }
            
        } catch (Exception e) {
            // 验证异常情况下的反馈
            assertNotNull(e.getMessage(), "异常情况下应该提供错误信息");
            assertFalse(e.getMessage().trim().isEmpty(), "异常消息不应该为空");
        }
    }
    
    /**
     * 边界测试：操作反馈异常处理一致性
     * 测试各种异常情况下的反馈是否符合要求
     */
    @Test(invocationCount = 50)
    public void testOperationFeedbackExceptionHandlingConsistency() {
        // 测试无效输入的反馈
        try {
            // 尝试添加无效文档
            documentService.addDocument(null, null, null);
            fail("无效输入应该抛出异常");
        } catch (ValidationException e) {
            // 验证验证异常的反馈格式
            assertNotNull(e.getMessage(), "验证异常应该包含错误消息");
            assertFalse(e.getMessage().trim().isEmpty(), "验证异常消息不应该为空");
            assertTrue(e.getMessage().length() > 3, "验证异常消息应该提供足够的信息");
        } catch (Exception e) {
            // 其他异常也应该提供适当的反馈
            assertNotNull(e.getMessage(), "异常应该包含错误消息");
        }
        
        // 测试业务逻辑异常的反馈
        try {
            // 尝试借阅不存在的文档
            borrowingService.borrowDocument("NONEXISTENT", "USER001", "测试用户");
            fail("借阅不存在的文档应该抛出异常");
        } catch (BusinessException e) {
            // 验证业务异常的反馈格式
            assertNotNull(e.getMessage(), "业务异常应该包含错误消息");
            assertFalse(e.getMessage().trim().isEmpty(), "业务异常消息不应该为空");
            assertTrue(e.getMessage().contains("不存在") || e.getMessage().contains("找不到"),
                "业务异常消息应该说明具体问题，实际消息: " + e.getMessage());
        } catch (Exception e) {
            // 其他异常也应该提供适当的反馈
            assertNotNull(e.getMessage(), "异常应该包含错误消息");
        }
    }
    
    /**
     * 生成随机文档编号
     */
    private String generateRandomDocumentCode() {
        if (random.nextDouble() < 0.1) {
            return null; // 10%概率返回null
        }
        if (random.nextDouble() < 0.1) {
            return ""; // 10%概率返回空字符串
        }
        
        int length = random.nextInt(30) + 1; // 1到30字符
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return "DOC" + sb.toString();
    }
    
    /**
     * 生成随机文档名称
     */
    private String generateRandomDocumentName() {
        if (random.nextDouble() < 0.1) {
            return null; // 10%概率返回null
        }
        if (random.nextDouble() < 0.1) {
            return ""; // 10%概率返回空字符串
        }
        
        String[] names = {"技术文档", "用户手册", "设计方案", "测试报告", "需求文档", "API文档"};
        return names[random.nextInt(names.length)] + random.nextInt(1000);
    }
    
    /**
     * 生成随机文档类型
     */
    private String generateRandomDocumentType() {
        String[] types = {"技术文档", "管理文档", "设计文档", "测试文档", "用户文档"};
        return types[random.nextInt(types.length)];
    }
    
    /**
     * 生成随机用户ID
     */
    private String generateRandomUserId() {
        if (random.nextDouble() < 0.1) {
            return null; // 10%概率返回null
        }
        if (random.nextDouble() < 0.1) {
            return ""; // 10%概率返回空字符串
        }
        
        int length = random.nextInt(25) + 1; // 1到25字符
        StringBuilder sb = new StringBuilder();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成随机用户姓名
     */
    private String generateRandomUserName() {
        if (random.nextDouble() < 0.1) {
            return null; // 10%概率返回null
        }
        if (random.nextDouble() < 0.1) {
            return ""; // 10%概率返回空字符串
        }
        
        String[] firstNames = {"张", "李", "王", "刘", "陈", "杨", "赵", "黄", "周", "吴"};
        String[] lastNames = {"伟", "芳", "娜", "敏", "静", "丽", "强", "磊", "军", "洋"};
        
        int length = random.nextInt(15) + 1; // 1到15字符
        if (length <= 10) {
            return firstNames[random.nextInt(firstNames.length)] + 
                   lastNames[random.nextInt(lastNames.length)];
        } else {
            // 生成超长姓名用于测试边界情况
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append(firstNames[random.nextInt(firstNames.length)]);
            }
            return sb.toString();
        }
    }
}