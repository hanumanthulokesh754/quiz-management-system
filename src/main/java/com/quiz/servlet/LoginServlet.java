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
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

/** Login for both students and admins. */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = WebUtil.currentUser(req);
        if (user != null) {
            WebUtil.redirectHome(req, resp, user);
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = WebUtil.trim(req.getParameter("email")).toLowerCase();
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        if (email.isEmpty() || password.isEmpty()) {
            fail(req, resp, email, "Please enter your email and password.");
            return;
        }

        try {
            User user = userDao.findByEmail(email);
            boolean ok = PasswordUtil.verify(password, user == null ? null : user.getPassword());
            if (!ok) {
                // same message for "unknown email" and "wrong password"
                fail(req, resp, email, "Invalid email or password.");
                return;
            }

            HttpSession session = req.getSession();
            req.changeSessionId();          // new session id after login (prevents session fixation)
            user.setPassword(null);         // never keep the hash in the session
            session.setAttribute("user", user);
            WebUtil.redirectHome(req, resp, user);
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
        }
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String email, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.setAttribute("email", email);
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }
}
