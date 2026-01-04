# 企业文档管理系统 - 合并文档

## 完整系统说明文档.md


## 企业文档管理系统-完整解决方案.md


## 文件说明.md

# 📁 项目文件说明

## 🚀 启动脚本（推荐使用）

| 文件名 | 功能 | 推荐度 | 说明 |
|--------|------|--------|------|
| `quick-run.bat` | 快速启动服务器 | 🥇 **最推荐** | 一键启动，访问 http://localhost:8081 |
| `run-server.bat` | 完整启动流程 | 🥈 推荐 | 带环境检查的启动脚本 |
| `restart-server.bat` | 重启服务器 | 🥈 推荐 | 清理重编译后启动 |
| `run-server.sh` | Linux/Mac启动 | 🥈 推荐 | Unix系统使用 |

## 🔨 构建脚本（传统部署）

| 文件名 | 功能 | 推荐度 | 说明 |
|--------|------|--------|------|
| `quick-build.bat` | 快速构建WAR | 🥉 备选 | 生成WAR文件用于Tomcat部署 |
| `FINAL-BUILD.bat` | 详细构建过程 | ⚠️ 调试用 | 显示详细的构建信息 |
| `fix-css-links.bat` | 修复样式问题 | 🔧 工具 | 修复CSS链接问题 |
| `fix-database-charset.bat` | 修复数据库编码 | 🔧 工具 | 修复中文乱码问题 |
| `build-and-run.sh` | Linux构建脚本 | ⚠️ 备用 | Unix系统构建使用 |

## 📖 说明文档

| 文件名 | 内容 | 重要性 |
|--------|------|--------|
| `README.md` | 项目概述和快速开始 | ⭐⭐⭐ |
| `运行指南.md` | 完整的运行和配置指南 | ⭐⭐⭐ |
| `数据库编码修复指南.md` | 中文乱码问题解决方案 | ⭐⭐⭐ |
| `前端样式修复说明.md` | 页面样式问题解决方案 | ⭐⭐ |
| `TROUBLESHOOTING.md` | 故障排除和问题解决 | ⭐⭐ |
| `README-DATABASE-SETUP.md` | 数据库配置说明 | ⭐ |

## 🎯 使用建议

### 新手用户
1. 直接双击 `quick-run.bat`
2. 如有问题查看 `运行指南.md`

### 开发者
1. 使用 `run-server.bat` 进行完整的环境检查
2. 参考 `运行指南.md` 了解所有配置选项

### 生产部署
1. 使用 `quick-build.bat` 构建WAR文件
2. 部署到外部Tomcat服务器

## 🗂️ 核心目录

- `src/` - 源代码目录
- `target/` - 构建输出目录
- `logs/` - 运行日志目录
- `.kiro/` - Kiro IDE配置目录

---

**💡 提示：大多数情况下，你只需要使用 `quick-run.bat` 就足够了！**

---

## 运行指南.md

# 🚀 企业文档管理系统 - 完整运行指南

## 🎯 推荐运行方式：命令行直接启动

### ⚡ 一键启动（最简单）
```bash
# Windows用户 - 双击运行或命令行执行
quick-run.bat

# Linux/Mac用户
chmod +x run-server.sh
./run-server.sh

# 手动命令
mvn tomcat7:run
```

**访问地址**：`http://localhost:8081/document-management/`

## 📋 前提条件
- ✅ Java 8+ 已安装
- ✅ Maven 3.6+ 已安装  
- ✅ MySQL 5.7+ 或 8.0+ 已安装
- ✅ MySQL 服务已启动

## 🔧 配置步骤

### 1️⃣ 配置数据库连接
编辑 `src/main/resources/database.properties`：
```properties
# 只需修改用户名和密码
jdbc.username=你的MySQL用户名
jdbc.password=你的MySQL密码
```

### 2️⃣ 启动MySQL服务
```bash
# Windows
net start mysql

# 检查服务状态
sc query mysql
```

### 3️⃣ 运行项目
选择以下任一方式：

#### 方式1：快速启动（推荐）
```bash
quick-run.bat
```

#### 方式2：完整启动（带环境检查）
```bash
run-server.bat
```

#### 方式3：手动命令
```bash
mvn tomcat7:run
```

## 🎉 命令行运行优势

| 特性 | 命令行运行 | 传统部署 |
|------|------------|----------|
| **安装要求** | 只需Java + Maven | 需要Java + Maven + Tomcat |
| **启动方式** | 一条命令 | 多步骤部署 |
| **调试便利** | 控制台直接显示日志 | 需要查看Tomcat日志 |
| **开发效率** | 修改代码后快速重启 | 需要重新打包部署 |
| **端口配置** | 8081（避免冲突） | 8080（可能冲突） |

## 📊 可用的批处理文件

| 文件名 | 功能 | 推荐度 | 适用场景 |
|--------|------|--------|----------|
| `quick-run.bat` | 直接启动服务器 | 🥇 最推荐 | 日常开发 |
| `run-server.bat` | 环境检查+启动 | 🥈 推荐 | 首次运行 |
| `quick-build.bat` | 快速构建WAR | 🥉 备选 | 传统部署 |
| `FINAL-BUILD.bat` | 详细构建过程 | ⚠️ 备用 | 调试构建问题 |

## 🔍 运行状态检查

### ✅ 成功启动的标志
看到以下信息表示启动成功：
```
[INFO] Running war on http://localhost:8081/document-management
数据库自动初始化完成
Root WebApplicationContext initialized
Completed initialization
Starting ProtocolHandler ["http-bio-8081"]
```

### 🗄️ 数据库自动初始化
系统会自动：
- ✅ 创建 `document_management` 数据库（如果不存在）
- ✅ 创建所有必需的数据表
- ✅ 插入示例数据（8个文档，3条借阅记录）
- ✅ 创建索引和外键约束

### 📊 示例数据
系统会自动插入以下测试数据：

**文档列表**：
- DOC001: 企业管理制度汇编
- DOC002: 项目开发规范  
- DOC003: 财务报表模板
- DOC004: 员工手册2024版（已借出）
- DOC005: 系统架构设计文档

**借阅记录**：
- 张三借阅员工手册（借阅中）
- 李四借阅项目开发规范（已归还）
- 王五借阅企业管理制度（已归还）

## 🛑 停止服务器
在命令行窗口按 `Ctrl + C` 即可停止服务器。

## 🆘 常见问题解决

### 问题1：中文字符显示乱码或繁体字
```
页面显示繁体字或乱码
```
**解决方案**：
1. 所有bat文件已自动设置UTF-8编码（`chcp 65001`）
2. 如果仍有问题，手动在命令行执行：`chcp 65001`
3. 确保浏览器编码设置为UTF-8

### 问题2：页面样式缺失或显示简陋
```
页面没有按钮样式、卡片效果等，看起来很简单
```
**解决方案**：
1. **网络问题**：CDN无法访问，已创建本地CSS文件
2. **重新启动服务器**：运行 `restart-server.bat` 或 `quick-run.bat`
3. **清除浏览器缓存**：Ctrl+F5 强制刷新页面
4. **检查CSS文件**：确认 `src/main/webapp/css/bootstrap-local.css` 存在

### 问题3：数据库中文数据显示乱码
```
页面其他中文正常，但数据库数据显示为乱码（如：鍟嗗搧绠＄悊）
```
**解决方案**：
1. **自动修复**（推荐）：运行 `fix-database-charset.bat`
2. **重启服务器**：运行 `restart-server.bat`
3. **手动修复**：参考 `数据库编码修复指南.md`
4. **验证修复**：查看页面数据是否正常显示

### 问题4：端口被占用
```
Address already in use: JVM_Bind <null>:8081
```
**解决方案**：
1. 更改端口：编辑 `pom.xml` 中的 `<port>8081</port>`
2. 或者停止占用端口的程序

### 问题3：数据库连接失败
```
Communications link failure
```
**解决方案**：
1. 确保MySQL服务已启动：`net start mysql`
2. 检查 `database.properties` 中的用户名密码
3. 确认数据库端口3306可访问

### 问题4：编译失败
**解决方案**：
1. 确保Java 8+已安装：`java -version`
2. 确保Maven 3.6+已安装：`mvn -version`
3. 运行 `mvn clean compile` 检查错误

### 问题5：批处理文件闪退
**解决方案**：
1. 在命令行中运行：`cmd` → `cd 项目目录` → `quick-run.bat`
2. 使用最稳定的脚本：`quick-run.bat`
3. 手动运行：`mvn tomcat7:run`

## 📋 完整命令参考

```bash
# 清理并编译
mvn clean compile

# 运行测试
mvn test

# 打包（跳过测试）
mvn clean package -DskipTests

# 启动内嵌服务器
mvn tomcat7:run

# 指定端口启动
mvn tomcat7:run -Dmaven.tomcat.port=8082

# 查看帮助
mvn tomcat7:help
```

## 🎯 开发工作流

1. **修改代码** → 保存文件
2. **重启服务器** → `Ctrl+C` 停止，重新运行 `quick-run.bat`
3. **测试功能** → 浏览器刷新页面 `http://localhost:8081/document-management/`
4. **查看日志** → 控制台直接显示

## 🔧 传统部署方式（可选）

如果需要部署到外部Tomcat：

### 1. 构建WAR文件
```bash
quick-build.bat
# 或
mvn clean package -DskipTests
```

### 2. 部署到Tomcat
1. 将 `target/document-management-1.0.0.war` 复制到Tomcat的 `webapps` 目录
2. 启动Tomcat
3. 访问 `http://localhost:8080/document-management/`

## 🛠️ 高级配置

### 自定义数据库初始化
编辑 `src/main/resources/database-init.properties`：
```properties
# 禁用自动初始化
database.auto.init.enabled=false

# 不插入示例数据（生产环境推荐）
database.auto.init.sample.data=false

# 每次启动重新创建表（开发环境）
database.auto.init.recreate=true
```

### 自定义端口
编辑 `pom.xml` 中的Tomcat插件配置：
```xml
<configuration>
    <port>8082</port>  <!-- 修改端口 -->
    <path>/document-management</path>
</configuration>
```

## 📞 获取帮助

如果遇到问题：
1. 📋 查看控制台日志输出
2. 📁 检查 `logs/` 目录下的日志文件
3. 🔍 查看 `TROUBLESHOOTING.md` 详细故障排除指南

---

## 🎉 总结

**现在你有两种运行方式：**

### 🚀 命令行运行（推荐）
- **最简单**：`quick-run.bat` → 访问 `http://localhost:8081/document-management/`
- **无需Tomcat**：使用内嵌服务器
- **开发友好**：实时日志，快速重启

### 🏗️ 传统部署
- **生产环境**：`quick-build.bat` → 部署WAR到Tomcat
- **标准流程**：适合正式部署环境

**推荐新手使用命令行运行方式，简单快捷！** 🎯

---

## 数据库编码修复指南.md

# 🔧 数据库编码修复指南

## 🔍 问题描述

如果你看到：
- ✅ 页面其他中文正常显示
- ❌ 数据库中的中文数据显示为乱码（如：鍟嗗搧绠＄悊）

这是典型的**数据库字符编码问题**。

## 🎯 解决方案

### 方法1：自动修复（推荐）

1. **运行修复脚本**：
   ```bash
   fix-database-charset.bat
   ```

2. **重启服务器**：
   ```bash
   restart-server.bat
   ```

3. **验证修复效果**：
   访问页面查看中文是否正常显示

### 方法2：手动修复

#### 步骤1：连接MySQL数据库
```bash
mysql -u root -p
```

#### 步骤2：执行修复SQL
```sql
-- 使用数据库
USE document_management;

-- 修改数据库字符集
ALTER DATABASE document_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 修改表字符集
ALTER TABLE documents CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE borrowing_records CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 清空并重新插入数据
DELETE FROM borrowing_records;
DELETE FROM documents;

-- 重新插入正确的中文数据
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

-- 更新归还时间
UPDATE borrowing_records SET return_time = '2024-12-22 16:30:00' WHERE document_code = 'DOC002';
UPDATE borrowing_records SET return_time = '2024-12-19 11:20:00' WHERE document_code = 'DOC001';
```

#### 步骤3：验证修复
```sql
-- 查看字符集设置
SHOW VARIABLES LIKE 'character_set%';

-- 查看数据
SELECT * FROM documents;
SELECT * FROM borrowing_records;
```

## 🔧 技术原理

### 问题原因
1. **MySQL默认字符集**：可能使用latin1而不是utf8mb4
2. **连接字符集不匹配**：应用连接时字符集设置不正确
3. **数据插入时编码问题**：插入数据时字符编码转换错误

### 修复措施
1. **数据库层面**：
   - 数据库字符集：`utf8mb4`
   - 表字符集：`utf8mb4_unicode_ci`

2. **应用层面**：
   - JDBC连接参数：`characterEncoding=UTF-8`
   - 连接时设置：`SET NAMES utf8mb4`

3. **Tomcat层面**：
   - URI编码：`UTF-8`
   - 页面编码：`UTF-8`

## 📋 验证清单

修复完成后，检查以下项目：

- [ ] **数据库字符集**：`utf8mb4_unicode_ci`
- [ ] **表字符集**：`utf8mb4_unicode_ci`
- [ ] **JDBC连接**：包含UTF-8参数
- [ ] **页面显示**：中文正常显示
- [ ] **数据内容**：数据库中文正确存储

## 🆘 常见问题

### Q1：修复后仍然乱码？
**A1**：
1. 清除浏览器缓存
2. 重启应用服务器
3. 检查MySQL服务器配置

### Q2：无法连接数据库？
**A2**：
1. 确认MySQL服务已启动
2. 检查用户名密码
3. 确认端口3306可访问

### Q3：权限不足？
**A3**：
1. 使用管理员权限运行MySQL
2. 确保用户有ALTER权限
3. 检查数据库用户权限设置

## 🎯 预防措施

为避免将来出现编码问题：

1. **MySQL配置**：在my.cnf中设置默认字符集
   ```ini
   [mysql]
   default-character-set=utf8mb4
   
   [mysqld]
   character-set-server=utf8mb4
   collation-server=utf8mb4_unicode_ci
   ```

2. **应用配置**：确保所有配置文件使用UTF-8编码

3. **开发规范**：统一使用UTF-8编码进行开发

---

**💡 提示：大多数情况下，运行 `fix-database-charset.bat` 就能解决问题！**

---

## 前端样式修复说明.md

# 🎨 前端样式修复说明

## 🔍 问题描述

如果你看到页面内容正常显示，但是**缺少样式**（没有按钮效果、卡片样式等），这是因为：

1. **网络问题**：无法访问Bootstrap CDN
2. **样式文件缺失**：Bootstrap CSS没有加载

## ✅ 解决方案

### 方法1：重启服务器（推荐）
```bash
# 停止当前服务器（Ctrl+C）
# 然后运行重启脚本
restart-server.bat

# 或者使用快速启动
quick-run.bat
```

### 方法2：强制刷新页面
```bash
# 在浏览器中按 Ctrl+F5 强制刷新
# 或者清除浏览器缓存后刷新
```

### 方法3：检查本地CSS文件
确认以下文件存在：
- `src/main/webapp/css/bootstrap-local.css`

## 🎯 修复效果对比

### 修复前（无样式）
- 页面内容正常显示
- 没有按钮样式
- 没有卡片效果
- 链接是普通蓝色文字

### 修复后（有样式）
- 🎨 漂亮的卡片布局
- 🔘 彩色按钮效果
- 📱 响应式设计
- 🎭 完整的视觉效果

## 🔧 技术说明

### 原始问题
```html
<!-- 无法访问的CDN链接 -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
```

### 修复方案
```html
<!-- 本地CSS文件 -->
<link href="${pageContext.request.contextPath}/css/bootstrap-local.css" rel="stylesheet">
```

### 本地CSS特性
- ✅ 包含Bootstrap核心样式
- ✅ 响应式网格系统
- ✅ 按钮和卡片组件
- ✅ 图标字符替代
- ✅ 完全离线可用

## 🚀 快速验证

访问 `http://localhost:8081/document-management/home` 应该看到：

1. **顶部蓝色导航栏**
2. **四个功能卡片**（查看文档、文档管理、借阅文档、借阅记录）
3. **彩色按钮**（蓝色、绿色、青色、黄色）
4. **系统功能说明卡片**
5. **底部版权信息**

如果看到以上效果，说明样式修复成功！

## 🆘 仍有问题？

如果样式仍然没有加载：

1. **检查控制台**：按F12查看是否有CSS加载错误
2. **清除缓存**：完全清除浏览器缓存
3. **重新编译**：运行 `mvn clean compile`
4. **检查文件**：确认CSS文件存在且内容正确

---

**💡 提示：大多数情况下，重启服务器就能解决样式问题！**

---

## TROUBLESHOOTING.md

# 🔧 故障排除指南

## 📋 `mvn clean package` 命令失败的常见问题及解决方案

### 🚨 问题1：编译错误

#### 症状
```
[ERROR] COMPILATION ERROR :
[ERROR] /path/to/file.java:[行号,列号] error: 找不到符号
```

#### 解决方案
1. **检查Java版本**：
   ```bash
   java -version
   javac -version
   ```
   确保使用Java 8+

2. **清理并重新编译**：
   ```bash
   mvn clean compile
   ```

3. **检查依赖**：
   ```bash
   mvn dependency:tree
   ```

## 🚨 问题8：批处理文件闪退

#### 症状
```
双击.bat文件后窗口一闪就消失了
```

#### 解决方案（按推荐顺序）

1. **使用最稳定的脚本**：
   ```bash
   # 最推荐：简单稳定
   quick-run.bat
   
   # 次推荐：稳定版本
   run-server.bat
   
   # 备选：详细检查
   FINAL-BUILD.bat
   ```

2. **使用命令行运行**：
   ```bash
   # 在项目目录下打开命令行
   cd /d D:\your\project\path
   quick-run.bat
   ```

3. **手动运行Maven命令**：
   ```bash
   mvn tomcat7:run
   ```

4. **检查脚本权限**：
   - 右键点击.bat文件
   - 选择"以管理员身份运行"

#### 📊 批处理文件状态

| 文件名 | 状态 | 说明 |
|--------|------|------|
| `quick-run.bat` | ✅ 稳定 | 最简单，最可靠，已修复中文编码 |
| `run-server.bat` | ✅ 稳定 | 完整环境检查，已修复中文编码 |
| `FINAL-BUILD.bat` | ✅ 稳定 | 详细环境检查，已修复中文编码 |
| `quick-build.bat` | ✅ 稳定 | 快速构建，已修复中文编码 |

## 🚨 问题9：中文字符显示乱码

#### 症状
```
页面或控制台显示繁体字、乱码或问号
```

#### 解决方案
1. **批处理文件已自动修复**：
   - 所有.bat文件已添加 `chcp 65001` 命令
   - 自动设置UTF-8编码

2. **手动设置编码**（如果仍有问题）：
   ```bash
   # 在命令行中执行
   chcp 65001
   ```

3. **浏览器设置**：
   - 确保浏览器编码设置为UTF-8
   - Chrome: 设置 → 高级 → 语言 → 编码

4. **IDE设置**：
   - 确保IDE文件编码设置为UTF-8

### 🚨 问题2：测试失败

#### 症状
```
[ERROR] Tests run: X, Failures: Y, Errors: Z, Skipped: 0
```

#### 解决方案
1. **跳过测试进行打包**：
   ```bash
   mvn clean package -DskipTests
   ```

2. **只运行特定测试**：
   ```bash
   mvn test -Dtest=DocumentServiceTest
   ```

3. **查看详细测试报告**：
   检查 `target/surefire-reports/` 目录下的测试报告

### 🚨 问题3：内存不足

#### 症状
```
[ERROR] Java heap space
[ERROR] OutOfMemoryError
```

#### 解决方案
1. **增加Maven内存**：
   ```bash
   set MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=256m
   mvn clean package
   ```

2. **Windows PowerShell**：
   ```powershell
   $env:MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m"
   mvn clean package
   ```

### 🚨 问题4：网络连接问题

#### 症状
```
[ERROR] Could not transfer artifact
[ERROR] Connection timed out
```

#### 解决方案
1. **使用国内镜像**：
   编辑 `~/.m2/settings.xml`：
   ```xml
   <mirrors>
     <mirror>
       <id>alimaven</id>
       <name>aliyun maven</name>
       <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
       <mirrorOf>central</mirrorOf>
     </mirror>
   </mirrors>
   ```

2. **离线模式**：
   ```bash
   mvn clean package -o
   ```

### 🚨 问题5：权限问题

#### 症状
```
[ERROR] Failed to delete
[ERROR] Access is denied
```

#### 解决方案
1. **以管理员身份运行**
2. **关闭占用文件的程序**（如IDE、Tomcat）
3. **检查文件权限**

### 🚨 问题6：配置文件错误

#### 症状
```
[ERROR] Error creating bean
[ERROR] Could not resolve placeholder
```

#### 解决方案
1. **检查配置文件路径**：
   - `src/main/resources/database.properties`
   - `src/main/resources/database-init.properties`

2. **验证配置文件内容**：
   ```properties
   jdbc.username=你的用户名
   jdbc.password=你的密码
   ```

### 🚨 问题7：数据库连接问题

#### 症状
```
[ERROR] Communications link failure
[ERROR] Access denied for user
```

#### 解决方案
1. **检查MySQL服务**：
   ```bash
   # Windows
   net start mysql
   
   # 或检查服务状态
   sc query mysql
   ```

2. **测试数据库连接**：
   ```bash
   mysql -u root -p -h localhost -P 3306
   ```

3. **检查防火墙设置**

## 🛠️ 快速诊断命令

### 1. 环境检查
```bash
# 检查Java环境
java -version
javac -version
echo %JAVA_HOME%

# 检查Maven环境
mvn -version

# 检查MySQL连接
mysql -u root -p -e "SELECT VERSION();"
```

### 2. 项目诊断
```bash
# 清理项目
mvn clean

# 验证项目结构
mvn validate

# 编译测试
mvn compile

# 依赖分析
mvn dependency:analyze
```

### 3. 详细日志
```bash
# 详细输出
mvn clean package -X

# 调试模式
mvn clean package -e -X
```

## 🔍 分步骤排查

### 步骤1：基础环境
1. ✅ Java 8+ 已安装
2. ✅ Maven 3.6+ 已安装
3. ✅ MySQL 服务已启动
4. ✅ 网络连接正常

### 步骤2：项目配置
1. ✅ `pom.xml` 文件完整
2. ✅ 数据库配置正确
3. ✅ 源代码无语法错误
4. ✅ 资源文件路径正确

### 步骤3：逐步构建
```bash
# 1. 清理
mvn clean

# 2. 验证
mvn validate

# 3. 编译
mvn compile

# 4. 测试编译
mvn test-compile

# 5. 跳过测试打包
mvn package -DskipTests

# 6. 完整打包
mvn package
```

## 📞 获取帮助

### 查看错误详情
1. **完整错误日志**：使用 `-X` 参数
2. **测试报告**：查看 `target/surefire-reports/`
3. **编译输出**：查看控制台完整输出

### 常用调试命令
```bash
# 显示有效POM
mvn help:effective-pom

# 显示依赖树
mvn dependency:tree

# 分析依赖
mvn dependency:analyze

# 显示项目信息
mvn help:describe -Dplugin=compiler
```

## 🎯 成功标志

当看到以下输出时，说明打包成功：
```
[INFO] Building war: D:\debug\Javaee_ssm\target\document-management-1.0.0.war
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

生成的文件位置：`target/document-management-1.0.0.war`

---

**💡 提示**：如果问题仍然存在，请提供完整的错误日志，这样可以更准确地诊断问题！

---

## README.md

# 🏢 企业文档管理系统

一个基于Spring MVC + MyBatis的企业级文档管理系统，支持文档借阅、归还和记录查询功能。

## 🚀 快速开始

### ⚡ 一键启动（推荐）
```bash
# Windows用户 - 双击运行
quick-run.bat

# 然后访问
http://localhost:8081/document-management/
```

### 📋 详细说明
查看完整的运行指南：[运行指南.md](运行指南.md)

## 🎯 主要功能

- 📄 **文档管理**：添加、查看、编辑文档信息
- 📚 **借阅系统**：文档借阅和归还管理
- 📊 **记录查询**：借阅历史记录查看
- 🔄 **自动初始化**：数据库表和示例数据自动创建

## 🛠️ 技术栈

- **后端**：Spring MVC 5.3.21 + MyBatis 3.5.10
- **数据库**：MySQL 5.7+ / 8.0+
- **连接池**：HikariCP 4.0.3
- **构建工具**：Maven 3.6+
- **运行环境**：Java 8+

## 📁 可用脚本

| 文件 | 功能 | 推荐度 |
|------|------|--------|
| `quick-run.bat` | 直接启动服务器 | 🥇 最推荐 |
| `run-server.bat` | 环境检查+启动 | 🥈 推荐 |
| `quick-build.bat` | 构建WAR文件 | 🥉 传统部署 |
| `FINAL-BUILD.bat` | 详细构建过程 | ⚠️ 调试用 |

## 🔧 配置要求

1. **安装Java 8+和Maven 3.6+**
2. **安装并启动MySQL服务**
3. **修改数据库配置**：编辑 `src/main/resources/database.properties`
   ```properties
   jdbc.username=你的MySQL用户名
   jdbc.password=你的MySQL密码
   ```

## 📊 系统特性

- ✅ **零配置启动**：内嵌Tomcat服务器，无需外部部署
- ✅ **自动数据库初始化**：首次运行自动创建表结构和示例数据
- ✅ **智能错误处理**：完善的异常处理和用户友好的错误提示
- ✅ **实时日志显示**：控制台直接显示运行状态和调试信息
- ✅ **开发友好**：支持热重启，修改代码后快速测试

## 🆘 遇到问题？

1. 📖 查看详细的 [运行指南.md](运行指南.md)
2. 🔧 查看 [TROUBLESHOOTING.md](TROUBLESHOOTING.md) 故障排除指南
3. 📋 检查控制台日志输出

---

**🎉 现在就开始使用吧！双击 `quick-run.bat` 即可启动系统！**

---

## README-DATABASE-SETUP.md

# 数据库自动初始化配置说明

## 概述

本项目已配置数据库自动初始化功能，**无需手动创建数据库表结构**。系统会在启动时自动完成以下操作：

1. 自动创建数据库表结构
2. 自动插入示例数据
3. 自动创建索引和外键约束

## 快速启动步骤

### 1. 安装MySQL
确保已安装MySQL 5.7+或8.0+，并启动MySQL服务。

### 2. 创建数据库（可选）
如果你的MySQL用户有创建数据库权限，系统会自动创建数据库。否则请手动创建：

```sql
CREATE DATABASE document_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置数据库连接
编辑 `src/main/resources/database.properties` 文件：

```properties
# 修改为你的MySQL连接信息
jdbc.username=你的用户名
jdbc.password=你的密码
```

### 4. 启动项目
```bash
mvn clean package
# 部署到Tomcat或使用其他方式启动
```

## 配置选项

### 数据库初始化配置
编辑 `src/main/resources/database-init.properties` 文件可以控制初始化行为：

```properties
# 是否启用自动初始化（默认：true）
database.auto.init.enabled=true

# 是否在每次启动时重新创建表（开发环境使用，默认：false）
database.auto.init.recreate=false

# 是否插入示例数据（默认：true）
database.auto.init.sample.data=true
```

### 示例数据说明
系统会自动插入以下示例数据：
- 8个示例文档（包含不同类型和状态）
- 3条借阅记录（包含已归还和借阅中的记录）

## 生产环境配置

### 禁用示例数据
生产环境建议禁用示例数据插入：

```properties
database.auto.init.sample.data=false
```

### 安全配置
1. 修改数据库用户名和密码
2. 确保数据库用户只有必要的权限
3. 启用SSL连接（修改jdbc.url中的useSSL=true）

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证用户名和密码是否正确
   - 确认数据库是否存在

2. **权限不足**
   - 确保数据库用户有CREATE、DROP、INSERT、UPDATE、DELETE权限
   - 如果需要自动创建数据库，用户需要有CREATE DATABASE权限

3. **字符编码问题**
   - 确保数据库和表使用utf8mb4字符集
   - 检查连接URL中的字符编码设置

### 日志查看
系统启动时会输出详细的初始化日志：

```
INFO  c.e.d.c.DatabaseAutoInitializer - 开始数据库自动初始化...
INFO  c.e.d.c.DatabaseAutoInitializer - 执行数据库表结构创建脚本
INFO  c.e.d.c.DatabaseAutoInitializer - 数据库表结构创建成功
INFO  c.e.d.c.DatabaseAutoInitializer - 示例数据插入成功
INFO  c.e.d.c.DatabaseAutoInitializer - 数据库自动初始化完成
```

## 手动控制

如果需要手动控制数据库初始化，可以：

1. **禁用自动初始化**：
   ```properties
   database.auto.init.enabled=false
   ```

2. **重新初始化数据库**（开发环境）：
   ```properties
   database.auto.init.recreate=true
   ```

3. **只创建表结构，不插入数据**：
   ```properties
   database.auto.init.sample.data=false
   ```

## 数据库表结构

系统会自动创建以下表：

### documents（文档表）
- id: 主键
- document_code: 文档编号（唯一）
- document_name: 文档名称
- document_type: 文档类型
- status: 状态（可下载/已借出/归档）
- created_time: 创建时间
- updated_time: 更新时间

### borrowing_records（借阅记录表）
- id: 主键
- document_code: 文档编号（外键）
- user_id: 工号/学号
- user_name: 用户姓名
- borrow_time: 借阅时间
- return_time: 归还时间
- status: 借阅状态

## 总结

通过以上配置，你只需要：
1. 安装并启动MySQL
2. 修改数据库连接配置
3. 启动项目

系统会自动完成所有数据库初始化工作，无需手动执行任何SQL脚本！

---

## .kiro/specs/enterprise-document-management/requirements.md

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

---

## .kiro/specs/enterprise-document-management/design.md

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

---

## .kiro/specs/enterprise-document-management/tasks.md

# 实施计划

- [x] 1. 项目结构和核心配置设置





  - 创建Maven项目结构，包含controller、service、mapper、model包
  - 配置Spring、SpringMVC、MyBatis的核心配置文件
  - 设置数据库连接池（HikariCP）和事务管理配置
  - 配置Web.xml和Spring配置文件
  - 启用Spring缓存支持和性能监控
  - _需求: 8.1, 8.3_

- [-] 2. 数据库设计和初始化


- [x] 2.1 创建数据库表结构和初始化脚本



  - 编写documents表和borrowing_records表的DDL脚本
  - 创建schema.sql用于自动建表
  - 实现数据库初始化检测逻辑
  - _需求: 6.2, 6.3_

- [x] 2.2 编写属性测试：数据加载一致性





  - **属性 9: 数据加载一致性**
  - **验证: 需求 6.2**

- [-] 3. 核心数据模型实现


- [x] 3.1 实现Document和BorrowingRecord实体类


  - 创建Document实体类，包含编号、名称、类型、状态字段
  - 创建BorrowingRecord实体类，包含借阅信息字段
  - 实现实体类的构造方法、getter/setter和toString方法
  - _需求: 1.1, 5.1_

- [x] 3.2 编写属性测试：文档创建一致性






  - **属性 2: 文档创建一致性**
  - **验证: 需求 2.1**

- [x] 4. 数据访问层实现





- [x] 4.1 实现DocumentMapper接口和XML映射


  - 创建DocumentMapper接口，定义CRUD操作方法
  - 编写DocumentMapper.xml，实现SQL映射
  - 包含查询所有文档、按编号查询、插入、更新、删除方法
  - 添加分页查询支持和索引优化建议
  - _需求: 1.1, 2.1, 2.2, 2.3, 8.3_

- [x] 4.2 实现BorrowingRecordMapper接口和XML映射


  - 创建BorrowingRecordMapper接口，定义借阅记录操作方法
  - 编写BorrowingRecordMapper.xml，实现SQL映射
  - 包含查询所有记录、按条件查询、插入、更新方法
  - 优化查询语句，添加必要的索引建议
  - _需求: 3.5, 4.4, 5.1, 8.3_

- [x] 4.3 编写单元测试：数据访问层测试






  - 测试DocumentMapper的所有CRUD操作
  - 测试BorrowingRecordMapper的所有操作
  - 使用H2内存数据库进行测试
  - _需求: 1.1, 2.1, 3.5, 5.1_


- [x] 5. 输入验证服务实现






- [x] 5.1 实现ValidationService输入验证逻辑



  - 实现文档编号验证（非空，长度≤20）
  - 实现工号/学号验证（非空，长度≤20）
  - 实现姓名验证（中英文组合，长度≤10）
  - 创建自定义验证异常类
  - _需求: 7.1, 7.2, 7.3, 7.4_

- [x] 5.2 编写属性测试：输入验证一致性






  - **属性 7: 输入验证一致性**
  - **验证: 需求 7.1, 7.2, 7.3**

- [x] 6. 业务服务层实现






- [x] 6.1 实现DocumentService业务逻辑


  - 实现查看文档列表功能（按编号排序）
  - 实现添加文档功能（默认状态为"可下载"）
  - 实现修改文档信息功能
  - 实现删除归档文档功能（检查状态和借阅记录）
  - _需求: 1.1, 1.3, 2.1, 2.2, 2.3, 2.4, 2.5_

- [x] 6.2 编写属性测试：文档列表完整性







  - **属性 1: 文档列表完整性**
  - **验证: 需求 1.1, 1.3**

- [x] 6.3 编写属性测试：文档删除规则






  - **属性 3: 文档删除规则**
  - **验证: 需求 2.3, 2.4, 2.5**

- [x] 6.4 实现BorrowingService业务逻辑


  - 实现文档借阅功能（状态检查、状态更新、记录创建）
  - 实现文档归还功能（记录验证、状态更新、时间记录）
  - 实现查看借阅记录功能
  - _需求: 3.1, 3.2, 3.3, 3.4, 3.5, 4.1, 4.2, 4.3, 4.4, 5.1, 5.2_

- [x] 6.5 编写属性测试：借阅往返一致性











  - **属性 4: 借阅往返一致性**
  - **验证: 需求 3.2, 4.2**

- [x] 6.6 编写属性测试：借阅记录完整性






  - **属性 5: 借阅记录完整性**
  - **验证: 需求 3.5**

- [x] 6.7 编写属性测试：借阅记录显示一致性






  - **属性 6: 借阅记录显示一致性**
  - **验证: 需求 5.1, 5.2**

- [x] 7. 检查点 - 确保所有测试通过





  - 确保所有测试通过，如有问题请询问用户

- [x] 8. Web控制器层实现







- [x] 8.1 实现HomeController主页控制器

  - 创建主页控制器，显示功能模块导航
  - 实现主页面路由和视图渲染
  - _需求: 8.1_

- [x] 8.2 实现DocumentController文档管理控制器


  - 实现查看文档列表的GET请求处理
  - 实现添加文档的POST请求处理
  - 实现修改文档的PUT请求处理
  - 实现删除文档的DELETE请求处理
  - 添加请求参数验证和异常处理
  - _需求: 1.1, 1.2, 2.1, 2.2, 2.3, 8.4_

- [x] 8.3 实现BorrowingController借阅管理控制器


  - 实现文档借阅的POST请求处理
  - 实现文档归还的PUT请求处理
  - 实现查看借阅记录的GET请求处理
  - 添加请求参数验证和异常处理
  - _需求: 3.1, 3.2, 4.1, 4.2, 5.1, 8.4_

- [x] 8.4 编写属性测试：操作结果反馈






  - **属性 10: 操作结果反馈**
  - **验证: 需求 8.4**

- [x] 9. 全局异常处理实现








- [x] 9.1 实现GlobalExceptionHandler异常处理器


  - 创建全局异常处理器，处理验证异常
  - 处理业务逻辑异常和数据库异常
  - 实现友好错误页面显示
  - _需求: 7.4, 7.5, 6.3, 6.4_


- [x] 9.2 编写属性测试：异常处理安全性






  - **属性 8: 异常处理安全性**
  - **验证: 需求 7.5**











- [-] 10. Web视图层实现


- [x] 10.1 创建JSP页面和静态资源





  - 创建主页面（index.jsp）显示功能模块
  - 创建文档管理页面（documents.jsp）
  - 创建借阅管理页面（borrowing.jsp）
  - 创建借阅记录页面（records.jsp）
  - 创建错误页面（error.jsp）
  - 引入Bootstrap CSS框架美化界面
  - _需求: 8.1, 8.2_


- [x] 10.2 编写单元测试：Web层集成测试








  - 使用MockMvc测试所有控制器方法
  - 测试页面渲染和数据传递
  - 测试表单提交和参数绑定
  - _需求: 8.1, 8.2, 8.4_


- [x] 11. 最终检查点 - 确保所有测试通过





  - 确保所有测试通过，如有问题请询问用户

