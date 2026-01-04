<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap-local.css" rel="stylesheet">
    <style>
        /* 额外的本地样式 */
        .navbar-nav.ms-auto {
            margin-left: auto;
        }
        
        .d-flex {
            display: flex;
        }
        
        .justify-content-between {
            justify-content: space-between;
        }
        
        .align-items-center {
            align-items: center;
        }
        
        .mx-2 {
            margin-left: 0.5rem;
            margin-right: 0.5rem;
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
                <i class="bi bi-file-earmark-text"></i> 企业文档管理系统
            </a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/about">关于</a>
            </div>
        </div>
    </nav>

    <div class="container mt-5">
        <div class="row">
            <div class="col-12">
                <div class="jumbotron bg-light p-5 rounded">
                    <h1 class="display-4">${welcomeMessage}</h1>
                    <p class="lead">管理企业内部重要电子文档，实现文档信息化管理和借阅记录跟踪</p>
                </div>
            </div>
        </div>

        <div class="row mt-4">
            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100 shadow-sm">
                    <div class="card-body text-center">
                        <i class="bi bi-list-ul display-4 text-primary mb-3"></i>
                        <h5 class="card-title">查看文档</h5>
                        <p class="card-text">查看所有文档的基本信息，包括编号、名称、类型和状态</p>
                        <a href="${pageContext.request.contextPath}/documents" class="btn btn-primary">
                            <i class="bi bi-eye"></i> 查看文档列表
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100 shadow-sm">
                    <div class="card-body text-center">
                        <i class="bi bi-plus-circle display-4 text-success mb-3"></i>
                        <h5 class="card-title">文档管理</h5>
                        <p class="card-text">添加、修改和删除文档信息，维护文档库内容</p>
                        <a href="${pageContext.request.contextPath}/documents/manage" class="btn btn-success">
                            <i class="bi bi-gear"></i> 管理文档
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100 shadow-sm">
                    <div class="card-body text-center">
                        <i class="bi bi-download display-4 text-info mb-3"></i>
                        <h5 class="card-title">借阅文档</h5>
                        <p class="card-text">借阅和归还文档，系统自动记录借阅状态</p>
                        <a href="${pageContext.request.contextPath}/borrowing" class="btn btn-info">
                            <i class="bi bi-arrow-down-circle"></i> 借阅管理
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100 shadow-sm">
                    <div class="card-body text-center">
                        <i class="bi bi-journal-text display-4 text-warning mb-3"></i>
                        <h5 class="card-title">借阅记录</h5>
                        <p class="card-text">查看所有借阅记录，监控文档使用情况</p>
                        <a href="${pageContext.request.contextPath}/borrowing/records" class="btn btn-warning">
                            <i class="bi bi-list-check"></i> 查看记录
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <div class="row mt-5">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="bi bi-info-circle"></i> 系统功能说明</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>文档管理功能</h6>
                                <ul class="list-unstyled">
                                    <li><i class="bi bi-check-circle text-success"></i> 查看文档列表（按编号排序）</li>
                                    <li><i class="bi bi-check-circle text-success"></i> 添加新文档（默认状态：可下载）</li>
                                    <li><i class="bi bi-check-circle text-success"></i> 修改文档信息</li>
                                    <li><i class="bi bi-check-circle text-success"></i> 删除归档文档</li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6>借阅管理功能</h6>
                                <ul class="list-unstyled">
                                    <li><i class="bi bi-check-circle text-success"></i> 文档借阅（状态检查）</li>
                                    <li><i class="bi bi-check-circle text-success"></i> 文档归还（记录更新）</li>
                                    <li><i class="bi bi-check-circle text-success"></i> 借阅记录查询</li>
                                    <li><i class="bi bi-check-circle text-success"></i> 输入验证和异常处理</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <footer class="bg-light mt-5 py-4">
        <div class="container text-center">
            <p class="text-muted mb-0">
                <i class="bi bi-building"></i> 企业文档管理系统 &copy; 2024 
                <span class="mx-2">|</span>
                <i class="bi bi-shield-check"></i> 安全可靠的文档管理解决方案
            </p>
        </div>
    </footer>

    <!-- 本地JavaScript替代 -->
    <script>
        // 简单的Bootstrap功能替代
        document.addEventListener('DOMContentLoaded', function() {
            console.log('企业文档管理系统已加载');
        });
    </script>
</body>
</html>