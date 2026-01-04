<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>文档管理 - 企业文档管理系统</title>
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
                <a class="nav-link active" href="${pageContext.request.contextPath}/documents">文档管理</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/borrowing">借阅管理</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2><i class="bi bi-folder"></i> 文档管理</h2>
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary">
                        <i class="bi bi-house"></i> 返回首页
                    </a>
                </div>

                <!-- 成功信息显示 -->
                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle"></i> ${successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- 错误信息显示 -->
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- 文档管理功能模块 -->
                <div class="row">
                    <div class="col-md-6 col-lg-4 mb-4">
                        <div class="card h-100 shadow-sm">
                            <div class="card-body text-center">
                                <i class="bi bi-list-ul display-4 text-primary mb-3"></i>
                                <h5 class="card-title">查看文档列表</h5>
                                <p class="card-text">查看所有文档的基本信息，包括编号、名称、类型和状态，按编号排序显示</p>
                                <a href="${pageContext.request.contextPath}/documents/list" class="btn btn-primary">
                                    <i class="bi bi-eye"></i> 查看列表
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-4 mb-4">
                        <div class="card h-100 shadow-sm">
                            <div class="card-body text-center">
                                <i class="bi bi-plus-circle display-4 text-success mb-3"></i>
                                <h5 class="card-title">添加新文档</h5>
                                <p class="card-text">添加新的文档信息，包括编号、名称和类型，默认状态为"可下载"</p>
                                <a href="${pageContext.request.contextPath}/documents/add" class="btn btn-success">
                                    <i class="bi bi-plus-circle"></i> 添加文档
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 col-lg-4 mb-4">
                        <div class="card h-100 shadow-sm">
                            <div class="card-body text-center">
                                <i class="bi bi-gear display-4 text-warning mb-3"></i>
                                <h5 class="card-title">管理文档</h5>
                                <p class="card-text">修改文档信息、更新文档状态、删除归档文档等管理操作</p>
                                <a href="${pageContext.request.contextPath}/documents/manage" class="btn btn-warning">
                                    <i class="bi bi-gear"></i> 管理文档
                                </a>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 文档状态统计 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="bi bi-bar-chart"></i> 文档状态统计</h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${not empty documents}">
                            <div class="row text-center">
                                <div class="col-md-4">
                                    <div class="card bg-success text-white">
                                        <div class="card-body">
                                            <h3>
                                                <c:set var="availableCount" value="0"/>
                                                <c:forEach var="doc" items="${documents}">
                                                    <c:if test="${doc.status == '可下载'}">
                                                        <c:set var="availableCount" value="${availableCount + 1}"/>
                                                    </c:if>
                                                </c:forEach>
                                                ${availableCount}
                                            </h3>
                                            <p class="mb-0">
                                                <i class="bi bi-check-circle"></i> 可下载
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="card bg-warning text-white">
                                        <div class="card-body">
                                            <h3>
                                                <c:set var="borrowedCount" value="0"/>
                                                <c:forEach var="doc" items="${documents}">
                                                    <c:if test="${doc.status == '已借出'}">
                                                        <c:set var="borrowedCount" value="${borrowedCount + 1}"/>
                                                    </c:if>
                                                </c:forEach>
                                                ${borrowedCount}
                                            </h3>
                                            <p class="mb-0">
                                                <i class="bi bi-clock"></i> 已借出
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="card bg-dark text-white">
                                        <div class="card-body">
                                            <h3>
                                                <c:set var="archivedCount" value="0"/>
                                                <c:forEach var="doc" items="${documents}">
                                                    <c:if test="${doc.status == '归档'}">
                                                        <c:set var="archivedCount" value="${archivedCount + 1}"/>
                                                    </c:if>
                                                </c:forEach>
                                                ${archivedCount}
                                            </h3>
                                            <p class="mb-0">
                                                <i class="bi bi-archive"></i> 归档
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${empty documents}">
                            <div class="text-center text-muted">
                                <i class="bi bi-inbox display-4"></i>
                                <p class="mt-3">暂无文档数据</p>
                                <a href="${pageContext.request.contextPath}/documents/add" class="btn btn-primary">
                                    <i class="bi bi-plus-circle"></i> 添加第一个文档
                                </a>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- 功能说明 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-question-circle"></i> 功能说明</h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>文档管理功能</h6>
                                <ul class="list-unstyled">
                                    <li><i class="bi bi-check-circle text-success"></i> 查看文档列表（按编号排序）</li>
                                    <li><i class="bi bi-check-circle text-success"></i> 添加新文档（默认状态：可下载）</li>
                                    <li><i class="bi bi-check-circle text-success"></i> 修改文档信息（名称、类型、状态）</li>
                                    <li><i class="bi bi-check-circle text-success"></i> 删除归档文档（需满足删除条件）</li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6>文档状态说明</h6>
                                <ul class="list-unstyled">
                                    <li>
                                        <span class="badge bg-success me-2">
                                            <i class="bi bi-check-circle"></i> 可下载
                                        </span>
                                        文档可以被借阅
                                    </li>
                                    <li>
                                        <span class="badge bg-warning me-2">
                                            <i class="bi bi-clock"></i> 已借出
                                        </span>
                                        文档已被借阅，暂不可用
                                    </li>
                                    <li>
                                        <span class="badge bg-dark me-2">
                                            <i class="bi bi-archive"></i> 归档
                                        </span>
                                        文档已归档，不可借阅
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 快捷操作 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-lightning"></i> 快捷操作</h6>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                            <a href="${pageContext.request.contextPath}/documents/list" class="btn btn-primary me-md-2">
                                <i class="bi bi-list-ul"></i> 查看列表
                            </a>
                            <a href="${pageContext.request.contextPath}/documents/add" class="btn btn-success me-md-2">
                                <i class="bi bi-plus-circle"></i> 添加文档
                            </a>
                            <a href="${pageContext.request.contextPath}/documents/manage" class="btn btn-warning me-md-2">
                                <i class="bi bi-gear"></i> 管理文档
                            </a>
                            <a href="${pageContext.request.contextPath}/borrowing" class="btn btn-outline-info">
                                <i class="bi bi-arrow-left-right"></i> 借阅管理
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
            </p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
