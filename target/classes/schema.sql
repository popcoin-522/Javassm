-- Enterprise Document Management System Database Schema
-- This script creates the necessary tables for the document management system

-- Create documents table
CREATE TABLE IF NOT EXISTS documents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_code VARCHAR(20) NOT NULL UNIQUE COMMENT '文档编号',
    document_name VARCHAR(100) NOT NULL COMMENT '文档名称',
    document_type VARCHAR(50) NOT NULL COMMENT '文档类型',
    status VARCHAR(20) NOT NULL DEFAULT '可下载' COMMENT '文档状态：可下载、已借出、归档',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档信息表';

-- Create indexes for documents table
CREATE INDEX IF NOT EXISTS idx_doc_code ON documents(document_code);
CREATE INDEX IF NOT EXISTS idx_doc_status ON documents(status);
CREATE INDEX IF NOT EXISTS idx_doc_created_time ON documents(created_time);

-- Create borrowing_records table
CREATE TABLE IF NOT EXISTS borrowing_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_code VARCHAR(20) NOT NULL COMMENT '文档编号',
    user_id VARCHAR(20) NOT NULL COMMENT '工号/学号',
    user_name VARCHAR(10) NOT NULL COMMENT '用户姓名',
    borrow_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    return_time TIMESTAMP NULL COMMENT '归还时间',
    status VARCHAR(20) NOT NULL DEFAULT '借阅中' COMMENT '借阅状态：借阅中、已归还'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='借阅记录表';

-- Create indexes for borrowing_records table
CREATE INDEX IF NOT EXISTS idx_br_document_code ON borrowing_records(document_code);
CREATE INDEX IF NOT EXISTS idx_br_user_id ON borrowing_records(user_id);
CREATE INDEX IF NOT EXISTS idx_br_borrow_time ON borrowing_records(borrow_time);
CREATE INDEX IF NOT EXISTS idx_br_status ON borrowing_records(status);

-- Add foreign key constraint
ALTER TABLE borrowing_records ADD CONSTRAINT IF NOT EXISTS fk_borrowing_document 
    FOREIGN KEY (document_code) REFERENCES documents(document_code) ON DELETE RESTRICT ON UPDATE CASCADE;