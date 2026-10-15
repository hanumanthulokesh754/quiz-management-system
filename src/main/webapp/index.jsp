<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Home" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="hero text-center">
    <h1 class="display-5">Test your knowledge. Track your progress.</h1>
    <p class="lead text-secondary mb-4">Take timed multiple-choice quizzes, get instant scores and review your results any time.</p>
    <c:choose>
        <c:when test="${empty sessionScope.user}">
            <a href="${ctx}/register" class="btn btn-primary btn-lg me-2">Get Started</a>
            <a href="${ctx}/login" class="btn btn-outline-primary btn-lg">Login</a>
        </c:when>
        <c:when test="${sessionScope.user.role == 'ADMIN'}">
            <a href="${ctx}/admin/dashboard" class="btn btn-primary btn-lg">Go to Admin Dashboard</a>
        </c:when>
        <c:otherwise>
            <a href="${ctx}/student/dashboard" class="btn btn-primary btn-lg">Browse Quizzes</a>
        </c:otherwise>
    </c:choose>
</div>

<div class="row g-4 mt-2">
    <div class="col-md-4">
        <div class="card h-100 card-hover"><div class="card-body">
            <h5 class="card-title">&#128221; Multiple-choice quizzes</h5>
            <p class="card-text text-secondary">Pick one of four options for every question using simple radio buttons.</p>
        </div></div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 card-hover"><div class="card-body">
            <h5 class="card-title">&#9889; Instant, secure scoring</h5>
            <p class="card-text text-secondary">Scores are calculated on the server, so answers can never be seen or changed in the browser.</p>
        </div></div>
    </div>
    <div class="col-md-4">
        <div class="card h-100 card-hover"><div class="card-body">
            <h5 class="card-title">&#128202; Result history</h5>
            <p class="card-text text-secondary">Every attempt is saved so you can look back at your scores whenever you want.</p>
        </div></div>
    </div>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
