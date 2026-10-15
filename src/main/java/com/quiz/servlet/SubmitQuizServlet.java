package com.quiz.servlet;

import com.quiz.dao.QuestionDAO;
import com.quiz.dao.QuizDAO;
import com.quiz.dao.ResultDAO;
import com.quiz.model.Question;
import com.quiz.model.Quiz;
import com.quiz.model.Result;
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
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/** Receives the answers, calculates the score ON THE SERVER, saves it, then redirects (PRG). */
@WebServlet("/student/submit")
public class SubmitQuizServlet extends HttpServlet {

    private static final long GRACE_MS = 30_000; // allowance for network delay on auto-submit

    private final QuizDAO quizDao = new QuizDAO();
    private final QuestionDAO questionDao = new QuestionDAO();
    private final ResultDAO resultDao = new ResultDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        HttpSession session = req.getSession();
        int quizId = WebUtil.parseInt(req.getParameter("quizId"), 0);
        String ctx = req.getContextPath();

        try {
            Quiz quiz = quizDao.findById(quizId);
            if (quiz == null) {
                WebUtil.flash(session, "danger", "Quiz not found.");
                resp.sendRedirect(ctx + "/student/dashboard");
                return;
            }

            // The quiz must have been opened through QuizServlet first.
            String key = "quizStart_" + quizId;
            Long start = (Long) session.getAttribute(key);
            if (start == null) {
                resp.sendRedirect(ctx + "/student/quiz?id=" + quizId);
                return;
            }
            long limitMs = quiz.getDuration() * 60L * 1000L;
            if (System.currentTimeMillis() - start > limitMs + GRACE_MS) {
                session.removeAttribute(key);
                WebUtil.flash(session, "danger", "Time limit exceeded. Your answers were not saved. You can start the quiz again.");
                resp.sendRedirect(ctx + "/student/dashboard");
                return;
            }

            if (resultDao.hasAttempted(user.getId(), quizId)) {
                WebUtil.flash(session, "warning", "You have already submitted this quiz.");
                resp.sendRedirect(ctx + "/student/history");
                return;
            }

            // ---- Server-side scoring: compare submitted letters with the correct answers in the database ----
            List<Question> questions = questionDao.findByQuizId(quizId, true);
            int score = 0;
            for (Question q : questions) {
                String chosen = req.getParameter("q_" + q.getId());
                if (chosen != null && chosen.equals(q.getCorrectAnswer())) {
                    score++;
                }
            }

            int resultId = resultDao.save(new Result(user.getId(), quizId, score, questions.size()));
            session.removeAttribute(key);
            resp.sendRedirect(ctx + "/student/result?id=" + resultId); // Post/Redirect/Get
        } catch (SQLIntegrityConstraintViolationException e) {
            // double-click or two tabs: the database unique key blocked the second submission
            WebUtil.flash(session, "warning", "You have already submitted this quiz.");
            resp.sendRedirect(ctx + "/student/history");
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
        }
    }
}
