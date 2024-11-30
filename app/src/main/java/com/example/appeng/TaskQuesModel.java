package com.example.appeng;

import java.util.List;

public class TaskQuesModel {
    private String question;
    private List<String> options;
    private String correct;

    // Constructor mặc định không tham số (yêu cầu Firebase)
    public TaskQuesModel() {
        // Firebase sẽ sử dụng constructor này để tạo đối tượng
    }

    // Constructor có tham số (dùng khi tạo đối tượng thủ công)
    public TaskQuesModel(String question, List<String> options, String correct) {
        this.question = question;
        this.options = options;
        this.correct = correct;
    }

    // Getter và Setter
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
}
