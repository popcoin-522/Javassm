<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
                <a class="nav-link" href="${pageContext.request.contextPath}/documents">文档列表</a>
                <a class="nav-link active" href="${pageContext.request.contextPath}/documents/manage">文档管理</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2><i class="bi bi-gear"></i> ${title}</h2>
                    <div>
                        <a href="${pageContext.request.contextPath}/documents/add" class="btn btn-success">
                            <i class="bi bi-plus-circle"></i> 添加文档
                        </a>
                        <a href="${pageContext.request.contextPath}/documents" class="btn btn-outline-primary">
                            <i class="bi bi-list-ul"></i> 查看列表
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

                <!-- 文档管理表格 -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0"><i class="bi bi-table"></i> 文档管理列表</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${empty documents}">
                                <div class="text-center py-5">
                                    <i class="bi bi-inbox display-1 text-muted"></i>
                                    <h4 class="text-muted mt-3">暂无文档</h4>
                                    <p class="text-muted">系统中还没有任何文档，请先添加文档。</p>
                                    <a href="${pageContext.request.contextPath}/documents/add" class="btn btn-primary">
                                        <i class="bi bi-plus-circle"></i> 添加文档
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover">
                                        <thead class="table-dark">
                                            <tr>
                                                <th scope="col"><i class="bi bi-hash"></i> 编号</th>
                                                <th scope="col"><i class="bi bi-file-text"></i> 名称</th>
                                                <th scope="col"><i class="bi bi-tag"></i> 类型</th>
                                                <th scope="col"><i class="bi bi-circle-fill"></i> 状态</th>
                                                <th scope="col"><i class="bi bi-calendar-check"></i> 更新时间</th>
                                                <th scope="col"><i class="bi bi-tools"></i> 操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="document" items="${documents}">
                                                <tr>
                                                    <td>
                                                        <code>${document.documentCode}</code>
                                                    </td>
                                                    <td>
                                                        <strong>${document.documentName}</strong>
                                                    </td>
                                                    <td>
                                                        <span class="badge bg-secondary">${document.documentType}</span>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${document.status == '可下载'}">
                                                                <span class="badge bg-success">
                                                                    <i class="bi bi-check-circle"></i> ${document.status}
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${document.status == '已借出'}">
                                                                <span class="badge bg-warning">
                                                                    <i class="bi bi-clock"></i> ${document.status}
                                                                </span>
                                                            </c:when>
                                                            <c:when test="${document.status == '归档'}">
                                                                <span class="badge bg-dark">
                                                                    <i class="bi bi-archive"></i> ${document.status}
                                                                </span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge bg-light text-dark">${document.status}</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:if test="${not empty document.updatedTime}">
                                                            <fmt:formatDate value="${document.updatedTime}" pattern="yyyy-MM-dd HH:mm"/>
                                                        </c:if>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group" role="group">
                                                            <a href="${pageContext.request.contextPath}/documents/edit/${document.documentCode}" 
                                                               class="btn btn-sm btn-outline-primary" title="修改文档">
                                                                <i class="bi bi-pencil"></i>
                                                            </a>
                                                            <c:if test="${document.status == '归档'}">
                                                                <button type="button" class="btn btn-sm btn-outline-danger" 
                                                                        onclick="confirmDelete('${document.documentCode}', '${document.documentName}')" 
                                                                        title="删除文档">
                                                                    <i class="bi bi-trash"></i>
                                                                </button>
                                                            </c:if>
                                                        </div>
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
                                                共管理 <strong>${documents.size()}</strong> 个文档
                                            </p>
                                        </div>
                                        <div class="col-md-6 text-end">
                                            <a href="${pageContext.request.contextPath}/documents/add" class="btn btn-success">
                                                <i class="bi bi-plus-circle"></i> 添加文档
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
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
                                <h6>修改文档</h6>
                                <ul class="list-unstyled">
                                    <li><i class="bi bi-check text-success"></i> 可以修改文档名称、类型和状态</li>
                                    <li><i class="bi bi-check text-success"></i> 支持状态变更：可下载、已借出、归档</li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6>删除文档</h6>
                                <ul class="list-unstyled">
                                    <li><i class="bi bi-exclamation-triangle text-warning"></i> 只能删除"归档"状态的文档</li>
                                    <li><i class="bi bi-exclamation-triangle text-warning"></i> 有借阅记录的文档无法删除</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 删除确认模态框 -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="bi bi-exclamation-triangle text-warning"></i> 确认删除</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>您确定要删除以下文档吗？</p>
                    <div class="alert alert-warning">
                        <strong>文档编号：</strong><span id="deleteDocumentCode"></span><br>
                        <strong>文档名称：</strong><span id="deleteDocumentName"></span>
                    </div>
                    <p class="text-danger"><i class="bi bi-exclamation-triangle"></i> 此操作不可撤销！</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                    <form id="deleteForm" method="post" style="display: inline;">
                        <button type="submit" class="btn btn-danger">
                            <i class="bi bi-trash"></i> 确认删除
                        </button>
                    </form>
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
        function confirmDelete(documentCode, documentName) {
            document.getElementById('deleteDocumentCode').textContent = documentCode;
            document.getElementById('deleteDocumentName').textContent = documentName;
            document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/documents/delete/' + documentCode;
            
            var deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
            deleteModal.show();
        }
    </script>
</body>
</html>
