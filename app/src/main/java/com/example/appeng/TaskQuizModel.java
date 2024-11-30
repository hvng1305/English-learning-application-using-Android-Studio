package com.example.appeng;

import java.util.List;

public class TaskQuizModel {
    private String id;
    private String title;
    private String subtitle;
    private String time;
    private List<TaskQuesModel> questionList;

    // Constructor mặc định không tham số (yêu cầu Firebase)
    public TaskQuizModel() {
        // Firebase sẽ sử dụng constructor này để tạo đối tượng
    }

    // Constructor có tham số (được bạn tạo ra)
    public TaskQuizModel(String id, String title, String subtitle, String time, List<TaskQuesModel> questionList) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.questionList = questionList;
    }

    // Getter và Setter
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

    public List<TaskQuesModel> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<TaskQuesModel> questionList) {
        this.questionList = questionList;
    }
}
