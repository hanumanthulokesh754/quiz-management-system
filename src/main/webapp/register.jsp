<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Register" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="card auth-card shadow-sm">
    <div class="card-body p-4">
        <h3 class="mb-3 text-center">Create student account</h3>

        <c:if test="${not empty errors}">
            <div class="alert alert-danger">
                <ul class="mb-0 ps-3">
                    <c:forEach var="e" items="${errors}"><li><c:out value="${e}" /></li></c:forEach>
                </ul>
            </div>
        </c:if>

        <form action="${ctx}/register" method="post" novalidate>
            <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
            <div class="mb-3">
                <label for="name" class="form-label">Full name</label>
                <input type="text" class="form-control" id="name" name="name" maxlength="100" required
                       value="<c:out value='${name}' />">
            </div>
            <div class="mb-3">
                <label for="email" class="form-label">Email</label>
                <input type="email" class="form-control" id="email" name="email" maxlength="150" required
                       value="<c:out value='${email}' />">
            </div>
            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" class="form-control" id="password" name="password" minlength="8" maxlength="72" required>
                <div class="form-text">At least 8 characters.</div>
            </div>
            <div class="mb-3">
                <label for="confirmPassword" class="form-label">Confirm password</label>
                <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" minlength="8" maxlength="72" required>
            </div>
            <button type="submit" class="btn btn-primary w-100">Register</button>
        </form>
        <p class="text-center mt-3 mb-0">Already registered? <a href="${ctx}/login">Login</a></p>
    </div>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
