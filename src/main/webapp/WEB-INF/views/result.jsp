<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Quiz Result" />
<%@ include file="/WEB-INF/views/header.jspf" %>
<fmt:formatNumber value="${result.percentage}" maxFractionDigits="0" var="pct" />

<div class="card shadow-sm mx-auto" style="max-width: 560px;">
    <div class="card-body text-center p-4">
        <h3 class="mb-1"><c:out value="${result.quizName}" /></h3>
        <p class="text-secondary">
            Submitted on <fmt:formatDate value="${result.createdAt}" pattern="dd MMM yyyy, HH:mm" />
        </p>

        <div class="score-circle">${pct}%</div>

        <h4 class="mb-3">
            <c:choose>
                <c:when test="${pct >= 80}">Excellent work! &#127942;</c:when>
                <c:when test="${pct >= 50}">Good job! &#128077;</c:when>
                <c:otherwise>Keep practicing! &#128170;</c:otherwise>
            </c:choose>
        </h4>

        <div class="row g-2 mb-4">
            <div class="col-4"><div class="p-2 border rounded"><div class="stat-number">${result.score}</div><small class="text-secondary">Correct</small></div></div>
            <div class="col-4"><div class="p-2 border rounded"><div class="stat-number">${result.incorrect}</div><small class="text-secondary">Incorrect / skipped</small></div></div>
            <div class="col-4"><div class="p-2 border rounded"><div class="stat-number">${result.totalQuestions}</div><small class="text-secondary">Total</small></div></div>
        </div>

        <a href="${ctx}/student/dashboard" class="btn btn-primary me-2">More Quizzes</a>
        <a href="${ctx}/student/history" class="btn btn-outline-primary">My Results</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
