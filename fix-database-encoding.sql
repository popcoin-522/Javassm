-- 修复数据库字符编码问题
-- 在MySQL中执行此脚本来修复字符集问题

-- 1. 修改数据库字符集
ALTER DATABASE document_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 修改表字符集
ALTER TABLE documents CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE borrowing_records CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 3. 检查当前字符集设置
SHOW VARIABLES LIKE 'character_set%';
SHOW VARIABLES LIKE 'collation%';

-- 4. 查看表结构确认字符集
SHOW CREATE TABLE documents;
SHOW CREATE TABLE borrowing_records;

-- 5. 重新插入正确的中文数据（如果需要）
-- 先清空现有数据
DELETE FROM borrowing_records;
DELETE FROM documents;

-- 重新插入示例数据
INSERT INTO documents (document_code, document_name, document_type, status) VALUES
('DOC001', '企业管理制度汇编', '管理制度', '可下载'),
('DOC002', '项目开发规范', '技术文档', '可下载'),
('DOC003', '财务报表模板', '财务文档', '可下载'),
('DOC004', '员工手册2024版', '人事文档', '已借出'),
('DOC005', '系统架构设计文档', '技术文档', '可下载'),
('DOC006', '合同模板集合', '法务文档', '归档'),
('DOC007', '产品需求说明书', '产品文档', '可下载'),
('DOC008', '质量管理体系文件', '质量文档', '可下载');

INSERT INTO borrowing_records (document_code, user_id, user_name, status, borrow_time) VALUES
('DOC004', 'EMP001', '张三', '借阅中', '2024-12-25 09:30:00'),
('DOC002', 'EMP002', '李四', '已归还', '2024-12-20 14:15:00'),
('DOC001', 'EMP003', '王五', '已归还', '2024-12-18 10:45:00');

-- 更新已归还记录的归还时间
UPDATE borrowing_records SET return_time = '2024-12-22 16:30:00' WHERE document_code = 'DOC002' AND user_id = 'EMP002';
UPDATE borrowing_records SET return_time = '2024-12-19 11:20:00' WHERE document_code = 'DOC001' AND user_id = 'EMP003';

-- 验证数据
SELECT * FROM documents;
SELECT * FROM borrowing_records;