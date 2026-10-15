package com.quiz.servlet;

import com.quiz.dao.QuestionDAO;
import com.quiz.dao.QuizDAO;
import com.quiz.dao.ResultDAO;
import com.quiz.dao.UserDAO;
import com.quiz.model.Quiz;
import com.quiz.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/** Admin dashboard, all students' results, and creating a new quiz. */
@WebServlet({"/admin/dashboard", "/admin/results", "/admin/quizzes/add"})
public class AdminServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();
    private final QuizDAO quizDao = new QuizDAO();
    private final QuestionDAO questionDao = new QuestionDAO();
    private final ResultDAO resultDao = new ResultDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (req.getServletPath()) {
                case "/admin/results":
                    req.setAttribute("results", resultDao.findAll());
                    req.getRequestDispatcher("/WEB-INF/views/view-results.jsp").forward(req, resp);
                    break;
                case "/admin/dashboard":
                    req.setAttribute("studentCount", userDao.countByRole("STUDENT"));
                    req.setAttribute("quizCount", quizDao.count());
                    req.setAttribute("questionCount", questionDao.count());
                    req.setAttribute("resultCount", resultDao.count());
                    req.setAttribute("quizzes", quizDao.findAll());
                    req.getRequestDispatcher("/WEB-INF/views/admin-dashboard.jsp").forward(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            }
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/admin/quizzes/add".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String name = WebUtil.trim(req.getParameter("quizName"));
        String category = WebUtil.trim(req.getParameter("category"));
        int duration = WebUtil.parseInt(req.getParameter("duration"), 0);

        String error = null;
        if (name.isEmpty() || name.length() > 150) {
            error = "Quiz name is required (max 150 characters).";
        } else if (category.isEmpty() || category.length() > 100) {
            error = "Category is required (max 100 characters).";
        } else if (duration < 1 || duration > 180) {
            error = "Duration must be between 1 and 180 minutes.";
        }

        try {
            if (error != null) {
                WebUtil.flash(req.getSession(), "danger", error);
            } else {
                quizDao.create(new Quiz(name, category, duration));
                WebUtil.flash(req.getSession(), "success", "Quiz \"" + name + "\" created. Now add questions to it.");
            }
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard"); // Post/Redirect/Get
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
        }
    }
}
