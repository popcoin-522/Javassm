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
                <a class="nav-link" href="${pageContext.request.contextPath}/borrowing">借阅管理</a>
                <a class="nav-link" href="${pageContext.request.contextPath}/borrowing/records">借阅记录</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div class="row justify-content-center">
            <div class="col-md-10">
                <div class="card">
                    <div class="card-header">
                        <h4 class="mb-0"><i class="bi bi-upload"></i> ${title}</h4>
                    </div>
                    <div class="card-body">
                        <!-- 错误信息显示 -->
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                <i class="bi bi-exclamation-triangle"></i> ${errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        </c:if>

                        <!-- 归还表单 -->
                        <form action="${pageContext.request.contextPath}/borrowing/return" method="post" id="returnForm" class="mb-4">
                            <div class="row">
                                <div class="col-md-4">
                                    <label for="documentCode" class="form-label">
                                        <i class="bi bi-file-text"></i> 文档编号 <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="documentCode" name="documentCode" 
                                           value="${documentCode}" maxlength="20" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="userId" class="form-label">
                                        <i class="bi bi-person-badge"></i> 工号/学号 <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="userId" name="userId" 
                                           value="${userId}" maxlength="20" required>
                                </div>
                                <div class="col-md-4">
                                    <label for="userName" class="form-label">
                                        <i class="bi bi-person"></i> 姓名 <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="userName" name="userName" 
                                           value="${userName}" maxlength="10" required>
                                </div>
                            </div>
                            
                            <div class="mt-3">
                                <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                    <a href="${pageContext.request.contextPath}/borrowing" class="btn btn-secondary me-md-2">
                                        <i class="bi bi-arrow-left"></i> 返回
                                    </a>
                                    <button type="reset" class="btn btn-outline-secondary me-md-2">
                                        <i class="bi bi-arrow-clockwise"></i> 重置
                                    </button>
                                    <button type="submit" class="btn btn-warning">
                                        <i class="bi bi-check-circle"></i> 确认归还
                                    </button>
                                </div>
                            </div>
                        </form>

                        <div class="alert alert-warning">
                            <i class="bi bi-exclamation-triangle"></i> 
                            <strong>归还须知：</strong>
                            <ul class="mb-0 mt-2">
                                <li>请确保输入的信息与借阅时完全一致</li>
                                <li>归还成功后，文档状态将恢复为"可下载"</li>
                                <li>系统将自动记录归还时间</li>
                                <li>如信息不匹配，将提示"无此借阅记录"</li>
                            </ul>
                        </div>
                    </div>
                </div>

                <!-- 未归还借阅记录列表 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-clock-history"></i> 未归还借阅记录</h6>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty activeRecords}">
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover">
                                        <thead class="table-dark">
                                            <tr>
                                                <th><i class="bi bi-hash"></i> 文档编号</th>
                                                <th><i class="bi bi-person-badge"></i> 工号/学号</th>
                                                <th><i class="bi bi-person"></i> 姓名</th>
                                                <th><i class="bi bi-calendar"></i> 借阅时间</th>
                                                <th><i class="bi bi-circle-fill"></i> 状态</th>
                                                <th><i class="bi bi-tools"></i> 操作</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="record" items="${activeRecords}">
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
                                                        <span class="badge bg-warning">
                                                            <i class="bi bi-clock"></i> ${record.status}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <button type="button" class="btn btn-sm btn-outline-warning" 
                                                                onclick="fillReturnForm('${record.documentCode}', '${record.userId}', '${record.userName}')" 
                                                                title="填入归还表单">
                                                            <i class="bi bi-arrow-up-circle"></i> 归还
                                                        </button>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                                
                                <div class="mt-3">
                                    <p class="text-muted">
                                        <i class="bi bi-info-circle"></i> 
                                        共有 <strong>${activeRecords.size()}</strong> 条未归还记录
                                    </p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="text-center text-muted py-4">
                                    <i class="bi bi-check-circle display-4 text-success"></i>
                                    <h5 class="mt-3">暂无未归还记录</h5>
                                    <p>所有借阅的文档都已归还，状态良好！</p>
                                    <a href="${pageContext.request.contextPath}/borrowing/borrow" class="btn btn-success">
                                        <i class="bi bi-download"></i> 借阅文档
                                    </a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- 操作提示 -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h6 class="mb-0"><i class="bi bi-lightbulb"></i> 操作提示</h6>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>快速归还</h6>
                                <p class="small text-muted">
                                    点击未归还记录列表中的"归还"按钮，系统会自动填入对应的文档编号、工号和姓名信息到归还表单中。
                                </p>
                            </div>
                            <div class="col-md-6">
                                <h6>手动输入</h6>
                                <p class="small text-muted">
                                    如果您知道具体的借阅信息，也可以直接在上方表单中手动输入文档编号、工号/学号和姓名进行归还。
                                </p>
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
        // 填入归还表单函数
        function fillReturnForm(documentCode, userId, userName) {
            document.getElementById('documentCode').value = documentCode;
            document.getElementById('userId').value = userId;
            document.getElementById('userName').value = userName;
            
            // 滚动到表单顶部
            document.getElementById('returnForm').scrollIntoView({ behavior: 'smooth' });
            
            // 高亮表单
            var form = document.getElementById('returnForm');
            form.style.border = '2px solid #ffc107';
            setTimeout(function() {
                form.style.border = '';
            }, 2000);
        }
        
        // 表单验证
        document.getElementById('returnForm').addEventListener('submit', function(e) {
            var documentCode = document.getElementById('documentCode').value.trim();
            var userId = document.getElementById('userId').value.trim();
            var userName = document.getElementById('userName').value.trim();
            
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
            
            // 确认归还
            if (!confirm('确认归还文档：' + documentCode + '？\n借阅人：' + userName + '(' + userId + ')')) {
                e.preventDefault();
                return false;
            }
            
            return true;
        });
    </script>
</body>
</html>
