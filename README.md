# 🏢 企业文档管理系统

一个基于Spring MVC + MyBatis的企业级文档管理系统，支持文档借阅、归还和记录查询功能。

## 🚀 快速开始

### ⚡ 一键启动（推荐）
```bash
# Windows用户 - 双击运行
merged-startup.bat

# 然后访问
http://localhost:8081/document-management/
```

### 📋 详细说明
查看整合说明文档：[MERGED-DOCUMENTATION.md](MERGED-DOCUMENTATION.md)

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

## 📁 启动脚本

| 文件 | 功能 |
|------|------|
| `merged-startup.bat` | 统一菜单：启动、重启、构建、修复工具 |

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

1. 📖 查看整合的 [MERGED-DOCUMENTATION.md](MERGED-DOCUMENTATION.md)
2. 🔧 故障排除请参考合并文档中的排障章节
3. 📋 检查控制台日志输出

---

**🎉 现在就开始使用吧！双击 `merged-startup.bat` 即可启动系统！**
