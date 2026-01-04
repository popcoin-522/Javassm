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
                <a class="nav-link" href="${pageContext.request.contextPath}/documents">文档列表</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/documents/manage">文档管理</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h4 class="mb-0"><i class="bi bi-pencil"></i> ${title}</h4>
                    </div>
                    <div class="card-body">
                        <!-- 错误信息显示 -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/documents/edit/${document.documentCode}" method="post" id="editDocumentForm">
                            <div class="mb-3">
                                <label for="documentCode" class="form-label">
                                    <i class="bi bi-hash"></i> 文档编号
                                </label>
                                <input type="text" class="form-control" id="documentCode" name="documentCode" 
                                       value="${document.documentCode}" readonly>
                                <div class="form-text">
                                    <i class="bi bi-info-circle"></i> 文档编号不可修改
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="documentName" class="form-label">
                                    <i class="bi bi-file-text"></i> 文档名称 <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="documentName" name="documentName" 
                                       value="${document.documentName}" maxlength="100" required>
                                <div class="form-text">
                                    <i class="bi bi-info-circle"></i> 文档的显示名称，不超过100个字符
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="documentType" class="form-label">
                                    <i class="bi bi-tag"></i> 文档类型 <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="documentType" name="documentType" required>
                                    <option value="">请选择文档类型</option>
                                    <option value="合同" ${document.documentType == '合同' ? 'selected' : ''}>合同</option>
                                    <option value="方案" ${document.documentType == '方案' ? 'selected' : ''}>方案</option>
                                    <option value="报告" ${document.documentType == '报告' ? 'selected' : ''}>报告</option>
                                    <option value="技术资料" ${document.documentType == '技术资料' ? 'selected' : ''}>技术资料</option>
                                    <option value="规范文档" ${document.documentType == '规范文档' ? 'selected' : ''}>规范文档</option>
                                    <option value="培训材料" ${document.documentType == '培训材料' ? 'selected' : ''}>培训材料</option>
                                    <option value="其他" ${document.documentType == '其他' ? 'selected' : ''}>其他</option>
                                </select>
                                <div class="form-text">
                                    <i class="bi bi-info-circle"></i> 选择文档的分类类型
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="status" class="form-label">
                                    <i class="bi bi-circle-fill"></i> 文档状态 <span class="text-danger">*</span>
                                </label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="">请选择文档状态</option>
                                    <option value="可下载" ${document.status == '可下载' ? 'selected' : ''}>
                                        <i class="bi bi-check-circle"></i> 可下载
                                    </option>
                                    <option value="已借出" ${document.status == '已借出' ? 'selected' : ''}>
                                        <i class="bi bi-clock"></i> 已借出
                                    </option>
                                    <option value="归档" ${document.status == '归档' ? 'selected' : ''}>
                                        <i class="bi bi-archive"></i> 归档
                                    </option>
                                </select>
                                <div class="form-text">
                                    <i class="bi bi-info-circle"></i> 选择文档的当前状态
                                </div>
                            </div>

                            <div class="alert alert-warning">
                                <i class="bi bi-exclamation-triangle"></i> 
                                <strong>注意：</strong>
                                <ul class="mb-0 mt-2">
                                    <li>修改状态为"已借出"时，请确保文档确实被借阅</li>
                                    <li>修改状态为"归档"时，文档将不可被借阅</li>
                                    <li>只有"归档"状态的文档可以被删除</li>
                                </ul>
                            </div>

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/documents/manage" class="btn btn-secondary me-md-2">
                                    <i class="bi bi-arrow-left"></i> 返回
                                </a>
                                <button type="reset" class="btn btn-outline-secondary me-md-2">
                                    <i class="bi bi-arrow-clockwise"></i> 重置
                                </button>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-check-circle"></i> 保存修改
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- 文档信息 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-info-circle"></i> 文档信息</h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>创建时间：</strong>
                                    <c:if test="${not empty document.createdTime}">
                                        <fmt:formatDate value="${document.createdTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    </c:if>
                                </p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>更新时间：</strong>
                                    <c:if test="${not empty document.updatedTime}">
                                        <fmt:formatDate value="${document.updatedTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                                    </c:if>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 状态说明 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-question-circle"></i> 状态说明</h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <span class="badge bg-success me-2">
                                    <i class="bi bi-check-circle"></i> 可下载
                                </span>
                                <p class="small text-muted mt-1">文档可以被用户借阅使用</p>
                            </div>
                            <div class="col-md-4">
                                <span class="badge bg-warning me-2">
                                    <i class="bi bi-clock"></i> 已借出
                                </span>
                                <p class="small text-muted mt-1">文档已被借阅，暂时不可用</p>
                            </div>
                            <div class="col-md-4">
                                <span class="badge bg-dark me-2">
                                    <i class="bi bi-archive"></i> 归档
                                </span>
                                <p class="small text-muted mt-1">文档已归档，不可借阅，可删除</p>
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
        // 表单验证
        document.getElementById('editDocumentForm').addEventListener('submit', function(e) {
            var documentName = document.getElementById('documentName').value.trim();
            var documentType = document.getElementById('documentType').value;
            var status = document.getElementById('status').value;
            
            if (!documentName) {
                alert('请输入文档名称');
                e.preventDefault();
                return false;
            }
            
            if (documentName.length > 100) {
                alert('文档名称不能超过100个字符');
                e.preventDefault();
                return false;
            }
            
            if (!documentType) {
                alert('请选择文档类型');
                e.preventDefault();
                return false;
            }
            
            if (!status) {
                alert('请选择文档状态');
                e.preventDefault();
                return false;
            }
            
            return true;
        });
    </script>
</body>
</html>
