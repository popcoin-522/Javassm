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
                <a class="nav-link active" href="${pageContext.request.contextPath}/borrowing">借阅管理</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/borrowing/records">借阅记录</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2><i class="bi bi-download"></i> ${title}</h2>
                    <div>
                        <a href="${pageContext.request.contextPath}/borrowing/records" class="btn btn-info">
                            <i class="bi bi-list-check"></i> 查看记录
                        </a>
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary">
                            <i class="bi bi-house"></i> 返回首页
                        </a>
                    </div>
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

                <!-- 功能模块卡片 -->
                <div class="row">
                    <div class="col-md-6 mb-4">
                        <div class="card h-100 shadow-sm">
                            <div class="card-body text-center">
                                <i class="bi bi-download display-1 text-success mb-3"></i>
                                <h4 class="card-title">借阅文档</h4>
                                <p class="card-text">选择可用文档进行借阅，系统将自动更新文档状态并记录借阅信息</p>
                                <a href="${pageContext.request.contextPath}/borrowing/borrow" class="btn btn-success btn-lg">
                                    <i class="bi bi-arrow-down-circle"></i> 开始借阅
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 mb-4">
                        <div class="card h-100 shadow-sm">
                            <div class="card-body text-center">
                                <i class="bi bi-upload display-1 text-warning mb-3"></i>
                                <h4 class="card-title">归还文档</h4>
                                <p class="card-text">归还已借阅的文档，系统将自动恢复文档状态并记录归还时间</p>
                                <a href="${pageContext.request.contextPath}/borrowing/return" class="btn btn-warning btn-lg">
                                    <i class="bi bi-arrow-up-circle"></i> 开始归还
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
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- 操作说明 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-question-circle"></i> 操作说明</h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>借阅流程</h6>
                                <ol class="list-group list-group-numbered">
                                    <li class="list-group-item">选择状态为"可下载"的文档</li>
                                    <li class="list-group-item">填写工号/学号和姓名信息</li>
                                    <li class="list-group-item">提交借阅申请</li>
                                    <li class="list-group-item">系统自动更新文档状态为"已借出"</li>
                                </ol>
                            </div>
                            <div class="col-md-6">
                                <h6>归还流程</h6>
                                <ol class="list-group list-group-numbered">
                                    <li class="list-group-item">选择已借阅的文档</li>
                                    <li class="list-group-item">确认借阅人信息</li>
                                    <li class="list-group-item">提交归还申请</li>
                                    <li class="list-group-item">系统自动恢复文档状态为"可下载"</li>
                                </ol>
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
                            <a href="${pageContext.request.contextPath}/borrowing/borrow" class="btn btn-success me-md-2">
                                <i class="bi bi-download"></i> 借阅文档
                            </a>
                            <a href="${pageContext.request.contextPath}/borrowing/return" class="btn btn-warning me-md-2">
                                <i class="bi bi-upload"></i> 归还文档
                            </a>
                            <a href="${pageContext.request.contextPath}/borrowing/records" class="btn btn-info me-md-2">
                                <i class="bi bi-list-check"></i> 查看记录
                            </a>
                            <a href="${pageContext.request.contextPath}/documents" class="btn btn-outline-primary">
                                <i class="bi bi-list-ul"></i> 文档列表
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
