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
                        <h4 class="mb-0"><i class="bi bi-plus-circle"></i> ${title}</h4>
                    </div>
                    <div class="card-body">
                        <!-- 错误信息显示 -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/documents/add" method="post" id="addDocumentForm">
                            <div class="mb-3">
                                <label for="documentCode" class="form-label">
                                    <i class="bi bi-hash"></i> 文档编号 <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="documentCode" name="documentCode" 
                                       value="${documentCode}" maxlength="20" required>
                                <div class="form-text">
                                    <i class="bi bi-info-circle"></i> 文档的唯一标识，不超过20个字符
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="documentName" class="form-label">
                                    <i class="bi bi-file-text"></i> 文档名称 <span class="text-danger">*</span>
                                </label>
                                <input type="text" class="form-control" id="documentName" name="documentName" 
                                       value="${documentName}" maxlength="100" required>
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
                                    <option value="合同" ${documentType == '合同' ? 'selected' : ''}>合同</option>
                                    <option value="方案" ${documentType == '方案' ? 'selected' : ''}>方案</option>
                                    <option value="报告" ${documentType == '报告' ? 'selected' : ''}>报告</option>
                                    <option value="技术资料" ${documentType == '技术资料' ? 'selected' : ''}>技术资料</option>
                                    <option value="规范文档" ${documentType == '规范文档' ? 'selected' : ''}>规范文档</option>
                                    <option value="培训材料" ${documentType == '培训材料' ? 'selected' : ''}>培训材料</option>
                                    <option value="其他" ${documentType == '其他' ? 'selected' : ''}>其他</option>
                                </select>
                                <div class="form-text">
                                    <i class="bi bi-info-circle"></i> 选择文档的分类类型
                                </div>
                            </div>

                            <div class="alert alert-info">
                                <i class="bi bi-info-circle"></i> 
                                <strong>提示：</strong>新添加的文档默认状态为"可下载"，可以立即被借阅使用。
                            </div>

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/documents/manage" class="btn btn-secondary me-md-2">
                                    <i class="bi bi-arrow-left"></i> 返回
                                </a>
                                <button type="reset" class="btn btn-outline-secondary me-md-2">
                                    <i class="bi bi-arrow-clockwise"></i> 重置
                                </button>
                                <button type="submit" class="btn btn-success">
                                    <i class="bi bi-check-circle"></i> 添加文档
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- 添加说明 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-question-circle"></i> 添加说明</h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>字段要求</h6>
                                <ul class="list-unstyled">
                                    <li><i class="bi bi-check text-success"></i> 文档编号：必填，不超过20字符，系统内唯一</li>
                                    <li><i class="bi bi-check text-success"></i> 文档名称：必填，不超过100字符</li>
                                    <li><i class="bi bi-check text-success"></i> 文档类型：必选，从预定义类型中选择</li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6>默认设置</h6>
                                <ul class="list-unstyled">
                                    <li><i class="bi bi-info-circle text-info"></i> 状态：自动设置为"可下载"</li>
                                    <li><i class="bi bi-info-circle text-info"></i> 创建时间：自动记录当前时间</li>
                                    <li><i class="bi bi-info-circle text-info"></i> 更新时间：自动记录当前时间</li>
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
        // 表单验证
        document.getElementById('addDocumentForm').addEventListener('submit', function(e) {
            var documentCode = document.getElementById('documentCode').value.trim();
            var documentName = document.getElementById('documentName').value.trim();
            var documentType = document.getElementById('documentType').value;
            
            if (!documentCode) {
                alert('请输入文档编号');
                e.preventDefault();
                return false;
            }
            
            if (documentCode.length > 20) {
                alert('文档编号不能超过20个字符');
                e.preventDefault();
                return false;
            }
            
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
            
            return true;
        });
    </script>
</body>
</html>
