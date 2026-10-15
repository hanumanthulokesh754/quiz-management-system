package com.quiz.servlet;

import com.quiz.dao.QuizDAO;
import com.quiz.model.Quiz;
import com.quiz.model.User;
import com.quiz.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** Student dashboard: lists the available quizzes. */
@WebServlet("/student/dashboard")
public class DashboardServlet extends HttpServlet {

    private final QuizDAO quizDao = new QuizDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        try {
            List<Quiz> quizzes = quizDao.findAllForStudent(user.getId());
            req.setAttribute("quizzes", quizzes);
            req.getRequestDispatcher("/WEB-INF/views/student-dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
        }
    }
}
