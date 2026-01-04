<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link href="${pageContext.request.contextPath}/css/bootstrap-local.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="${pageContext.request.contextPath}/home">
                <i class="bi bi-file-earmark-text"></i> 企业文档管理系统
            </a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="${pageContext.request.contextPath}/home">首页</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/about">关于</a>
            </div>
        </div>
    </nav>

    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header text-center">
                        <h2><i class="bi bi-info-circle"></i> ${title}</h2>
                    </div>
                    <div class="card-body">
                        <div class="text-center mb-4">
                            <h4>${systemInfo}</h4>
                            <p class="lead">${description}</p>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <h5><i class="bi bi-gear"></i> 技术架构</h5>
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item">Spring Framework</li>
                                    <li class="list-group-item">Spring MVC</li>
                                    <li class="list-group-item">MyBatis</li>
                                    <li class="list-group-item">MySQL Database</li>
                                    <li class="list-group-item">Bootstrap UI</li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h5><i class="bi bi-list-check"></i> 主要功能</h5>
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item">文档信息管理</li>
                                    <li class="list-group-item">借阅记录跟踪</li>
                                    <li class="list-group-item">状态自动更新</li>
                                    <li class="list-group-item">输入验证保护</li>
                                    <li class="list-group-item">数据持久化存储</li>
                                </ul>
                            </div>
                        </div>

                        <div class="mt-4">
                            <h5><i class="bi bi-people"></i> 适用对象</h5>
                            <p>本系统专为中小企业或高校项目组设计，提供轻量级的文档管理解决方案。通过简单直观的Web界面，帮助管理人员高效地管理企业内部重要电子文档，包括合同、方案、报告、技术资料等。</p>
                        </div>

                        <div class="text-center mt-4">
                            <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                                <i class="bi bi-house"></i> 返回首页
                            </a>
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

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
