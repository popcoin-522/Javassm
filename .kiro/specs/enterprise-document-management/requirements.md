# 需求文档

## 介绍

企业文档管理系统是为中小企业或高校项目组设计的轻量级Web文档管理平台，主要用于对企业内部的重要电子文档（如合同、方案、报告、技术资料等）进行信息化管理。系统通过简单的增删改查功能，实现文档信息、借阅/下载记录的基础管理，帮助管理人员掌握文档状态、规范使用流程。

## 术语表

- **Document_Management_System**: 企业文档管理系统的主要应用程序
- **Document**: 系统中管理的电子文档实体，包含编号、名称、类型和状态
- **Borrowing_Record**: 文档借阅记录，包含借阅人信息、借阅时间和归还时间
- **Document_Status**: 文档状态，包括"可下载"、"已借出"、"归档"三种状态
- **User**: 系统用户，通过工号/学号和姓名标识

## 需求

### 需求 1

**用户故事:** 作为管理员，我想要查看所有文档的基本信息，以便了解当前文档库的状态。

#### 验收标准

1. WHEN 管理员请求查看文档列表 THEN Document_Management_System SHALL 在Web页面显示所有文档的编号、名称、类型和状态信息
2. WHEN 文档列表为空 THEN Document_Management_System SHALL 在Web页面显示"暂无文档"提示信息
3. WHEN 文档列表包含多个文档 THEN Document_Management_System SHALL 在Web页面按文档编号顺序显示所有文档

### 需求 2

**用户故事:** 作为管理员，我想要维护文档信息，以便管理文档库的内容。

#### 验收标准

1. WHEN 管理员添加新文档 THEN Document_Management_System SHALL 创建包含编号、名称、类型的文档记录，并设置状态为"可下载"
2. WHEN 管理员修改文档信息 THEN Document_Management_System SHALL 更新指定文档的名称、类型或状态
3. WHEN 管理员删除归档文档 THEN Document_Management_System SHALL 仅删除状态为"归档"且无关联借阅记录的文档
4. WHEN 管理员尝试删除非归档文档 THEN Document_Management_System SHALL 拒绝删除并提示"只能删除归档状态的文档"
5. WHEN 管理员尝试删除有借阅记录的文档 THEN Document_Management_System SHALL 拒绝删除并提示"存在借阅记录，无法删除"

### 需求 3

**用户故事:** 作为用户，我想要借阅文档，以便获取所需的文档资料。

#### 验收标准

1. WHEN 用户提供有效的工号/学号、姓名和文档编号进行借阅 THEN Document_Management_System SHALL 检查文档状态是否为"可下载"
2. WHEN 文档状态为"可下载" THEN Document_Management_System SHALL 将文档状态更改为"已借出"并创建借阅记录
3. WHEN 文档状态为"已借出" THEN Document_Management_System SHALL 拒绝借阅并提示"文档已借出"
4. WHEN 文档状态为"归档" THEN Document_Management_System SHALL 拒绝借阅并提示"文档已归档不可借阅"
5. WHEN 借阅成功 THEN Document_Management_System SHALL 记录文档编号、工号/学号、姓名和借阅时间

### 需求 4

**用户故事:** 作为用户，我想要归还文档，以便其他用户可以继续使用该文档。

#### 验收标准

1. WHEN 用户提供工号/学号、姓名和文档编号进行归还 THEN Document_Management_System SHALL 验证借阅记录是否存在
2. WHEN 借阅记录存在且文档状态为"已借出" THEN Document_Management_System SHALL 将文档状态更改为"可下载"并更新归还时间
3. WHEN 借阅记录不存在 THEN Document_Management_System SHALL 拒绝归还并提示"无此借阅记录"
4. WHEN 归还成功 THEN Document_Management_System SHALL 在借阅记录中记录归还时间

### 需求 5

**用户故事:** 作为管理员，我想要查看所有借阅记录，以便监控文档的使用情况。

#### 验收标准

1. WHEN 管理员请求查看借阅记录 THEN Document_Management_System SHALL 在Web页面显示所有借阅记录的文档编号、工号/学号、姓名、借阅时间和归还时间
2. WHEN 文档未归还 THEN Document_Management_System SHALL 在Web页面的归还时间字段显示"-"
3. WHEN 借阅记录为空 THEN Document_Management_System SHALL 在Web页面显示"暂无借阅记录"提示信息

### 需求 6

**用户故事:** 作为系统，我需要持久化存储数据，以便在程序重启后保持数据完整性。

#### 验收标准

1. WHEN 用户退出程序 THEN Document_Management_System SHALL 将文档列表和借阅记录保存到数据库
2. WHEN 程序启动 THEN Document_Management_System SHALL 从数据库读取文档列表和借阅记录
3. WHEN 数据库连接失败 THEN Document_Management_System SHALL 提示"数据库连接失败"并使用默认空数据
4. WHEN 数据保存失败 THEN Document_Management_System SHALL 提示"数据保存失败，请检查数据库连接"

### 需求 7

**用户故事:** 作为系统，我需要验证用户输入，以便确保数据的有效性和系统的稳定性。

#### 验收标准

1. WHEN 用户输入文档编号 THEN Document_Management_System SHALL 验证编号为非空字符串且长度不超过20个字符
2. WHEN 用户输入工号/学号 THEN Document_Management_System SHALL 验证为非空字符串且长度不超过20个字符
3. WHEN 用户输入姓名 THEN Document_Management_System SHALL 验证为中文/英文组合且长度不超过10个字符
4. WHEN 输入验证失败 THEN Document_Management_System SHALL 在Web页面提示具体的错误信息并要求重新输入
5. WHEN 发生系统异常 THEN Document_Management_System SHALL 捕获异常并在Web页面提示友好的错误信息，避免程序崩溃

### 需求 8

**用户故事:** 作为用户，我想要通过简洁的Web界面操作系统，以便高效地完成文档管理任务。

#### 验收标准

1. WHEN 用户访问系统 THEN Document_Management_System SHALL 显示Web主页面，包含查看文档、借阅文档、归还文档、查看记录等功能模块
2. WHEN 用户进行关键操作 THEN Document_Management_System SHALL 在Web页面提供明确的表单输入和操作结果反馈
3. WHEN 系统处理Web请求 THEN Document_Management_System SHALL 在2秒内完成响应（适用于小规模数据）
4. WHEN 用户在Web页面执行操作 THEN Document_Management_System SHALL 显示操作结果并更新页面内容