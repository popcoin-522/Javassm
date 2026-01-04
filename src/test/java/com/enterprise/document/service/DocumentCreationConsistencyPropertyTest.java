package com.enterprise.document.service;

import com.enterprise.document.exception.ValidationException;
import com.enterprise.document.mapper.DocumentMapper;
import com.enterprise.document.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;

/**
 * **Feature: enterprise-document-management, Property 2: 文档创建一致性**
 * **验证: 需求 2.1**
 * 
 * 属性测试：对于任何有效的文档信息（编号、名称、类型），创建文档后应该能够在文档列表中找到该文档，且状态为"可下载"
 */
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class DocumentCreationConsistencyPropertyTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Random random = new Random();

    /**
     * 数据提供器：生成随机的有效文档信息
     * 每次测试运行100次迭代，使用不同的随机文档数据
     */
    @DataProvider(name = "randomValidDocuments")
    public Object[][] provideRandomValidDocuments() {
        Object[][] dataSets = new Object[100][];
        for (int i = 0; i < 100; i++) {
            TestDocumentInfo docInfo = generateRandomValidDocument(i);
            dataSets[i] = new Object[]{docInfo};
        }
        return dataSets;
    }

    /**
     * 属性测试：文档创建一致性
     * 
     * 测试逻辑：
     * 1. 使用随机生成的有效文档信息调用DocumentService.addDocument()
     * 2. 验证文档创建成功且返回的文档对象包含正确信息
     * 3. 调用DocumentService.getAllDocuments()获取文档列表
     * 4. 验证新创建的文档在列表中存在
     * 5. 验证文档状态为"可下载"
     * 6. 验证文档信息完全匹配
     */
    @Test(dataProvider = "randomValidDocuments")
    public void testDocumentCreationConsistency(TestDocumentInfo docInfo) {
        // 清理数据库，确保测试环境干净
        cleanDatabase();

        try {
            // 创建文档
            Document createdDocument = documentService.addDocument(
                docInfo.documentCode, 
                docInfo.documentName, 
                docInfo.documentType
            );

            // 验证创建的文档对象
            assertNotNull(createdDocument, "创建的文档对象不应该为null");
            assertEquals(createdDocument.getDocumentCode(), docInfo.documentCode, 
                "创建的文档编号应该匹配");
            assertEquals(createdDocument.getDocumentName(), docInfo.documentName, 
                "创建的文档名称应该匹配");
            assertEquals(createdDocument.getDocumentType(), docInfo.documentType, 
                "创建的文档类型应该匹配");
            assertEquals(createdDocument.getStatus(), "可下载", 
                "新创建的文档状态应该为'可下载'");

            // 获取文档列表
            List<Document> allDocuments = documentService.getAllDocuments();

            // 验证文档在列表中存在
            assertNotNull(allDocuments, "文档列表不应该为null");
            assertFalse(allDocuments.isEmpty(), "文档列表不应该为空");

            // 查找创建的文档
            Document foundDocument = findDocumentByCode(allDocuments, docInfo.documentCode);
            assertNotNull(foundDocument, 
                "创建的文档应该在文档列表中找到: " + docInfo.documentCode);

            // 验证找到的文档信息完全匹配
            assertEquals(foundDocument.getDocumentCode(), docInfo.documentCode, 
                "列表中文档编号应该匹配");
            assertEquals(foundDocument.getDocumentName(), docInfo.documentName, 
                "列表中文档名称应该匹配");
            assertEquals(foundDocument.getDocumentType(), docInfo.documentType, 
                "列表中文档类型应该匹配");
            assertEquals(foundDocument.getStatus(), "可下载", 
                "列表中文档状态应该为'可下载'");

            // 验证通过编号直接查询也能找到文档
            Document directQueryDocument = documentService.getDocumentByCode(docInfo.documentCode);
            assertNotNull(directQueryDocument, 
                "通过编号直接查询应该能找到文档: " + docInfo.documentCode);
            assertEquals(directQueryDocument.getDocumentCode(), docInfo.documentCode, 
                "直接查询的文档编号应该匹配");
            assertEquals(directQueryDocument.getStatus(), "可下载", 
                "直接查询的文档状态应该为'可下载'");

        } catch (ValidationException e) {
            fail("有效的文档信息不应该导致验证异常: " + e.getMessage() + 
                 " (文档编号: " + docInfo.documentCode + 
                 ", 名称: " + docInfo.documentName + 
                 ", 类型: " + docInfo.documentType + ")");
        }
    }

    /**
     * 边界测试：最小长度的有效文档信息
     */
    @Test(invocationCount = 10)
    public void testMinimalValidDocumentCreation() {
        cleanDatabase();

        try {
            // 使用最小长度的有效信息
            String documentCode = "A";  // 1个字符
            String documentName = "文";  // 1个字符
            String documentType = "档";  // 1个字符

            Document createdDocument = documentService.addDocument(documentCode, documentName, documentType);

            // 验证创建成功
            assertNotNull(createdDocument, "最小长度的有效文档应该创建成功");
            assertEquals(createdDocument.getStatus(), "可下载", "状态应该为'可下载'");

            // 验证在列表中能找到
            List<Document> allDocuments = documentService.getAllDocuments();
            Document foundDocument = findDocumentByCode(allDocuments, documentCode);
            assertNotNull(foundDocument, "最小长度的文档应该在列表中找到");

        } catch (ValidationException e) {
            fail("最小长度的有效文档信息不应该导致验证异常: " + e.getMessage());
        }
    }

    /**
     * 边界测试：最大长度的有效文档信息
     */
    @Test(invocationCount = 10)
    public void testMaximalValidDocumentCreation() {
        cleanDatabase();

        try {
            // 使用最大长度的有效信息
            String documentCode = generateStringOfLength(20);  // 20个字符
            String documentName = generateChineseStringOfLength(50);  // 50个字符
            String documentType = generateChineseStringOfLength(30);  // 30个字符

            Document createdDocument = documentService.addDocument(documentCode, documentName, documentType);

            // 验证创建成功
            assertNotNull(createdDocument, "最大长度的有效文档应该创建成功");
            assertEquals(createdDocument.getStatus(), "可下载", "状态应该为'可下载'");

            // 验证在列表中能找到
            List<Document> allDocuments = documentService.getAllDocuments();
            Document foundDocument = findDocumentByCode(allDocuments, documentCode);
            assertNotNull(foundDocument, "最大长度的文档应该在列表中找到");

        } catch (ValidationException e) {
            fail("最大长度的有效文档信息不应该导致验证异常: " + e.getMessage());
        }
    }

    /**
     * 测试重复文档编号的处理
     */
    @Test(invocationCount = 10)
    public void testDuplicateDocumentCodeHandling() {
        cleanDatabase();

        try {
            String documentCode = "DUPLICATE001";
            String documentName1 = "第一个文档";
            String documentType1 = "类型1";
            String documentName2 = "第二个文档";
            String documentType2 = "类型2";

            // 创建第一个文档
            Document firstDocument = documentService.addDocument(documentCode, documentName1, documentType1);
            assertNotNull(firstDocument, "第一个文档应该创建成功");

            // 尝试创建相同编号的第二个文档，应该失败
            try {
                documentService.addDocument(documentCode, documentName2, documentType2);
                fail("重复的文档编号应该导致验证异常");
            } catch (ValidationException e) {
                assertTrue(e.getMessage().contains("文档编号已存在"), 
                    "重复编号的异常信息应该包含'文档编号已存在'");
            }

            // 验证只有第一个文档存在
            List<Document> allDocuments = documentService.getAllDocuments();
            assertEquals(allDocuments.size(), 1, "应该只有一个文档存在");
            
            Document foundDocument = findDocumentByCode(allDocuments, documentCode);
            assertNotNull(foundDocument, "应该找到第一个文档");
            assertEquals(foundDocument.getDocumentName(), documentName1, 
                "应该是第一个文档的名称");

        } catch (ValidationException e) {
            fail("第一个文档创建不应该失败: " + e.getMessage());
        }
    }

    /**
     * 在文档列表中查找指定编号的文档
     */
    private Document findDocumentByCode(List<Document> documents, String documentCode) {
        for (Document document : documents) {
            if (documentCode.equals(document.getDocumentCode())) {
                return document;
            }
        }
        return null;
    }

    /**
     * 生成随机的有效文档信息
     */
    private TestDocumentInfo generateRandomValidDocument(int index) {
        TestDocumentInfo docInfo = new TestDocumentInfo();
        
        // 生成唯一的文档编号（1-20字符）
        docInfo.documentCode = "DOC" + String.format("%04d", index + 1) + 
                              generateRandomAlphanumeric(random.nextInt(13) + 1);
        
        // 生成随机文档名称（1-50字符）
        docInfo.documentName = generateRandomDocumentName();
        
        // 生成随机文档类型（1-30字符）
        docInfo.documentType = generateRandomDocumentType();
        
        return docInfo;
    }

    /**
     * 生成随机文档名称
     */
    private String generateRandomDocumentName() {
        String[] prefixes = {"测试", "项目", "技术", "业务", "系统", "用户", "管理", "开发", "设计", "分析"};
        String[] suffixes = {"文档", "方案", "报告", "规范", "手册", "指南", "说明", "资料", "计划", "总结"};
        
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        int number = random.nextInt(9999) + 1;
        
        return prefix + suffix + number;
    }

    /**
     * 生成随机文档类型
     */
    private String generateRandomDocumentType() {
        String[] types = {"合同", "方案", "报告", "技术资料", "规范文档", "用户手册", "设计文档", "测试报告", 
                         "需求文档", "架构文档", "API文档", "操作手册", "培训资料", "项目计划"};
        return types[random.nextInt(types.length)];
    }

    /**
     * 生成指定长度的随机字母数字字符串
     */
    private String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的字符串
     */
    private String generateStringOfLength(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char) ('A' + (i % 26)));
        }
        return sb.toString();
    }

    /**
     * 生成指定长度的中文字符串
     */
    private String generateChineseStringOfLength(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 使用常用汉字范围
            char ch = (char) (0x4e00 + (i % 100));
            sb.append(ch);
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
     * 测试文档信息类
     */
    private static class TestDocumentInfo {
        String documentCode;
        String documentName;
        String documentType;
    }
}