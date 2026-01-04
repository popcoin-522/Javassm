-- 企业文档管理系统数据库表结构
-- Enterprise Document Management System Database Schema
-- 此脚本会在应用启动时自动执行

-- 创建文档表
CREATE TABLE IF NOT EXISTS documents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    document_code VARCHAR(20) NOT NULL UNIQUE COMMENT '文档编号',
    document_name VARCHAR(100) NOT NULL COMMENT '文档名称',
    document_type VARCHAR(50) NOT NULL COMMENT '文档类型',
    status VARCHAR(20) NOT NULL DEFAULT '可下载' COMMENT '文档状态：可下载、已借出、归档',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档信息表';

-- 创建文档表索引（兼容MySQL 5.7+）
CREATE INDEX idx_doc_code ON documents(document_code);
CREATE INDEX idx_doc_status ON documents(status);
CREATE INDEX idx_doc_created_time ON documents(created_time);

-- 创建借阅记录表
CREATE TABLE IF NOT EXISTS borrowing_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    document_code VARCHAR(20) NOT NULL COMMENT '文档编号',
    user_id VARCHAR(20) NOT NULL COMMENT '工号/学号',
    user_name VARCHAR(10) NOT NULL COMMENT '用户姓名',
    borrow_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    return_time TIMESTAMP NULL COMMENT '归还时间',
    status VARCHAR(20) NOT NULL DEFAULT '借阅中' COMMENT '借阅状态：借阅中、已归还'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- 创建借阅记录表索引（兼容MySQL 5.7+）
CREATE INDEX idx_br_document_code ON borrowing_records(document_code);
CREATE INDEX idx_br_user_id ON borrowing_records(user_id);
CREATE INDEX idx_br_borrow_time ON borrowing_records(borrow_time);
CREATE INDEX idx_br_status ON borrowing_records(status);

-- 添加外键约束（兼容MySQL 5.7+）
ALTER TABLE borrowing_records ADD CONSTRAINT fk_borrowing_document 
    FOREIGN KEY (document_code) REFERENCES documents(document_code) ON DELETE RESTRICT ON UPDATE CASCADE;