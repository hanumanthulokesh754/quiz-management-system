package com.quiz.servlet;

import com.quiz.dao.ResultDAO;
import com.quiz.model.Result;
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

/** /student/result shows one result, /student/history lists all of the student's results. */
@WebServlet({"/student/result", "/student/history"})
public class ResultServlet extends HttpServlet {

    private final ResultDAO resultDao = new ResultDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        try {
            if ("/student/history".equals(req.getServletPath())) {
                List<Result> results = resultDao.findByUser(user.getId());
                req.setAttribute("results", results);
                req.getRequestDispatcher("/WEB-INF/views/quiz-history.jsp").forward(req, resp);
                return;
            }

            int id = WebUtil.parseInt(req.getParameter("id"), 0);
            Result result = resultDao.findByIdAndUser(id, user.getId());
            if (result == null) {
                WebUtil.flash(req.getSession(), "danger", "Result not found.");
                resp.sendRedirect(req.getContextPath() + "/student/history");
                return;
            }
            req.setAttribute("result", result);
            req.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(req, resp);
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
        }
    }
}
