package com.quiz.dao;

import com.quiz.model.Result;
import com.quiz.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** JDBC operations for the results table. */
public class ResultDAO {

    /**
     * Saves a result and returns its id.
     * Throws SQLIntegrityConstraintViolationException if the student already submitted this quiz
     * (the UNIQUE (user_id, quiz_id) key in the database).
     */
    public int save(Result r) throws SQLException {
        String sql = "INSERT INTO results (user_id, quiz_id, score, total_questions) VALUES (?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getQuizId());
            ps.setInt(3, r.getScore());
            ps.setInt(4, r.getTotalQuestions());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean hasAttempted(int userId, int quizId) throws SQLException {
        String sql = "SELECT 1 FROM results WHERE user_id = ? AND quiz_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** A student can only open their own result (the user_id check). */
    public Result findByIdAndUser(int id, int userId) throws SQLException {
        String sql = "SELECT r.id, r.user_id, r.quiz_id, r.score, r.total_questions, r.created_at, z.quiz_name "
                + "FROM results r JOIN quizzes z ON z.id = r.quiz_id WHERE r.id = ? AND r.user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Result> findByUser(int userId) throws SQLException {
        String sql = "SELECT r.id, r.user_id, r.quiz_id, r.score, r.total_questions, r.created_at, z.quiz_name "
                + "FROM results r JOIN quizzes z ON z.id = r.quiz_id "
                + "WHERE r.user_id = ? ORDER BY r.created_at DESC, r.id DESC";
        List<Result> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /** Every student's results (admin only). */
    public List<Result> findAll() throws SQLException {
        String sql = "SELECT r.id, r.user_id, r.quiz_id, r.score, r.total_questions, r.created_at, "
                + "z.quiz_name, u.name AS user_name, u.email AS user_email "
                + "FROM results r JOIN quizzes z ON z.id = r.quiz_id JOIN users u ON u.id = r.user_id "
                + "ORDER BY r.created_at DESC, r.id DESC";
        List<Result> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Result r = map(rs);
                r.setUserName(rs.getString("user_name"));
                r.setUserEmail(rs.getString("user_email"));
                list.add(r);
            }
        }
        return list;
    }

    public int count() throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM results");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private Result map(ResultSet rs) throws SQLException {
        Result r = new Result();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setQuizId(rs.getInt("quiz_id"));
        r.setScore(rs.getInt("score"));
        r.setTotalQuestions(rs.getInt("total_questions"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        r.setQuizName(rs.getString("quiz_name"));
        return r;
    }
}
