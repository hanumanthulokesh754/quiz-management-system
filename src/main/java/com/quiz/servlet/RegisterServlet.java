package com.quiz.servlet;

import com.quiz.dao.UserDAO;
import com.quiz.model.User;
import com.quiz.util.PasswordUtil;
import com.quiz.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/** Student registration. The role is always STUDENT - it is never read from the form. */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        if (user != null) {
            WebUtil.redirectHome(req, resp, user);
            return;
        }
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = WebUtil.trim(req.getParameter("name"));
        String email = WebUtil.trim(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirm = req.getParameter("confirmPassword") == null ? "" : req.getParameter("confirmPassword");

        List<String> errors = new ArrayList<>();
        if (name.length() < 2 || name.length() > 100) {
            errors.add("Name must be 2 to 100 characters long.");
        }
        if (!WebUtil.isValidEmail(email)) {
            errors.add("Please enter a valid email address.");
        }
        if (password.length() < 8 || password.length() > 72) {
            errors.add("Password must be 8 to 72 characters long.");
        }
        if (!password.equals(confirm)) {
            errors.add("Passwords do not match.");
        }

        try {
            if (errors.isEmpty() && userDao.emailExists(email)) {
                errors.add("This email is already registered. Please log in.");
            }
            if (errors.isEmpty()) {
                userDao.create(new User(name, email, PasswordUtil.hash(password), "STUDENT"));
                WebUtil.flash(req.getSession(), "success", "Registration successful! Please log in.");
                resp.sendRedirect(req.getContextPath() + "/login"); // Post/Redirect/Get
                return;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            errors.add("This email is already registered. Please log in."); // two people registering at once
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
            return;
        }

        req.setAttribute("errors", errors);
        req.setAttribute("name", name);
        req.setAttribute("email", email);
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }
}
