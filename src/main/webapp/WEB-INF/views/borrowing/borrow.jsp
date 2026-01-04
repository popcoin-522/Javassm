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
                <a class="nav-link" href="${pageContext.request.contextPath}/borrowing">借阅管理</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/borrowing/records">借阅记录</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h4 class="mb-0"><i class="bi bi-download"></i> ${title}</h4>
                    </div>
                    <div class="card-body">
                        <!-- 错误信息显示 -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/borrowing/borrow" method="post" id="borrowForm">
                            <div class="mb-3">
                                <label for="documentCode" class="form-label">
                                    <i class="bi bi-file-text"></i> 选择文档 <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="documentCode" name="documentCode" required>
                                    <option value="">请选择要借阅的文档</option>
                                    <c:forEach var="document" items="${documents}">
                                        <c:if test="${document.status == '可下载'}">
                                            <option value="${document.documentCode}" 
                                                    ${documentCode == document.documentCode ? 'selected' : ''}>
                                                ${document.documentCode} - ${document.documentName} (${document.documentType})
                                            </option>
                                        </c:if>
                                    </c:forEach>
                                </select>
                                <div class="form-text">
                                    <i class="bi bi-info-circle"></i> 只显示状态为"可下载"的文档
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="userId" class="form-label">
                                    <i class="bi bi-person-badge"></i> 工号/学号 <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="userId" name="userId" 
                                       value="${userId}" maxlength="20" required>
                                <div class="form-text">
                                    <i class="bi bi-info-circle"></i> 请输入您的工号或学号，不超过20个字符
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="userName" class="form-label">
                                    <i class="bi bi-person"></i> 姓名 <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="userName" name="userName" 
                                       value="${userName}" maxlength="10" required>
                                <div class="form-text">
                                    <i class="bi bi-info-circle"></i> 请输入您的真实姓名，支持中英文，不超过10个字符
                                </div>
                            </div>

                            <div class="alert alert-info">
                                <i class="bi bi-info-circle"></i> 
                                <strong>借阅须知：</strong>
                                <ul class="mb-0 mt-2">
                                    <li>借阅成功后，文档状态将变更为"已借出"</li>
                                    <li>其他用户将无法借阅该文档，直到您归还</li>
                                    <li>请妥善保管借阅的文档，及时归还</li>
                                    <li>如有问题，请联系管理员</li>
                                </ul>
                            </div>

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/borrowing" class="btn btn-secondary me-md-2">
                                    <i class="bi bi-arrow-left"></i> 返回
                                </a>
                                <button type="reset" class="btn btn-outline-secondary me-md-2">
                                    <i class="bi bi-arrow-clockwise"></i> 重置
                                </button>
                                <button type="submit" class="btn btn-success">
                                    <i class="bi bi-check-circle"></i> 确认借阅
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- 可借阅文档列表 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-list-ul"></i> 可借阅文档列表</h6>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty documents}">
                                <c:set var="availableDocuments" value="0"/>
                                <c:forEach var="document" items="${documents}">
                                    <c:if test="${document.status == '可下载'}">
                                        <c:set var="availableDocuments" value="${availableDocuments + 1}"/>
                                    </c:if>
                                </c:forEach>
                                
                                <c:choose>
                                    <c:when test="${availableDocuments > 0}">
                                        <div class="table-responsive">
                                            <table class="table table-sm table-hover">
                                                <thead class="table-light">
                                                    <tr>
                                                        <th>编号</th>
                                                        <th>名称</th>
                                                        <th>类型</th>
                                                        <th>状态</th>
                                                        <th>操作</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="document" items="${documents}">
                                                        <c:if test="${document.status == '可下载'}">
                                                            <tr>
                                                                <td><code>${document.documentCode}</code></td>
                                                                <td>${document.documentName}</td>
                                                                <td><span class="badge bg-secondary">${document.documentType}</span></td>
                                                                <td>
                                                                    <span class="badge bg-success">
                                                                        <i class="bi bi-check-circle"></i> ${document.status}
                                                                    </span>
                                                                </td>
                                                                <td>
                                                                    <button type="button" class="btn btn-sm btn-outline-success" 
                                                                            onclick="selectDocument('${document.documentCode}', '${document.documentName}', '${document.documentType}')">
                                                                        <i class="bi bi-hand-index"></i> 选择
                                                                    </button>
                                                                </td>
                                                            </tr>
                                                        </c:if>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-center text-muted py-4">
                                            <i class="bi bi-exclamation-circle display-4"></i>
                                            <h5 class="mt-3">暂无可借阅文档</h5>
                                            <p>所有文档都已被借出或已归档，请稍后再试。</p>
                                            <a href="${pageContext.request.contextPath}/documents" class="btn btn-outline-primary">
                                                <i class="bi bi-list-ul"></i> 查看所有文档
                                            </a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center text-muted py-4">
                                    <i class="bi bi-inbox display-4"></i>
                                    <h5 class="mt-3">暂无文档</h5>
                                    <p>系统中还没有任何文档。</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
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
        // 选择文档函数
        function selectDocument(documentCode, documentName, documentType) {
            document.getElementById('documentCode').value = documentCode;
            
            // 滚动到表单顶部
            document.getElementById('borrowForm').scrollIntoView({ behavior: 'smooth' });
            
            // 高亮选中的文档选项
            var selectElement = document.getElementById('documentCode');
            selectElement.focus();
        }
        
        // 表单验证
        document.getElementById('borrowForm').addEventListener('submit', function(e) {
            var documentCode = document.getElementById('documentCode').value;
            var userId = document.getElementById('userId').value.trim();
            var userName = document.getElementById('userName').value.trim();
            
            if (!documentCode) {
                alert('请选择要借阅的文档');
                e.preventDefault();
                return false;
            }
            
            if (!userId) {
                alert('请输入工号/学号');
                e.preventDefault();
                return false;
            }
            
            if (userId.length > 20) {
                alert('工号/学号不能超过20个字符');
                e.preventDefault();
                return false;
            }
            
            if (!userName) {
                alert('请输入姓名');
                e.preventDefault();
                return false;
            }
            
            if (userName.length > 10) {
                alert('姓名不能超过10个字符');
                e.preventDefault();
                return false;
            }
            
            // 确认借阅
            var documentText = document.getElementById('documentCode').selectedOptions[0].text;
            if (!confirm('确认借阅文档：' + documentText + '？')) {
                e.preventDefault();
                return false;
            }
            
            return true;
        });
    </script>
</body>
</html>
