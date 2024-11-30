package com.example.appeng;

import java.util.List;

public class QuestionModel {
    private String question;
    private List<String> options;
    private String correct;
    private String userAnswer; // Thêm thuộc tính userAnswer

    // Constructor đầy đủ tham số
    public QuestionModel(String question, List<String> options, String correct) {
        this.question = question;
        this.options = options;
        this.correct = correct;
        this.userAnswer = null; // Khởi tạo giá trị mặc định
    }

    // Constructor mặc định
    public QuestionModel() {
        this("", null, "");
    }

    // Getters và Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    /**
     * Kiểm tra xem câu trả lời của người dùng có đúng không.
     *
     * @return true nếu câu trả lời đúng, false nếu sai hoặc chưa trả lời.
     */
    public boolean isAnswerCorrect() {
        return userAnswer != null && userAnswer.equals(correct);
    }
}
