package com.quiz.dao;

import com.quiz.model.Quiz;
import com.quiz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** JDBC operations for the quizzes table. */
public class QuizDAO {

    /** All quizzes with their question counts (used by the admin pages). */
    public List<Quiz> findAll() throws SQLException {
        return query(0);
    }

    /** All quizzes, each flagged as attempted or not by the given student. */
    public List<Quiz> findAllForStudent(int userId) throws SQLException {
        return query(userId);
    }

    private List<Quiz> query(int userId) throws SQLException {
        String sql = "SELECT q.id, q.quiz_name, q.category, q.duration, "
                + "(SELECT COUNT(*) FROM questions qs WHERE qs.quiz_id = q.id) AS question_count, "
                + "(SELECT COUNT(*) FROM results r WHERE r.quiz_id = q.id AND r.user_id = ?) AS attempts "
                + "FROM quizzes q ORDER BY q.id";
        List<Quiz> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Quiz q = new Quiz();
                    q.setId(rs.getInt("id"));
                    q.setQuizName(rs.getString("quiz_name"));
                    q.setCategory(rs.getString("category"));
                    q.setDuration(rs.getInt("duration"));
                    q.setQuestionCount(rs.getInt("question_count"));
                    q.setAttempted(rs.getInt("attempts") > 0);
                    list.add(q);
                }
            }
        }
        return list;
    }

    public Quiz findById(int id) throws SQLException {
        String sql = "SELECT id, quiz_name, category, duration FROM quizzes WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Quiz q = new Quiz();
                q.setId(rs.getInt("id"));
                q.setQuizName(rs.getString("quiz_name"));
                q.setCategory(rs.getString("category"));
                q.setDuration(rs.getInt("duration"));
                return q;
            }
        }
    }

    public int create(Quiz quiz) throws SQLException {
        String sql = "INSERT INTO quizzes (quiz_name, category, duration) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quiz.getQuizName());
            ps.setString(2, quiz.getCategory());
            ps.setInt(3, quiz.getDuration());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public int count() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM quizzes");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}
