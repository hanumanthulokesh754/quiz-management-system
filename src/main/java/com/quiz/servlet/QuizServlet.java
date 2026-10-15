package com.quiz.servlet;

import com.quiz.dao.QuestionDAO;
import com.quiz.dao.QuizDAO;
import com.quiz.dao.ResultDAO;
import com.quiz.model.Question;
import com.quiz.model.Quiz;
import com.quiz.model.User;
import com.quiz.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** Shows the questions of a quiz. Correct answers are NOT loaded (findByQuizId(..., false)). */
@WebServlet("/student/quiz")
public class QuizServlet extends HttpServlet {

    private final QuizDAO quizDao = new QuizDAO();
    private final QuestionDAO questionDao = new QuestionDAO();
    private final ResultDAO resultDao = new ResultDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        HttpSession session = req.getSession();
        int quizId = WebUtil.parseInt(req.getParameter("id"), 0);
        String home = req.getContextPath() + "/student/dashboard";

        try {
            Quiz quiz = quizDao.findById(quizId);
            if (quiz == null) {
                WebUtil.flash(session, "danger", "Quiz not found.");
                resp.sendRedirect(home);
                return;
            }
            if (resultDao.hasAttempted(user.getId(), quizId)) {
                WebUtil.flash(session, "warning", "You have already completed this quiz.");
                resp.sendRedirect(home);
                return;
            }
            List<Question> questions = questionDao.findByQuizId(quizId, false);
            if (questions.isEmpty()) {
                WebUtil.flash(session, "warning", "This quiz has no questions yet.");
                resp.sendRedirect(home);
                return;
            }

            // Remember when the student started, so the server can check the time limit on submit.
            String key = "quizStart_" + quizId;
            long limitMs = quiz.getDuration() * 60L * 1000L;
            long now = System.currentTimeMillis();
            Long start = (Long) session.getAttribute(key);
            if (start == null || now - start > limitMs) {
                start = now;
                session.setAttribute(key, start);
            }
            long remainingSeconds = Math.max(0, (limitMs - (now - start)) / 1000);

            req.setAttribute("quiz", quiz);
            req.setAttribute("questions", questions);
            req.setAttribute("remainingSeconds", remainingSeconds);
            req.getRequestDispatcher("/WEB-INF/views/quiz.jsp").forward(req, resp);
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
        }
    }
}
