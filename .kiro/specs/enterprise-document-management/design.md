# 设计文档

## 概述

企业文档管理系统是一个基于Spring、SpringMVC和MyBatis的Web应用程序，采用经典的三层架构模式。系统提供文档信息管理、借阅记录管理和数据持久化功能，通过Web界面为用户提供直观的操作体验。

## 架构

### 整体架构
系统采用经典的MVC（Model-View-Controller）三层架构：

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Presentation  │    │    Business     │    │   Persistence   │
│     Layer       │    │     Layer       │    │     Layer       │
│                 │    │                 │    │                 │
│  - JSP Pages    │◄──►│  - Controllers  │◄──►│  - MyBatis      │
│  - Static Files │    │  - Services     │    │  - Mappers      │
│  - Forms        │    │  - Validators   │    │  - Database     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 技术栈
- **Web层**: SpringMVC + JSP + Bootstrap
- **业务层**: Spring Core + Spring Transaction
- **持久层**: MyBatis + MySQL
- **构建工具**: Maven
- **应用服务器**: Tomcat

## 组件和接口

### 控制器层 (Controller Layer)
- `DocumentController`: 处理文档相关的Web请求
- `BorrowingController`: 处理借阅相关的Web请求
- `HomeController`: 处理主页和导航请求

### 服务层 (Service Layer)
- `DocumentService`: 文档业务逻辑处理
- `BorrowingService`: 借阅业务逻辑处理
- `ValidationService`: 输入验证服务

### 数据访问层 (DAO Layer)
- `DocumentMapper`: 文档数据访问接口
- `BorrowingRecordMapper`: 借阅记录数据访问接口

### 模型层 (Model Layer)
- `Document`: 文档实体类
- `BorrowingRecord`: 借阅记录实体类
- `User`: 用户信息类

## 数据模型

### 文档表 (documents)
```sql
CREATE TABLE documents (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_code VARCHAR(20) NOT NULL UNIQUE,
    document_name VARCHAR(100) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT '可下载',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 借阅记录表 (borrowing_records)
```sql
CREATE TABLE borrowing_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_code VARCHAR(20) NOT NULL,
    user_id VARCHAR(20) NOT NULL,
    user_name VARCHAR(10) NOT NULL,
    borrow_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_time TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL DEFAULT '借阅中',
    FOREIGN KEY (document_code) REFERENCES documents(document_code)
);
```

### 实体关系
- 一个文档可以有多个借阅记录（一对多关系）
- 借阅记录通过document_code与文档关联

### 数据库初始化策略
系统启动时的数据库初始化逻辑：

1. **表结构检测**：
   - 使用Spring Boot的自动配置检测数据库表是否存在
   - 如果表不存在，自动创建documents和borrowing_records表

2. **首次运行检测**：
   ```java
   @PostConstruct
   public void initializeDatabase() {
       if (documentMapper.countDocuments() == 0 && borrowingRecordMapper.countRecords() == 0) {
           logger.info("未检测到历史数据，将初始化空文档表和借阅记录表");
           // 数据库表已通过DDL脚本创建，无需插入初始数据
       } else {
           logger.info("检测到历史数据，正在加载...");
       }
   }
   ```

3. **DDL脚本管理**：
   - 使用MyBatis的schema.sql在应用启动时自动执行建表语句
   - 支持不同数据库的DDL适配（MySQL/H2）
## 正确性属性

*属性是一个特征或行为，应该在系统的所有有效执行中保持为真——本质上，是关于系统应该做什么的正式陈述。属性作为人类可读规范和机器可验证正确性保证之间的桥梁。*

### 属性反思

在完成初始预工作分析后，我识别出以下可能的冗余性：
- 属性3.2和4.2可以合并为一个往返属性：借阅然后归还应该恢复文档到原始状态
- 属性7.1和7.2都是关于字符串长度验证，可以合并为一个通用的输入验证属性
- 属性1.1和5.1都是关于列表显示，可以合并为一个通用的数据显示属性

基于反思，以下是精简后的正确性属性：

**属性 1: 文档列表完整性**
*对于任何*文档集合，查看文档列表应该返回所有文档的完整信息（编号、名称、类型、状态），并按编号排序
**验证: 需求 1.1, 1.3**

**属性 2: 文档创建一致性**
*对于任何*有效的文档信息（编号、名称、类型），创建文档后应该能够在文档列表中找到该文档，且状态为"可下载"
**验证: 需求 2.1**

**属性 3: 文档删除规则**
*对于任何*文档，只有当文档状态为"归档"且无关联借阅记录时，删除操作才能成功；非归档文档或有借阅记录的归档文档删除应该失败并返回相应错误信息
**验证: 需求 2.3, 2.4, 2.5**

**属性 4: 借阅往返一致性**
*对于任何*状态为"可下载"的文档，执行借阅然后归还操作后，文档状态应该恢复为"可下载"
**验证: 需求 3.2, 4.2**

**属性 5: 借阅记录完整性**
*对于任何*成功的借阅操作，系统应该创建包含文档编号、用户信息和借阅时间的完整借阅记录
**验证: 需求 3.5**

**属性 6: 借阅记录显示一致性**
*对于任何*借阅记录集合，查看借阅记录应该显示所有记录的完整信息（文档编号、工号/学号、姓名、借阅时间、归还时间），其中未归还的记录在归还时间字段必须显示"-"
**验证: 需求 5.1, 5.2**

**属性 7: 输入验证一致性**
*对于任何*用户输入，系统应该验证字符串非空且长度符合规定（文档编号/工号≤20字符，姓名≤10字符）
**验证: 需求 7.1, 7.2, 7.3**

**属性 8: 异常处理安全性**
*对于任何*系统异常，系统应该捕获异常并提供友好的错误信息，而不是崩溃
**验证: 需求 7.5**

**属性 9: 数据加载一致性**
*对于任何*存储在数据库中的数据，系统启动时应该正确加载所有文档和借阅记录
**验证: 需求 6.2**

**属性 10: 操作结果反馈**
*对于任何*用户操作，系统应该在Web页面显示操作结果并更新相关内容
**验证: 需求 8.4**
## 错误处理

### 输入验证错误
- 文档编号/工号/学号为空或超过20字符：返回具体验证错误信息
- 姓名格式不正确或超过10字符：返回格式要求提示
- 必填字段缺失：返回字段缺失提示

### 业务逻辑错误
- 借阅不可用文档：返回文档状态说明（已借出/已归档）
- 归还无效记录：返回"无此借阅记录"提示
- 删除受限文档：返回删除限制说明
  - 删除非归档文档：返回"只能删除归档状态的文档"
  - 删除有借阅记录的归档文档：返回"存在借阅记录，无法删除"

### 系统异常处理
- 数据库连接失败：返回"数据库连接失败"提示，使用默认空数据显示
- 数据保存失败：返回"数据保存失败，请检查数据库连接"提示
- 数据读取失败：返回"数据读取失败，将使用初始空表"提示
- 未预期异常：记录日志，返回通用错误提示

### 数据库异常映射
原始需求中的文件操作异常对应的数据库异常处理：
- 文件被占用 → 数据库连接池耗尽：提示"系统繁忙，请稍后重试"
- 文件权限不足 → 数据库权限不足：提示"数据访问权限不足"
- 文件损坏 → 数据库数据损坏：提示"数据读取失败，将使用初始空表"

### 异常处理策略
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public String handleValidation(ValidationException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }
    
    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception e, Model model) {
        logger.error("Unexpected error", e);
        model.addAttribute("error", "系统发生错误，请稍后重试");
        return "error";
    }
}
```

## 测试策略

### 双重测试方法

系统将采用单元测试和基于属性的测试相结合的方法：

**单元测试**：
- 验证具体示例、边界情况和错误条件
- 测试组件间的集成点
- 覆盖关键业务逻辑的具体场景

**基于属性的测试**：
- 使用TestNG作为基于属性的测试库
- 每个属性测试运行最少100次迭代
- 验证跨所有输入的通用属性
- 每个基于属性的测试必须用注释明确引用设计文档中的正确性属性
- 测试标签格式：`**Feature: enterprise-document-management, Property {number}: {property_text}**`

**测试覆盖范围**：
- 单元测试捕获具体错误，基于属性的测试验证一般正确性
- 两种测试类型互补，提供全面覆盖
- 单元测试处理特定示例，基于属性的测试处理大量输入

### 测试环境配置
- 使用H2内存数据库进行测试
- Spring Test Context框架进行集成测试
- MockMvc进行Web层测试
- 测试数据使用Builder模式构建

### 原始需求测试映射
基于原始需求中的测试需求，具体测试案例包括：

**功能测试**：
- 查看文档列表、添加/修改/删除文档功能测试
- 借阅文档、归还文档、查看借阅记录功能测试
- 数据库数据持久化和加载测试

**边界测试**：
- 借阅"归档"文档时的拒绝提示测试
- 归还未借阅文档时的"无此借阅记录"提示测试
- 删除非归档文档的拒绝提示测试
- 删除有借阅记录的归档文档的拒绝提示测试

**错误处理测试**：
- 输入空文档编号/工号时的"不能为空"提示测试
- 输入超长字符串时的长度限制提示测试
- 数据库连接异常时的容错处理测试
- 数据读取失败时的"使用初始空表"提示测试