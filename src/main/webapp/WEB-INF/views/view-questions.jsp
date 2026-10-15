<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Questions" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
    <h2 class="mb-0">Questions</h2>
    <div class="d-flex gap-2">
        <form action="${ctx}/admin/questions" method="get">
            <select name="quizId" class="form-select" onchange="this.form.submit()">
                <option value="0">All quizzes</option>
                <c:forEach var="z" items="${quizzes}">
                    <option value="${z.id}" ${z.id == selectedQuizId ? 'selected' : ''}><c:out value="${z.quizName}" /></option>
                </c:forEach>
            </select>
        </form>
        <a href="${ctx}/admin/questions/add?quizId=${selectedQuizId}" class="btn btn-primary">+ Add</a>
    </div>
</div>

<c:choose>
    <c:when test="${empty questions}">
        <div class="alert alert-info">No questions found.</div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-primary">
                    <tr><th>#</th><th>Quiz</th><th>Question</th><th>A</th><th>B</th><th>C</th><th>D</th><th>Answer</th><th style="width:150px">Actions</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="q" items="${questions}" varStatus="st">
                        <tr>
                            <td>${st.count}</td>
                            <td><c:out value="${q.quizName}" /></td>
                            <td><c:out value="${q.questionText}" /></td>
                            <td><c:out value="${q.optionA}" /></td>
                            <td><c:out value="${q.optionB}" /></td>
                            <td><c:out value="${q.optionC}" /></td>
                            <td><c:out value="${q.optionD}" /></td>
                            <td><span class="badge bg-primary">${q.correctAnswer}</span></td>
                            <td>
                                <a href="${ctx}/admin/questions/edit?id=${q.id}" class="btn btn-sm btn-outline-primary">Edit</a>
                                <form action="${ctx}/admin/questions/delete" method="post" class="d-inline"
                                      onsubmit="return confirm('Delete this question?');">
                                    <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
                                    <input type="hidden" name="id" value="${q.id}">
                                    <input type="hidden" name="quizId" value="${selectedQuizId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/footer.jspf" %>
