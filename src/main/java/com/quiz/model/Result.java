package com.quiz.model;

import java.sql.Timestamp;

/** Model for a row in the results table (plus names filled by joins). */
public class Result {
    private int id;
    private int userId;
    private int quizId;
    private int score;           // number of correct answers
    private int totalQuestions;
    private Timestamp createdAt;
    private String quizName;     // helper
    private String userName;     // helper
    private String userEmail;    // helper

    public Result() { }

    public Result(int userId, int quizId, int score, int totalQuestions) {
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.totalQuestions = totalQuestions;
    }

    /** Percentage score, 0-100. */
    public double getPercentage() {
        return totalQuestions == 0 ? 0 : (score * 100.0) / totalQuestions;
    }

    /** Wrong or unanswered questions. */
    public int getIncorrect() { return totalQuestions - score; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getQuizId() { return quizId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getQuizName() { return quizName; }
    public void setQuizName(String quizName) { this.quizName = quizName; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
