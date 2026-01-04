package com.enterprise.document.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 数据库自动初始化器
 * 在应用启动时自动创建数据库表结构和插入示例数据
 */
@Component
@PropertySource("classpath:database-init.properties")
public class DatabaseAutoInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAutoInitializer.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${database.auto.init.enabled:true}")
    private boolean autoInitEnabled;

    @Value("${database.auto.init.recreate:false}")
    private boolean recreateTables;

    @Value("${database.auto.init.sample.data:true}")
    private boolean insertSampleData;

    @Value("${database.init.schema.script:classpath:sql/schema.sql}")
    private String schemaScript;

    @Value("${database.init.data.script:classpath:sql/data.sql}")
    private String dataScript;

    @PostConstruct
    public void initializeDatabase() {
        if (!autoInitEnabled) {
            logger.info("数据库自动初始化已禁用");
            return;
        }

        logger.info("开始数据库自动初始化...");

        try {
            // 检查是否需要重新创建表
            if (recreateTables) {
                logger.info("重新创建数据库表...");
                dropTablesIfExists();
            }

            // 执行表结构创建脚本
            executeSchemaScript();

            // 插入示例数据
            if (insertSampleData) {
                executeDataScript();
            }

            logger.info("数据库自动初始化完成");

        } catch (Exception e) {
            logger.error("数据库初始化失败", e);
            throw new RuntimeException("数据库初始化失败", e);
        }
    }

    /**
     * 执行表结构创建脚本
     */
    private void executeSchemaScript() {
        try {
            logger.info("执行数据库表结构创建脚本: {}", schemaScript);
            
            ClassPathResource resource = new ClassPathResource("sql/schema.sql");
            if (resource.exists()) {
                try (Connection connection = dataSource.getConnection()) {
                    // 设置连接字符编码
                    connection.prepareStatement("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci").execute();
                    connection.prepareStatement("SET CHARACTER SET utf8mb4").execute();
                    
                    // 设置忽略错误模式，允许重复创建索引等操作
                    connection.setAutoCommit(false);
                    
                    try {
                        ScriptUtils.executeSqlScript(connection, resource);
                        connection.commit();
                        logger.info("数据库表结构创建成功");
                    } catch (Exception e) {
                        connection.rollback();
                        // 如果是索引已存在的错误，我们可以忽略
                        if (e.getMessage().contains("Duplicate key name") || 
                            e.getMessage().contains("already exists")) {
                            logger.warn("索引或约束已存在，继续执行: {}", e.getMessage());
                            // 尝试逐条执行SQL语句
                            executeSchemaScriptLineByLine(connection);
                        } else {
                            throw e;
                        }
                    }
                }
            } else {
                logger.warn("表结构脚本文件不存在: {}", schemaScript);
            }
        } catch (SQLException e) {
            logger.error("执行表结构脚本失败", e);
            throw new RuntimeException("执行表结构脚本失败", e);
        }
    }

    /**
     * 逐行执行SQL脚本，忽略重复创建错误
     */
    private void executeSchemaScriptLineByLine(Connection connection) throws SQLException {
        try {
            // 创建表的SQL语句
            String[] createTableSqls = {
                "CREATE TABLE IF NOT EXISTS documents (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID'," +
                "document_code VARCHAR(20) NOT NULL UNIQUE COMMENT '文档编号'," +
                "document_name VARCHAR(100) NOT NULL COMMENT '文档名称'," +
                "document_type VARCHAR(50) NOT NULL COMMENT '文档类型'," +
                "status VARCHAR(20) NOT NULL DEFAULT '可下载' COMMENT '文档状态：可下载、已借出、归档'," +
                "created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                "updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档信息表'",
                
                "CREATE TABLE IF NOT EXISTS borrowing_records (" +
                "id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID'," +
                "document_code VARCHAR(20) NOT NULL COMMENT '文档编号'," +
                "user_id VARCHAR(20) NOT NULL COMMENT '工号/学号'," +
                "user_name VARCHAR(10) NOT NULL COMMENT '用户姓名'," +
                "borrow_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间'," +
                "return_time TIMESTAMP NULL COMMENT '归还时间'," +
                "status VARCHAR(20) NOT NULL DEFAULT '借阅中' COMMENT '借阅状态：借阅中、已归还'" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表'"
            };
            
            // 创建索引的SQL语句（可能失败）
            String[] createIndexSqls = {
                "CREATE INDEX idx_doc_code ON documents(document_code)",
                "CREATE INDEX idx_doc_status ON documents(status)",
                "CREATE INDEX idx_doc_created_time ON documents(created_time)",
                "CREATE INDEX idx_br_document_code ON borrowing_records(document_code)",
                "CREATE INDEX idx_br_user_id ON borrowing_records(user_id)",
                "CREATE INDEX idx_br_borrow_time ON borrowing_records(borrow_time)",
                "CREATE INDEX idx_br_status ON borrowing_records(status)"
            };
            
            // 外键约束SQL（可能失败）
            String[] constraintSqls = {
                "ALTER TABLE borrowing_records ADD CONSTRAINT fk_borrowing_document " +
                "FOREIGN KEY (document_code) REFERENCES documents(document_code) ON DELETE RESTRICT ON UPDATE CASCADE"
            };
            
            // 执行创建表语句
            for (String sql : createTableSqls) {
                try {
                    connection.prepareStatement(sql).execute();
                    logger.debug("执行成功: {}", sql.substring(0, Math.min(50, sql.length())) + "...");
                } catch (SQLException e) {
                    logger.warn("执行失败（可能已存在）: {} - {}", sql.substring(0, Math.min(50, sql.length())), e.getMessage());
                }
            }
            
            // 执行创建索引语句（允许失败）
            for (String sql : createIndexSqls) {
                try {
                    connection.prepareStatement(sql).execute();
                    logger.debug("索引创建成功: {}", sql);
                } catch (SQLException e) {
                    logger.debug("索引已存在或创建失败: {} - {}", sql, e.getMessage());
                }
            }
            
            // 执行外键约束语句（允许失败）
            for (String sql : constraintSqls) {
                try {
                    connection.prepareStatement(sql).execute();
                    logger.debug("约束创建成功: {}", sql);
                } catch (SQLException e) {
                    logger.debug("约束已存在或创建失败: {} - {}", sql, e.getMessage());
                }
            }
            
            connection.commit();
            logger.info("数据库表结构创建完成（部分索引可能已存在）");
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    /**
     * 执行示例数据插入脚本
     */
    private void executeDataScript() {
        try {
            // 检查是否已有数据
            int documentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents", Integer.class);
            if (documentCount > 0) {
                logger.info("检测到已有文档数据 ({} 条)，跳过示例数据插入", documentCount);
                return;
            }

            logger.info("执行示例数据插入脚本: {}", dataScript);
            
            ClassPathResource resource = new ClassPathResource("sql/data.sql");
            if (resource.exists()) {
                try (Connection connection = dataSource.getConnection()) {
                    // 设置连接字符编码
                    connection.prepareStatement("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci").execute();
                    connection.prepareStatement("SET CHARACTER SET utf8mb4").execute();
                    
                    ScriptUtils.executeSqlScript(connection, resource);
                    logger.info("示例数据插入成功");
                }
            } else {
                logger.warn("示例数据脚本文件不存在: {}", dataScript);
            }
        } catch (Exception e) {
            logger.error("执行示例数据脚本失败", e);
            throw new RuntimeException("执行示例数据脚本失败", e);
        }
    }

    /**
     * 删除已存在的表（仅在开发环境使用）
     */
    private void dropTablesIfExists() {
        try {
            logger.warn("删除已存在的数据库表（开发模式）");
            
            // 先删除有外键约束的表
            jdbcTemplate.execute("DROP TABLE IF EXISTS borrowing_records");
            jdbcTemplate.execute("DROP TABLE IF EXISTS documents");
            
            logger.info("已删除所有数据库表");
        } catch (Exception e) {
            logger.error("删除数据库表失败", e);
            // 不抛出异常，继续执行创建表的操作
        }
    }

    /**
     * 检查表是否存在
     */
    private boolean tableExists(String tableName) {
        try {
            String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class, tableName);
            return count != null && count > 0;
        } catch (Exception e) {
            logger.debug("检查表是否存在时出错: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 获取数据库初始化状态信息
     */
    public String getInitializationStatus() {
        try {
            int documentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents", Integer.class);
            int borrowingCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM borrowing_records", Integer.class);
            
            return String.format("数据库初始化状态: 文档表 %d 条记录, 借阅记录表 %d 条记录", 
                    documentCount, borrowingCount);
        } catch (Exception e) {
            return "数据库状态检查失败: " + e.getMessage();
        }
    }
}