package com.quiz.filter;

import com.quiz.model.User;
import com.quiz.util.WebUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * One filter runs before every request and does three jobs:
 *  1. Access control: /student/* needs a logged-in STUDENT, /admin/* needs a logged-in ADMIN.
 *  2. CSRF protection: every POST must carry the secret token stored in the session.
 *  3. Safe headers: protected pages are never cached, so "Back" after logout shows nothing.
 */
@WebFilter("/*")
public class SecurityFilter implements Filter {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();

        // Static files need no checks.
        if (path.startsWith("/css/") || path.equals("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }

        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("X-Frame-Options", "DENY");

        // ---- CSRF token (created once per session, exposed to JSPs as ${csrfToken}) ----
        HttpSession session = req.getSession(true);
        String token = (String) session.getAttribute("csrfToken");
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute("csrfToken", token);
        }
        req.setAttribute("csrfToken", token);

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String sent = req.getParameter("csrfToken");
            boolean valid = sent != null && MessageDigest.isEqual(
                    token.getBytes(StandardCharsets.UTF_8), sent.getBytes(StandardCharsets.UTF_8));
            if (!valid) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        // ---- Role-based access control ----
        boolean adminArea = path.startsWith("/admin/");
        boolean studentArea = path.startsWith("/student/");
        if (adminArea || studentArea) {
            resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            resp.setHeader("Pragma", "no-cache");
            resp.setDateHeader("Expires", 0);

            User user = (User) session.getAttribute("user");
            if (user == null) {
                WebUtil.flash(session, "warning", "Please log in to continue.");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            if (adminArea && !"ADMIN".equals(user.getRole())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            if (studentArea && !"STUDENT".equals(user.getRole())) {
                // an admin who opens a student URL is simply sent to the admin home
                WebUtil.redirectHome(req, resp, user);
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
