package com.example.appeng;

import java.util.ArrayList;
import java.util.List;

public class QuizModel {
    private String id;
    private String title;
    private String subtitle;
    private String time;
    private List<QuestionModel> questionList;
    private boolean completed;
    // Constructor đầy đủ tham số
    public QuizModel(String id, String title, String subtitle, String time, List<QuestionModel> questionList) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.questionList = questionList;
    }

    // Constructor mặc định
    public QuizModel() {
        this("", "", "", "", null);
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<QuestionModel> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuestionModel> questionList) {
        this.questionList = questionList;
    }

    /**
     * Lấy danh sách các câu hỏi mà người dùng trả lời sai.
     *
     * @return Danh sách các câu hỏi trả lời sai.
     */
    public List<QuestionModel> getWrongAnswers() {
        List<QuestionModel> wrongAnswers = new ArrayList<>();
        if (questionList != null) {
            for (QuestionModel question : questionList) {
                if (question.getUserAnswer() != null && !question.getUserAnswer().equals(question.getCorrect())) {
                    wrongAnswers.add(question);
                }
            }
        }
        return wrongAnswers;
    }

    /**
     * Lấy số lượng câu trả lời đúng.
     *
     * @return Số lượng câu đúng.
     */
    public int getCorrectAnswerCount() {
        int correctCount = 0;
        if (questionList != null) {
            for (QuestionModel question : questionList) {
                if (question.getUserAnswer() != null && question.getUserAnswer().equals(question.getCorrect())) {
                    correctCount++;
                }
            }
        }
        return correctCount;
    }
    // Getter và Setter cho trạng thái hoàn thành
    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
