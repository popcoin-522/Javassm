package com.enterprise.document.service;

import com.enterprise.document.mapper.DocumentMapper;
import com.enterprise.document.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;

/**
 * **Feature: enterprise-document-management, Property 1: 文档列表完整性**
 * **验证: 需求 1.1, 1.3**
 * 
 * 属性测试：对于任何文档集合，查看文档列表应该返回所有文档的完整信息
 * （编号、名称、类型、状态），并按编号排序
 */
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
public class DocumentListCompletenessPropertyTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Random random = new Random();

    /**
     * 数据提供器：生成随机的文档集合
     * 每次测试运行100次迭代，使用不同的随机文档数据
     */
    @DataProvider(name = "randomDocumentSets")
    public Object[][] provideRandomDocumentSets() {
        Object[][] dataSets = new Object[100][];
        for (int i = 0; i < 100; i++) {
            dataSets[i] = new Object[]{generateRandomDocumentSet()};
        }
        return dataSets;
    }

    /**
     * 属性测试：文档列表完整性
     * 
     * 测试逻辑：
     * 1. 在数据库中插入随机生成的文档集合
     * 2. 调用DocumentService.getAllDocuments()获取文档列表
     * 3. 验证返回的列表包含所有插入的文档
     * 4. 验证每个文档的完整信息（编号、名称、类型、状态）
     * 5. 验证文档按编号排序
     */
    @Test(dataProvider = "randomDocumentSets")
    public void testDocumentListCompleteness(List<TestDocument> testDocuments) {
        // 清理数据库，确保测试环境干净
        cleanDatabase();

        // 插入测试文档到数据库
        insertTestDocuments(testDocuments);

        // 调用服务获取文档列表
        List<Document> actualDocuments = documentService.getAllDocuments();

        // 验证文档数量一致性
        assertEquals(actualDocuments.size(), testDocuments.size(), 
            "返回的文档数量应该与插入的文档数量一致");

        // 验证文档按编号排序
        verifyDocumentsSortedByCode(actualDocuments);

        // 验证每个文档的完整信息
        verifyDocumentCompleteness(actualDocuments, testDocuments);

        // 验证所有插入的文档都在返回列表中
        verifyAllDocumentsPresent(actualDocuments, testDocuments);
    }

    /**
     * 边界测试：空文档列表的完整性
     */
    @Test
    public void testEmptyDocumentListCompleteness() {
        // 清理数据库
        cleanDatabase();

        // 获取空文档列表
        List<Document> documents = documentService.getAllDocuments();

        // 验证返回空列表而不是null
        assertNotNull(documents, "文档列表不应该为null");
        assertTrue(documents.isEmpty(), "空数据库应该返回空文档列表");
    }

    /**
     * 边界测试：单个文档的完整性
     */
    @Test(invocationCount = 10)
    public void testSingleDocumentListCompleteness() {
        // 清理数据库
        cleanDatabase();

        // 插入单个随机文档
        TestDocument testDoc = generateRandomDocument("DOC001");
        insertTestDocument(testDoc);

        // 获取文档列表
        List<Document> documents = documentService.getAllDocuments();

        // 验证单个文档的完整性
        assertEquals(documents.size(), 1, "应该返回一个文档");
        
        Document actualDoc = documents.get(0);
        assertEquals(actualDoc.getDocumentCode(), testDoc.documentCode, "文档编号应该一致");
        assertEquals(actualDoc.getDocumentName(), testDoc.documentName, "文档名称应该一致");
        assertEquals(actualDoc.getDocumentType(), testDoc.documentType, "文档类型应该一致");
        assertEquals(actualDoc.getStatus(), testDoc.status, "文档状态应该一致");
        
        // 验证必要字段不为空
        assertNotNull(actualDoc.getDocumentCode(), "文档编号不应该为空");
        assertNotNull(actualDoc.getDocumentName(), "文档名称不应该为空");
        assertNotNull(actualDoc.getDocumentType(), "文档类型不应该为空");
        assertNotNull(actualDoc.getStatus(), "文档状态不应该为空");
    }

    /**
     * 验证文档按编号排序
     */
    private void verifyDocumentsSortedByCode(List<Document> documents) {
        for (int i = 1; i < documents.size(); i++) {
            String prevCode = documents.get(i - 1).getDocumentCode();
            String currentCode = documents.get(i).getDocumentCode();
            
            assertTrue(prevCode.compareTo(currentCode) <= 0, 
                "文档应该按编号排序，但发现 '" + prevCode + "' 排在 '" + currentCode + "' 之前");
        }
    }

    /**
     * 验证每个文档的完整信息
     */
    private void verifyDocumentCompleteness(List<Document> actualDocuments, List<TestDocument> expectedDocuments) {
        for (Document actualDoc : actualDocuments) {
            // 验证必要字段不为空
            assertNotNull(actualDoc.getDocumentCode(), "文档编号不应该为空");
            assertNotNull(actualDoc.getDocumentName(), "文档名称不应该为空");
            assertNotNull(actualDoc.getDocumentType(), "文档类型不应该为空");
            assertNotNull(actualDoc.getStatus(), "文档状态不应该为空");
            
            // 验证字段不为空字符串
            assertFalse(actualDoc.getDocumentCode().trim().isEmpty(), "文档编号不应该为空字符串");
            assertFalse(actualDoc.getDocumentName().trim().isEmpty(), "文档名称不应该为空字符串");
            assertFalse(actualDoc.getDocumentType().trim().isEmpty(), "文档类型不应该为空字符串");
            assertFalse(actualDoc.getStatus().trim().isEmpty(), "文档状态不应该为空字符串");
        }
    }

    /**
     * 验证所有插入的文档都在返回列表中
     */
    private void verifyAllDocumentsPresent(List<Document> actualDocuments, List<TestDocument> expectedDocuments) {
        for (TestDocument expectedDoc : expectedDocuments) {
            boolean found = false;
            for (Document actualDoc : actualDocuments) {
                if (expectedDoc.documentCode.equals(actualDoc.getDocumentCode())) {
                    // 验证文档信息完全匹配
                    assertEquals(actualDoc.getDocumentName(), expectedDoc.documentName, 
                        "文档 " + expectedDoc.documentCode + " 的名称应该匹配");
                    assertEquals(actualDoc.getDocumentType(), expectedDoc.documentType, 
                        "文档 " + expectedDoc.documentCode + " 的类型应该匹配");
                    assertEquals(actualDoc.getStatus(), expectedDoc.status, 
                        "文档 " + expectedDoc.documentCode + " 的状态应该匹配");
                    found = true;
                    break;
                }
            }
            assertTrue(found, "插入的文档 " + expectedDoc.documentCode + " 应该在返回列表中");
        }
    }

    /**
     * 生成随机文档集合
     */
    private List<TestDocument> generateRandomDocumentSet() {
        List<TestDocument> documents = new ArrayList<>();
        
        // 生成随机数量的文档（0-20个）
        int documentCount = random.nextInt(21);
        
        for (int i = 0; i < documentCount; i++) {
            String documentCode = "DOC" + String.format("%03d", i + 1);
            documents.add(generateRandomDocument(documentCode));
        }
        
        return documents;
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
     * 清理数据库
     */
    private void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM borrowing_records");
        jdbcTemplate.update("DELETE FROM documents");
    }

    /**
     * 插入测试文档集合到数据库
     */
    private void insertTestDocuments(List<TestDocument> testDocuments) {
        for (TestDocument doc : testDocuments) {
            insertTestDocument(doc);
        }
    }

    /**
     * 插入单个测试文档到数据库
     */
    private void insertTestDocument(TestDocument doc) {
        jdbcTemplate.update(
            "INSERT INTO documents (document_code, document_name, document_type, status) VALUES (?, ?, ?, ?)",
            doc.documentCode, doc.documentName, doc.documentType, doc.status
        );
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
}