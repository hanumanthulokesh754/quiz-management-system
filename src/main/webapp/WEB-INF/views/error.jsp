<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Error" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<c:set var="status" value="${requestScope['jakarta.servlet.error.status_code']}" />
<div class="text-center py-5">
    <h1 class="display-4 text-primary">
        <c:choose>
            <c:when test="${not empty status}">Error <c:out value="${status}" /></c:when>
            <c:otherwise>Oops!</c:otherwise>
        </c:choose>
    </h1>
    <p class="lead text-secondary">
        <c:choose>
            <c:when test="${not empty errorMessage}"><c:out value="${errorMessage}" /></c:when>
            <c:when test="${status == 403}">You are not allowed to do that, or your form has expired. Go back, refresh the page and try again.</c:when>
            <c:when test="${status == 404}">The page you are looking for does not exist.</c:when>
            <c:otherwise>Something went wrong. Please try again.</c:otherwise>
        </c:choose>
    </p>
    <a href="${ctx}/" class="btn btn-primary">Back to Home</a>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
