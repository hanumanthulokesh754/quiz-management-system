<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${quiz.quizName}" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="timer-bar py-2 mb-3 d-flex justify-content-between align-items-center">
    <div>
        <strong><c:out value="${quiz.quizName}" /></strong>
        <span class="text-secondary">&middot; <c:out value="${questions.size()}" /> questions</span>
    </div>
    <div class="fs-5 fw-bold text-primary">&#9201; <span id="timer">--:--</span></div>
</div>

<form id="quizForm" action="${ctx}/student/submit" method="post">
    <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}' />">
    <input type="hidden" name="quizId" value="${quiz.id}">

    <c:forEach var="q" items="${questions}" varStatus="st">
        <div class="card mb-3 shadow-sm">
            <div class="card-body">
                <h6 class="mb-3">Q${st.count}. <c:out value="${q.questionText}" /></h6>

                <div>
                    <input class="form-check-input d-none" type="radio" name="q_${q.id}" id="q${q.id}a" value="A">
                    <label class="option-label" for="q${q.id}a"><strong>A.</strong> <c:out value="${q.optionA}" /></label>
                </div>
                <div>
                    <input class="form-check-input d-none" type="radio" name="q_${q.id}" id="q${q.id}b" value="B">
                    <label class="option-label" for="q${q.id}b"><strong>B.</strong> <c:out value="${q.optionB}" /></label>
                </div>
                <div>
                    <input class="form-check-input d-none" type="radio" name="q_${q.id}" id="q${q.id}c" value="C">
                    <label class="option-label" for="q${q.id}c"><strong>C.</strong> <c:out value="${q.optionC}" /></label>
                </div>
                <div>
                    <input class="form-check-input d-none" type="radio" name="q_${q.id}" id="q${q.id}d" value="D">
                    <label class="option-label" for="q${q.id}d"><strong>D.</strong> <c:out value="${q.optionD}" /></label>
                </div>
            </div>
        </div>
    </c:forEach>

    <div class="text-end mb-4">
        <button type="submit" id="submitBtn" class="btn btn-primary btn-lg">Submit Quiz</button>
    </div>
</form>

<script>
    // The timer is only a convenience: the server checks the real time limit again on submit.
    (function () {
        var remaining = ${remainingSeconds};
        var timerEl = document.getElementById('timer');
        var form = document.getElementById('quizForm');
        var button = document.getElementById('submitBtn');

        function show() {
            var m = Math.floor(remaining / 60), s = remaining % 60;
            timerEl.textContent = (m < 10 ? '0' : '') + m + ':' + (s < 10 ? '0' : '') + s;
        }
        show();
        var interval = setInterval(function () {
            remaining--;
            show();
            if (remaining <= 0) {
                clearInterval(interval);
                button.disabled = true;
                form.submit();            // time is up: send whatever has been answered
            }
        }, 1000);

        form.addEventListener('submit', function (event) {
            if (!form.dataset.sent && remaining > 0 && !confirm('Submit your answers now?')) {
                event.preventDefault();
                return;
            }
            form.dataset.sent = 'true';
            button.disabled = true;       // prevents double submission
        });
    })();
</script>

<%@ include file="/WEB-INF/views/footer.jspf" %>
