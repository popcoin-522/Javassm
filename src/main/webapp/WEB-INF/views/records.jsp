<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>借阅记录 - 企业文档管理系统</title>
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
                <a class="nav-link" href="${pageContext.request.contextPath}/documents">文档管理</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/borrowing">借阅管理</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/records">借阅记录</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2><i class="bi bi-journal-text"></i> 借阅记录</h2>
                    <div>
                        <a href="${pageContext.request.contextPath}/borrowing" class="btn btn-info">
                            <i class="bi bi-arrow-left-right"></i> 借阅管理
                        </a>
                        <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary">
                            <i class="bi bi-house"></i> 返回首页
                        </a>
                    </div>
                </div>

                <!-- 错误信息显示 -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-triangle"></i> ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- 提示信息显示 -->
                <c:if test="${not empty message}">
                    <div class="alert alert-info alert-dismissible fade show" role="alert">
                        <i class="bi bi-info-circle"></i> ${message}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- 借阅记录表格 -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="bi bi-table"></i> 借阅记录列表</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty records}">
                                <div class="text-center py-5">
                                    <i class="bi bi-inbox display-1 text-muted"></i>
                                    <h4 class="text-muted mt-3">暂无借阅记录</h4>
                                    <p class="text-muted">系统中还没有任何借阅记录，请先进行文档借阅。</p>
                                    <a href="${pageContext.request.contextPath}/borrowing/borrow" class="btn btn-success">
                                        <i class="bi bi-download"></i> 开始借阅
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover">
                                        <thead class="table-dark">
                                            <tr>
                                                <th scope="col"><i class="bi bi-hash"></i> 文档编号</th>
                                                <th scope="col"><i class="bi bi-person-badge"></i> 工号/学号</th>
                                                <th scope="col"><i class="bi bi-person"></i> 姓名</th>
                                                <th scope="col"><i class="bi bi-calendar"></i> 借阅时间</th>
                                                <th scope="col"><i class="bi bi-calendar-check"></i> 归还时间</th>
                                                <th scope="col"><i class="bi bi-circle-fill"></i> 状态</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="record" items="${records}">
                                                <tr>
                                                    <td>
                                                        <code>${record.documentCode}</code>
                                                    </td>
                                                    <td>
                                                        <strong>${record.userId}</strong>
                                                    </td>
                                                    <td>
                                                        ${record.userName}
                                                    </td>
                                                    <td>
                                                        <c:if test="${not empty record.borrowTime}">
                                                            <fmt:formatDate value="${record.borrowTime}" pattern="yyyy-MM-dd HH:mm"/>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty record.returnTime}">
                                                                <fmt:formatDate value="${record.returnTime}" pattern="yyyy-MM-dd HH:mm"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">-</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${record.status == '借阅中'}">
                                                                <span class="badge bg-warning">
                                                                    <i class="bi bi-clock"></i> ${record.status}
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${record.status == '已归还'}">
                                                                <span class="badge bg-success">
                                                                    <i class="bi bi-check-circle"></i> ${record.status}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-light text-dark">${record.status}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>

                                <div class="mt-3">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <p class="text-muted">
                                                <i class="bi bi-info-circle"></i> 
                                                共找到 <strong>${records.size()}</strong> 条借阅记录
                                            </p>
                                        </div>
                                        <div class="col-md-6 text-end">
                                            <div class="btn-group" role="group">
                                                <a href="${pageContext.request.contextPath}/borrowing/borrow" class="btn btn-success">
                                                    <i class="bi bi-download"></i> 借阅文档
                                                </a>
                                                <a href="${pageContext.request.contextPath}/borrowing/return" class="btn btn-warning">
                                                    <i class="bi bi-upload"></i> 归还文档
                                                </a>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- 记录统计 -->
                <c:if test="${not empty records}">
                    <div class="card mt-4">
                        <div class="card-header">
                            <h6 class="mb-0"><i class="bi bi-bar-chart"></i> 记录统计</h6>
                        </div>
                        <div class="card-body">
                            <div class="row text-center">
                                <div class="col-md-3">
                                    <div class="card bg-info text-white">
                                        <div class="card-body">
                                            <h4>${records.size()}</h4>
                                            <p class="mb-0">
                                                <i class="bi bi-list"></i> 总记录数
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card bg-warning text-white">
                                        <div class="card-body">
                                            <h4>
                                                <c:set var="borrowingCount" value="0"/>
                                                <c:forEach var="record" items="${records}">
                                                    <c:if test="${record.status == '借阅中'}">
                                                        <c:set var="borrowingCount" value="${borrowingCount + 1}"/>
                                                    </c:if>
                                                </c:forEach>
                                                ${borrowingCount}
                                            </h4>
                                            <p class="mb-0">
                                                <i class="bi bi-clock"></i> 借阅中
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card bg-success text-white">
                                        <div class="card-body">
                                            <h4>
                                                <c:set var="returnedCount" value="0"/>
                                                <c:forEach var="record" items="${records}">
                                                    <c:if test="${record.status == '已归还'}">
                                                        <c:set var="returnedCount" value="${returnedCount + 1}"/>
                                                    </c:if>
                                                </c:forEach>
                                                ${returnedCount}
                                            </h4>
                                            <p class="mb-0">
                                                <i class="bi bi-check-circle"></i> 已归还
                                            </p>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="card bg-secondary text-white">
                                        <div class="card-body">
                                            <h4>
                                                <c:set var="returnRate" value="0"/>
                                                <c:if test="${records.size() > 0}">
                                                    <c:set var="returnRate" value="${returnedCount * 100 / records.size()}"/>
                                                </c:if>
                                                <fmt:formatNumber value="${returnRate}" maxFractionDigits="0"/>%
                                            </h4>
                                            <p class="mb-0">
                                                <i class="bi bi-percent"></i> 归还率
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- 状态说明 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-question-circle"></i> 状态说明</h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>记录状态</h6>
                                <ul class="list-unstyled">
                                    <li class="mb-2">
                                        <span class="badge bg-warning me-2">
                                            <i class="bi bi-clock"></i> 借阅中
                                        </span>
                                        文档已借出，尚未归还
                                    </li>
                                    <li class="mb-2">
                                        <span class="badge bg-success me-2">
                                            <i class="bi bi-check-circle"></i> 已归还
                                        </span>
                                        文档已成功归还
                                    </li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6>时间显示</h6>
                                <ul class="list-unstyled">
                                    <li class="mb-2">
                                        <i class="bi bi-calendar text-primary"></i>
                                        <strong>借阅时间：</strong>记录文档借出的具体时间
                                    </li>
                                    <li class="mb-2">
                                        <i class="bi bi-calendar-check text-success"></i>
                                        <strong>归还时间：</strong>记录文档归还的具体时间，未归还显示"-"
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
                            <a href="${pageContext.request.contextPath}/borrowing/borrow" class="btn btn-success me-md-2">
                                <i class="bi bi-download"></i> 借阅文档
                            </a>
                            <a href="${pageContext.request.contextPath}/borrowing/return" class="btn btn-warning me-md-2">
                                <i class="bi bi-upload"></i> 归还文档
                            </a>
                            <a href="${pageContext.request.contextPath}/borrowing" class="btn btn-info me-md-2">
                                <i class="bi bi-arrow-left-right"></i> 借阅管理
                            </a>
                            <a href="${pageContext.request.contextPath}/documents" class="btn btn-outline-primary">
                                <i class="bi bi-folder"></i> 文档管理
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
