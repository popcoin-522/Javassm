-- Test Database Schema for H2 Database
-- This script creates the necessary tables for testing with H2 in-memory database

-- Create documents table for testing
CREATE TABLE IF NOT EXISTS documents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_code VARCHAR(20) NOT NULL UNIQUE,
    document_name VARCHAR(100) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT '可下载',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for documents table
CREATE INDEX IF NOT EXISTS idx_doc_code ON documents(document_code);
CREATE INDEX IF NOT EXISTS idx_doc_status ON documents(status);
CREATE INDEX IF NOT EXISTS idx_doc_created_time ON documents(created_time);

-- Create borrowing_records table for testing
CREATE TABLE IF NOT EXISTS borrowing_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_code VARCHAR(20) NOT NULL,
    user_id VARCHAR(20) NOT NULL,
    user_name VARCHAR(10) NOT NULL,
    borrow_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_time TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL DEFAULT '借阅中'
);

-- Create indexes for borrowing_records table
CREATE INDEX IF NOT EXISTS idx_br_document_code ON borrowing_records(document_code);
CREATE INDEX IF NOT EXISTS idx_br_user_id ON borrowing_records(user_id);
CREATE INDEX IF NOT EXISTS idx_br_borrow_time ON borrowing_records(borrow_time);
CREATE INDEX IF NOT EXISTS idx_br_status ON borrowing_records(status);

-- Add foreign key constraint (H2 syntax)
ALTER TABLE borrowing_records ADD CONSTRAINT IF NOT EXISTS fk_borrowing_document 
    FOREIGN KEY (document_code) REFERENCES documents(document_code);