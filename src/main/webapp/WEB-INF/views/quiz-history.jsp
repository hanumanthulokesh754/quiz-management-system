<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My Results" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<h2 class="mb-3">My Quiz Results</h2>

<c:choose>
    <c:when test="${empty results}">
        <div class="alert alert-info">
            You have not taken any quiz yet. <a href="${ctx}/student/dashboard">Start your first quiz</a>.
        </div>
    </c:when>
    <c:otherwise>
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-primary">
                    <tr><th>#</th><th>Quiz</th><th>Score</th><th>Percentage</th><th>Date</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${results}" varStatus="st">
                        <tr>
                            <td>${st.count}</td>
                            <td><a href="${ctx}/student/result?id=${r.id}"><c:out value="${r.quizName}" /></a></td>
                            <td>${r.score} / ${r.totalQuestions}</td>
                            <td><fmt:formatNumber value="${r.percentage}" maxFractionDigits="0" />%</td>
                            <td><fmt:formatDate value="${r.createdAt}" pattern="dd MMM yyyy, HH:mm" /></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/footer.jspf" %>
