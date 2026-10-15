<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- The same page is used for adding (question.id == 0) and editing (question.id > 0). --%>
<c:set var="editing" value="${question.id > 0}" />
<c:set var="pageTitle" value="${editing ? 'Edit Question' : 'Add Question'}" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="card shadow-sm mx-auto" style="max-width: 760px;">
    <div class="card-body p-4">
        <h3 class="mb-3"><c:out value="${pageTitle}" /></h3>

        <c:if test="${not empty errors}">
            <div class="alert alert-danger">
                <ul class="mb-0 ps-3">
                    <c:forEach var="e" items="${errors}"><li><c:out value="${e}" /></li></c:forEach>
                </ul>
            </div>
        </c:if>

        <c:if test="${empty quizzes}">
            <div class="alert alert-warning">
                Create a quiz first on the <a href="${ctx}/admin/dashboard">dashboard</a>, then add questions to it.
            </div>
        </c:if>

        <form action="${ctx}/admin/questions/${editing ? 'edit' : 'add'}" method="post">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
            <c:if test="${editing}"><input type="hidden" name="id" value="${question.id}"></c:if>

            <div class="mb-3">
                <label class="form-label" for="quizId">Quiz</label>
                <select class="form-select" id="quizId" name="quizId" required>
                    <option value="">-- choose a quiz --</option>
                    <c:forEach var="z" items="${quizzes}">
                        <option value="${z.id}" ${z.id == question.quizId ? 'selected' : ''}>
                            <c:out value="${z.quizName}" />
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="mb-3">
                <label class="form-label" for="questionText">Question</label>
                <textarea class="form-control" id="questionText" name="questionText" rows="3" maxlength="1000" required><c:out value="${question.questionText}" /></textarea>
            </div>

            <div class="row g-3 mb-3">
                <div class="col-md-6"><label class="form-label" for="optionA">Option A</label>
                    <input class="form-control" id="optionA" name="optionA" maxlength="255" required value="<c:out value='${question.optionA}' />"></div>
                <div class="col-md-6"><label class="form-label" for="optionB">Option B</label>
                    <input class="form-control" id="optionB" name="optionB" maxlength="255" required value="<c:out value='${question.optionB}' />"></div>
                <div class="col-md-6"><label class="form-label" for="optionC">Option C</label>
                    <input class="form-control" id="optionC" name="optionC" maxlength="255" required value="<c:out value='${question.optionC}' />"></div>
                <div class="col-md-6"><label class="form-label" for="optionD">Option D</label>
                    <input class="form-control" id="optionD" name="optionD" maxlength="255" required value="<c:out value='${question.optionD}' />"></div>
            </div>

            <div class="mb-4">
                <label class="form-label" for="correctAnswer">Correct answer</label>
                <select class="form-select" id="correctAnswer" name="correctAnswer" required>
                    <option value="A" ${question.correctAnswer == 'A' ? 'selected' : ''}>A</option>
                    <option value="B" ${question.correctAnswer == 'B' ? 'selected' : ''}>B</option>
                    <option value="C" ${question.correctAnswer == 'C' ? 'selected' : ''}>C</option>
                    <option value="D" ${question.correctAnswer == 'D' ? 'selected' : ''}>D</option>
                </select>
            </div>

            <button type="submit" class="btn btn-primary">${editing ? 'Save Changes' : 'Add Question'}</button>
            <a href="${ctx}/admin/questions" class="btn btn-outline-secondary ms-2">Cancel</a>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
