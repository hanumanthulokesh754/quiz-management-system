<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Login" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="card auth-card shadow-sm">
    <div class="card-body p-4">
        <h3 class="mb-3 text-center">Login</h3>

        <c:if test="${not empty error}">
            <div class="alert alert-danger"><c:out value="${error}" /></div>
        </c:if>

        <form action="${ctx}/login" method="post">
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
            <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input type="email" class="form-control" id="email" name="email" required autofocus
                       value="<c:out value='${email}' />">
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" class="form-control" id="password" name="password" required>
            </div>
            <button type="submit" class="btn btn-primary w-100">Login</button>
        </form>
        <p class="text-center mt-3 mb-0">New student? <a href="${ctx}/register">Create an account</a></p>
    </div>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
