package com.quiz.servlet;

import com.quiz.dao.QuestionDAO;
import com.quiz.dao.QuizDAO;
import com.quiz.model.Question;
import com.quiz.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Admin question management: list, add, edit and delete. All changes use POST. */
@WebServlet({"/admin/questions", "/admin/questions/add", "/admin/questions/edit", "/admin/questions/delete"})
public class QuestionServlet extends HttpServlet {

    private static final String FORM_VIEW = "/WEB-INF/views/add-question.jsp";

    private final QuestionDAO questionDao = new QuestionDAO();
    private final QuizDAO quizDao = new QuizDAO();

    // ------------------------------------------------------------ GET
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ctx = req.getContextPath();
        try {
            switch (req.getServletPath()) {
                case "/admin/questions/add": {
                    Question blank = new Question();
                    blank.setQuizId(WebUtil.parseInt(req.getParameter("quizId"), 0));
                    blank.setCorrectAnswer("A");
                    showForm(req, resp, blank, null);
                    break;
                }
                case "/admin/questions/edit": {
                    Question q = questionDao.findById(WebUtil.parseInt(req.getParameter("id"), 0));
                    if (q == null) {
                        WebUtil.flash(req.getSession(), "danger", "Question not found.");
                        resp.sendRedirect(ctx + "/admin/questions");
                        return;
                    }
                    showForm(req, resp, q, null);
                    break;
                }
                case "/admin/questions/delete":
                    resp.sendRedirect(ctx + "/admin/questions"); // deleting is POST only
                    break;
                default: {
                    int quizId = WebUtil.parseInt(req.getParameter("quizId"), 0);
                    req.setAttribute("questions", questionDao.findAll(quizId));
                    req.setAttribute("quizzes", quizDao.findAll());
                    req.setAttribute("selectedQuizId", quizId);
                    req.getRequestDispatcher("/WEB-INF/views/view-questions.jsp").forward(req, resp);
                }
            }
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
        }
    }

    // ------------------------------------------------------------ POST
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            switch (req.getServletPath()) {
                case "/admin/questions/add":
                    save(req, resp, false);
                    break;
                case "/admin/questions/edit":
                    save(req, resp, true);
                    break;
                case "/admin/questions/delete":
                    delete(req, resp);
                    break;
                default:
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            WebUtil.showError(req, resp, e);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, boolean editing)
            throws SQLException, ServletException, IOException {
        Question q = new Question();
        q.setId(editing ? WebUtil.parseInt(req.getParameter("id"), 0) : 0);
        q.setQuizId(WebUtil.parseInt(req.getParameter("quizId"), 0));
        q.setQuestionText(WebUtil.trim(req.getParameter("questionText")));
        q.setOptionA(WebUtil.trim(req.getParameter("optionA")));
        q.setOptionB(WebUtil.trim(req.getParameter("optionB")));
        q.setOptionC(WebUtil.trim(req.getParameter("optionC")));
        q.setOptionD(WebUtil.trim(req.getParameter("optionD")));
        q.setCorrectAnswer(WebUtil.trim(req.getParameter("correctAnswer")).toUpperCase());

        List<String> errors = validate(q);
        if (errors.isEmpty() && quizDao.findById(q.getQuizId()) == null) {
            errors.add("Please choose a valid quiz.");
        }
        if (!errors.isEmpty()) {
            showForm(req, resp, q, errors); // keep what the admin typed
            return;
        }

        String ctx = req.getContextPath();
        if (editing) {
            if (!questionDao.update(q)) {
                WebUtil.flash(req.getSession(), "danger", "Question not found.");
                resp.sendRedirect(ctx + "/admin/questions");
                return;
            }
            WebUtil.flash(req.getSession(), "success", "Question updated.");
            resp.sendRedirect(ctx + "/admin/questions?quizId=" + q.getQuizId());
        } else {
            questionDao.add(q);
            WebUtil.flash(req.getSession(), "success", "Question added. You can add another one.");
            resp.sendRedirect(ctx + "/admin/questions/add?quizId=" + q.getQuizId());
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws SQLException, IOException {
        int id = WebUtil.parseInt(req.getParameter("id"), 0);
        int quizId = WebUtil.parseInt(req.getParameter("quizId"), 0);
        boolean deleted = questionDao.delete(id);
        WebUtil.flash(req.getSession(), deleted ? "success" : "danger",
                deleted ? "Question deleted." : "Question not found.");
        resp.sendRedirect(req.getContextPath() + "/admin/questions" + (quizId > 0 ? "?quizId=" + quizId : ""));
    }

    private List<String> validate(Question q) {
        List<String> errors = new ArrayList<>();
        if (q.getQuizId() <= 0) {
            errors.add("Please choose a quiz.");
        }
        if (q.getQuestionText().isEmpty() || q.getQuestionText().length() > 1000) {
            errors.add("Question text is required (max 1000 characters).");
        }
        String[] options = {q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD()};
        for (String option : options) {
            if (option.isEmpty() || option.length() > 255) {
                errors.add("All four options are required (max 255 characters each).");
                break;
            }
        }
        if (!q.getCorrectAnswer().matches("[ABCD]")) {
            errors.add("Correct answer must be A, B, C or D.");
        }
        return errors;
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Question q, List<String> errors)
            throws SQLException, ServletException, IOException {
        req.setAttribute("question", q);
        req.setAttribute("errors", errors);
        req.setAttribute("quizzes", quizDao.findAll());
        req.getRequestDispatcher(FORM_VIEW).forward(req, resp);
    }
}
