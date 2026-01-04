package com.enterprise.document.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * 数据库初始化服务测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/applicationContext-test.xml"})
@Transactional
public class DatabaseInitializationServiceTest {

    @Autowired
    private DatabaseInitializationService databaseInitializationService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        // 测试数据库连接是否正常
        assertTrue("数据库连接应该正常", databaseInitializationService.isDatabaseConnected());
    }

    @Test
    public void testTablesCreated() {
        // 测试数据库表是否已创建
        Integer documentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents", Integer.class);
        Integer recordCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM borrowing_records", Integer.class);
        
        assertNotNull("documents表应该存在", documentCount);
        assertNotNull("borrowing_records表应该存在", recordCount);
        assertEquals("初始文档数量应该为0", Integer.valueOf(0), documentCount);
        assertEquals("初始借阅记录数量应该为0", Integer.valueOf(0), recordCount);
    }

    @Test
    public void testTableStructure() {
        // 测试表结构是否正确 - 通过插入测试数据验证
        try {
            // 测试documents表结构
            jdbcTemplate.update(
                "INSERT INTO documents (document_code, document_name, document_type, status) VALUES (?, ?, ?, ?)",
                "TEST001", "测试文档", "合同", "可下载"
            );
            
            // 测试borrowing_records表结构
            jdbcTemplate.update(
                "INSERT INTO borrowing_records (document_code, user_id, user_name, status) VALUES (?, ?, ?, ?)",
                "TEST001", "U001", "张三", "借阅中"
            );
            
            // 验证数据插入成功
            Integer documentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents", Integer.class);
            Integer recordCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM borrowing_records", Integer.class);
            
            assertEquals("应该有1条文档记录", Integer.valueOf(1), documentCount);
            assertEquals("应该有1条借阅记录", Integer.valueOf(1), recordCount);
            
        } catch (Exception e) {
            fail("表结构测试失败: " + e.getMessage());
        }
    }

    @Test
    public void testReinitializeDatabase() {
        // 测试手动重新初始化数据库
        try {
            databaseInitializationService.reinitializeDatabase();
            assertTrue("重新初始化后数据库连接应该正常", databaseInitializationService.isDatabaseConnected());
        } catch (Exception e) {
            fail("重新初始化数据库失败: " + e.getMessage());
        }
    }
}