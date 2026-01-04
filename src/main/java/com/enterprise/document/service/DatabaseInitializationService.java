package com.enterprise.document.service;

import com.enterprise.document.mapper.DocumentMapper;
import com.enterprise.document.mapper.BorrowingRecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库初始化服务
 * 负责检测数据库状态并执行必要的初始化操作
 */
@Service
public class DatabaseInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializationService.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private DocumentMapper documentMapper;

    @Autowired(required = false)
    private BorrowingRecordMapper borrowingRecordMapper;

    /**
     * 系统启动时的数据库初始化检测
     */
    @PostConstruct
    @Transactional
    public void initializeDatabase() {
        try {
            logger.info("开始数据库初始化检测...");
            
            // 等待Spring JDBC初始化完成，然后检测数据
            Thread.sleep(100); // 短暂等待确保表已创建
            
            // 检测是否为首次运行
            if (isFirstRun()) {
                logger.info("未检测到历史数据，将初始化空文档表和借阅记录表");
                // 数据库表已通过DDL脚本创建，无需插入初始数据
            } else {
                logger.info("检测到历史数据，正在加载...");
                logDataStatistics();
            }

            logger.info("数据库初始化检测完成");

        } catch (Exception e) {
            logger.error("数据库初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    /**
     * 检查数据库表是否存在
     */
    private boolean tablesExist() {
        try {
            // 尝试查询documents表的记录数
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents", Integer.class);
            // 尝试查询borrowing_records表的记录数
            jdbcTemplate.queryForObject("SELECT COUNT(*) FROM borrowing_records", Integer.class);
            return true;
        } catch (Exception e) {
            logger.debug("数据库表不存在或查询失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 创建数据库表
     */
    private void createTables() {
        try (Connection connection = dataSource.getConnection()) {
            ClassPathResource schemaResource = new ClassPathResource("schema.sql");
            ScriptUtils.executeSqlScript(connection, schemaResource);
            logger.info("数据库表结构创建成功");
        } catch (SQLException e) {
            logger.error("创建数据库表失败: {}", e.getMessage(), e);
            throw new RuntimeException("创建数据库表失败", e);
        }
    }

    /**
     * 检测是否为首次运行
     */
    private boolean isFirstRun() {
        try {
            if (documentMapper == null || borrowingRecordMapper == null) {
                logger.warn("Mapper未初始化，跳过数据检测");
                return true;
            }

            int documentCount = countDocuments();
            int recordCount = countBorrowingRecords();
            
            return documentCount == 0 && recordCount == 0;
        } catch (Exception e) {
            logger.warn("检测首次运行状态失败，假设为首次运行: {}", e.getMessage());
            return true;
        }
    }

    /**
     * 统计文档数量
     */
    private int countDocuments() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents", Integer.class);
        } catch (Exception e) {
            logger.warn("统计文档数量失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 统计借阅记录数量
     */
    private int countBorrowingRecords() {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM borrowing_records", Integer.class);
        } catch (Exception e) {
            logger.warn("统计借阅记录数量失败: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 记录数据统计信息
     */
    private void logDataStatistics() {
        try {
            int documentCount = countDocuments();
            int recordCount = countBorrowingRecords();
            logger.info("当前数据统计 - 文档数量: {}, 借阅记录数量: {}", documentCount, recordCount);
        } catch (Exception e) {
            logger.warn("记录数据统计失败: {}", e.getMessage());
        }
    }

    /**
     * 获取数据库连接状态
     */
    public boolean isDatabaseConnected() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5);
        } catch (SQLException e) {
            logger.error("数据库连接检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 手动触发数据库初始化（用于测试或维护）
     */
    public void reinitializeDatabase() {
        logger.info("手动触发数据库重新初始化");
        initializeDatabase();
    }
}