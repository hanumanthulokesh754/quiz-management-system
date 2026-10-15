package com.quiz.util;

import com.quiz.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/** Small helpers shared by the servlets. */
public final class WebUtil {

    private static final Logger LOG = Logger.getLogger(WebUtil.class.getName());
    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private WebUtil() { }

    /** Null-safe trim. */
    public static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    public static boolean isValidEmail(String email) {
        return email.length() <= 150 && EMAIL.matcher(email).matches();
    }

    public static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** One-time message shown on the next page (type: success, danger, warning, info). */
    public static void flash(HttpSession session, String type, String message) {
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }

    public static User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    /** Sends the user to the home page that matches the role. */
    public static void redirectHome(HttpServletRequest req, HttpServletResponse resp, User user) throws IOException {
        String target = "ADMIN".equals(user.getRole()) ? "/admin/dashboard" : "/student/dashboard";
        resp.sendRedirect(req.getContextPath() + target);
    }

    /** Logs the real exception on the server and shows a friendly error page. */
    public static void showError(HttpServletRequest req, HttpServletResponse resp, Exception e)
            throws ServletException, IOException {
        LOG.log(Level.SEVERE, "Request failed: " + req.getRequestURI(), e);
        req.setAttribute("errorMessage", "Something went wrong while processing your request. Please try again.");
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        req.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(req, resp);
    }
}
