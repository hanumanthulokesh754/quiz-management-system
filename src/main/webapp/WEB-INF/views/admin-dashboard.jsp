<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Admin Dashboard" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<h2 class="mb-3">Admin Dashboard</h2>

<div class="row g-3 mb-4">
    <div class="col-6 col-lg-3"><div class="card stat-card"><div class="card-body">
        <div class="stat-number">${studentCount}</div><div class="text-secondary">Students</div></div></div></div>
    <div class="col-6 col-lg-3"><div class="card stat-card"><div class="card-body">
        <div class="stat-number">${quizCount}</div><div class="text-secondary">Quizzes</div></div></div></div>
    <div class="col-6 col-lg-3"><div class="card stat-card"><div class="card-body">
        <div class="stat-number">${questionCount}</div><div class="text-secondary">Questions</div></div></div></div>
    <div class="col-6 col-lg-3"><div class="card stat-card"><div class="card-body">
        <div class="stat-number">${resultCount}</div><div class="text-secondary">Submissions</div></div></div></div>
</div>

<div class="mb-4">
    <a href="${ctx}/admin/questions/add" class="btn btn-primary me-2">+ Add Question</a>
    <a href="${ctx}/admin/questions" class="btn btn-outline-primary me-2">View Questions</a>
    <a href="${ctx}/admin/results" class="btn btn-outline-primary">View Results</a>
</div>

<div class="row g-4">
    <div class="col-lg-7">
        <div class="card shadow-sm"><div class="card-body">
            <h5 class="card-title">Quizzes</h5>
            <div class="table-responsive">
                <table class="table table-sm align-middle mb-0">
                    <thead><tr><th>Name</th><th>Category</th><th>Minutes</th><th>Questions</th><th></th></tr></thead>
                    <tbody>
                        <c:forEach var="quiz" items="${quizzes}">
                            <tr>
                                <td><c:out value="${quiz.quizName}" /></td>
                                <td><c:out value="${quiz.category}" /></td>
                                <td>${quiz.duration}</td>
                                <td>${quiz.questionCount}</td>
                                <td class="text-end"><a href="${ctx}/admin/questions?quizId=${quiz.id}">Manage</a></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty quizzes}"><tr><td colspan="5" class="text-secondary">No quizzes yet.</td></tr></c:if>
                    </tbody>
                </table>
            </div>
        </div></div>
    </div>

    <div class="col-lg-5">
        <div class="card shadow-sm"><div class="card-body">
            <h5 class="card-title">Create a new quiz</h5>
            <form action="${ctx}/admin/quizzes/add" method="post">
                <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                <div class="mb-2">
                    <label class="form-label" for="quizName">Quiz name</label>
                    <input class="form-control" id="quizName" name="quizName" maxlength="150" required>
                </div>
                <div class="mb-2">
                    <label class="form-label" for="category">Category</label>
                    <input class="form-control" id="category" name="category" maxlength="100" required>
                </div>
                <div class="mb-3">
                    <label class="form-label" for="duration">Duration (minutes)</label>
                    <input type="number" class="form-control" id="duration" name="duration" min="1" max="180" value="10" required>
                </div>
                <button type="submit" class="btn btn-primary">Create Quiz</button>
            </form>
        </div></div>
    </div>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
