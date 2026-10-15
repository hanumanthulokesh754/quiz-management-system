package com.quiz.dao;

import com.quiz.model.Question;
import com.quiz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** JDBC operations for the questions table. */
public class QuestionDAO {

    private static final String COLUMNS =
            "q.id, q.quiz_id, q.question_text, q.option_a, q.option_b, q.option_c, q.option_d, q.correct_answer";

    /**
     * Questions of one quiz.
     * @param includeAnswers false for students: the correct answer is never loaded into the object,
     *                       so it can never leak to the browser. true only for scoring and admin pages.
     */
    public List<Question> findByQuizId(int quizId, boolean includeAnswers) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM questions q WHERE q.quiz_id = ? ORDER BY q.id";
        List<Question> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs, includeAnswers));
                }
            }
        }
        return list;
    }

    /** Admin list. quizId = 0 returns the questions of every quiz. */
    public List<Question> findAll(int quizId) throws SQLException {
        String sql = "SELECT " + COLUMNS + ", z.quiz_name FROM questions q "
                + "JOIN quizzes z ON z.id = q.quiz_id "
                + "WHERE (? = 0 OR q.quiz_id = ?) ORDER BY q.quiz_id, q.id";
        List<Question> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            ps.setInt(2, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question q = map(rs, true);
                    q.setQuizName(rs.getString("quiz_name"));
                    list.add(q);
                }
            }
        }
        return list;
    }

    public Question findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM questions q WHERE q.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs, true) : null;
            }
        }
    }

    public void add(Question q) throws SQLException {
        String sql = "INSERT INTO questions (quiz_id, question_text, option_a, option_b, option_c, option_d, correct_answer) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, q.getQuizId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, q.getCorrectAnswer());
            ps.executeUpdate();
        }
    }

    /** @return true if a row was updated */
    public boolean update(Question q) throws SQLException {
        String sql = "UPDATE questions SET quiz_id = ?, question_text = ?, option_a = ?, option_b = ?, "
                + "option_c = ?, option_d = ?, correct_answer = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, q.getQuizId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getOptionA());
            ps.setString(4, q.getOptionB());
            ps.setString(5, q.getOptionC());
            ps.setString(6, q.getOptionD());
            ps.setString(7, q.getCorrectAnswer());
            ps.setInt(8, q.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** @return true if a row was deleted */
    public boolean delete(int id) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM questions WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public int count() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM questions");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Question map(ResultSet rs, boolean includeAnswer) throws SQLException {
        Question q = new Question();
        q.setId(rs.getInt("id"));
        q.setQuizId(rs.getInt("quiz_id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setOptionA(rs.getString("option_a"));
        q.setOptionB(rs.getString("option_b"));
        q.setOptionC(rs.getString("option_c"));
        q.setOptionD(rs.getString("option_d"));
        if (includeAnswer) {
            q.setCorrectAnswer(rs.getString("correct_answer"));
        }
        return q;
    }
}
