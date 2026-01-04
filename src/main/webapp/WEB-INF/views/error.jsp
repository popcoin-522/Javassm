<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>系统错误</title>
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
            </div>
        </div>
    </nav>

    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card border-danger">
                    <div class="card-header bg-danger text-white">
                        <h4 class="mb-0"><i class="bi bi-exclamation-triangle"></i> 系统错误</h4>
                    </div>
                    <div class="card-body">
                        <div class="text-center mb-4">
                            <c:choose>
                                <c:when test="${errorType == 'validation'}">
                                    <i class="bi bi-exclamation-triangle display-1 text-warning"></i>
                                </c:when>
                                <c:when test="${errorType == 'business'}">
                                    <i class="bi bi-shield-exclamation display-1 text-info"></i>
                                </c:when>
                                <c:when test="${errorType == 'database'}">
                                    <i class="bi bi-database-exclamation display-1 text-danger"></i>
                                </c:when>
                                <c:when test="${errorType == 'sql'}">
                                    <i class="bi bi-server display-1 text-danger"></i>
                                </c:when>
                                <c:when test="${errorType == 'system'}">
                                    <i class="bi bi-gear-wide-connected display-1 text-danger"></i>
                                </c:when>
                                <c:when test="${errorType == 'parameter'}">
                                    <i class="bi bi-input-cursor display-1 text-warning"></i>
                                </c:when>
                                <c:when test="${errorType == 'runtime'}">
                                    <i class="bi bi-cpu display-1 text-danger"></i>
                                </c:when>
                                <c:otherwise>
                                    <i class="bi bi-bug display-1 text-danger"></i>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">
                                <h6><i class="bi bi-exclamation-circle"></i> 错误信息：</h6>
                                <p class="mb-0">${error}</p>
                            </div>
                        </c:if>
                        
                        <c:if test="${not empty suggestion}">
                            <div class="alert alert-info">
                                <h6><i class="bi bi-lightbulb"></i> 解决建议：</h6>
                                <p class="mb-0">${suggestion}</p>
                            </div>
                        </c:if>
                        
                        <c:if test="${empty error}">
                            <div class="alert alert-warning">
                                <h6><i class="bi bi-question-circle"></i> 未知错误</h6>
                                <p class="mb-0">系统遇到了未知错误，请稍后重试或联系管理员。</p>
                            </div>
                        </c:if>

                        <div class="text-center mt-4">
                            <div class="btn-group" role="group">
                                <button type="button" class="btn btn-outline-secondary" onclick="history.back()">
                                    <i class="bi bi-arrow-left"></i> 返回上页
                                </button>
                                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                                    <i class="bi bi-house"></i> 返回首页
                                </a>
                                <button type="button" class="btn btn-outline-primary" onclick="location.reload()">
                                    <i class="bi bi-arrow-clockwise"></i> 刷新页面
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 帮助信息 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-lightbulb"></i> 解决建议</h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>常见解决方法</h6>
                                <ul class="list-unstyled">
                                    <li><i class="bi bi-check text-success"></i> 检查输入信息是否正确</li>
                                    <li><i class="bi bi-check text-success"></i> 刷新页面重新尝试</li>
                                    <li><i class="bi bi-check text-success"></i> 清除浏览器缓存</li>
                                    <li><i class="bi bi-check text-success"></i> 检查网络连接状态</li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6>联系支持</h6>
                                <p class="small text-muted">
                                    如果问题持续存在，请联系系统管理员并提供以下信息：
                                </p>
                                <ul class="list-unstyled small">
                                    <li><strong>时间：</strong><span id="currentTime"></span></li>
                                    <li><strong>页面：</strong><span id="currentUrl"></span></li>
                                    <li><strong>浏览器：</strong><span id="userAgent"></span></li>
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
            </p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // 显示当前时间
        document.getElementById('currentTime').textContent = new Date().toLocaleString('zh-CN');
        
        // 显示当前URL
        document.getElementById('currentUrl').textContent = window.location.href;
        
        // 显示浏览器信息
        document.getElementById('userAgent').textContent = navigator.userAgent.split(' ').slice(0, 3).join(' ') + '...';
    </script>
</body>
</html>
