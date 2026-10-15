<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Student Dashboard" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2 class="mb-0">Available Quizzes</h2>
    <a href="${ctx}/student/history" class="btn btn-outline-primary">My Results</a>
</div>

<c:if test="${empty quizzes}">
    <div class="alert alert-info">No quizzes are available yet. Please check back later.</div>
</c:if>

<div class="row g-4">
    <c:forEach var="quiz" items="${quizzes}">
        <div class="col-md-6 col-lg-4">
            <div class="card h-100 card-hover shadow-sm">
                <div class="card-body d-flex flex-column">
                    <span class="badge badge-cat align-self-start mb-2"><c:out value="${quiz.category}" /></span>
                    <h5 class="card-title"><c:out value="${quiz.quizName}" /></h5>
                    <p class="text-secondary mb-3">
                        &#10067; <c:out value="${quiz.questionCount}" /> questions &nbsp;&middot;&nbsp;
                        &#9201; <c:out value="${quiz.duration}" /> min
                    </p>
                    <div class="mt-auto">
                        <c:choose>
                            <c:when test="${quiz.attempted}">
                                <a href="${ctx}/student/history" class="btn btn-success w-100">&#10003; Completed - view result</a>
                            </c:when>
                            <c:when test="${quiz.questionCount == 0}">
                                <button class="btn btn-secondary w-100" disabled>No questions yet</button>
                            </c:when>
                            <c:otherwise>
                                <a href="${ctx}/student/quiz?id=${quiz.id}" class="btn btn-primary w-100">Start Quiz</a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </c:forEach>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
