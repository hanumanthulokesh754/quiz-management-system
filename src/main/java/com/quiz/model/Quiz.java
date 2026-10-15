package com.quiz.model;

/** Model for a row in the quizzes table (plus two helper fields filled by joins). */
public class Quiz {
    private int id;
    private String quizName;
    private String category;
    private int duration;        // minutes
    private int questionCount;   // helper: number of questions in this quiz
    private boolean attempted;   // helper: has the current student already taken it?

    public Quiz() { }

    public Quiz(String quizName, String category, int duration) {
        this.quizName = quizName;
        this.category = category;
        this.duration = duration;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getQuizName() { return quizName; }
    public void setQuizName(String quizName) { this.quizName = quizName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public int getQuestionCount() { return questionCount; }
    public void setQuestionCount(int questionCount) { this.questionCount = questionCount; }
    public boolean isAttempted() { return attempted; }
    public void setAttempted(boolean attempted) { this.attempted = attempted; }
}
