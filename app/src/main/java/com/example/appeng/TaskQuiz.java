package com.example.appeng;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appeng.databinding.ActivityTaskQuizBinding;
import com.example.appeng.databinding.ScoreDialogBinding;

import java.util.List;
import java.util.Locale;

public class TaskQuiz extends AppCompatActivity implements View.OnClickListener {

    public static List<TaskQuesModel> questionModelList;
    public static String time = "5";  // Mặc định là 5 phút cho ví dụ

    private ActivityTaskQuizBinding binding;
    private int currentQuestionIndex = 0;
    private String selectedAnswer = "";
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Xử lý sự kiện quay lại
        binding.toolbar.setNavigationOnClickListener(v -> navigateToLearnFragment());

        // Thiết lập các sự kiện click
        binding.btn0.setOnClickListener(this);
        binding.btn1.setOnClickListener(this);
        binding.btn2.setOnClickListener(this);
        binding.btn3.setOnClickListener(this);
        binding.nextBtn.setOnClickListener(this);

        loadQuestions();
        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Quay lại fragment trước đó khi nhấn nút quay lại
    private void navigateToLearnFragment() {
        finish(); // Kết thúc Activity hiện tại
    }

    // Bắt đầu bộ đếm thời gian
    private void startTimer() {
        long totalTimeInMillis = Integer.parseInt(time) * 60 * 1000L;

        // Tạo CountDownTimer trên UI thread
        new CountDownTimer(totalTimeInMillis, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainingSeconds = seconds % 60;
                // Sử dụng String.format với Locale để tránh sử dụng mặc định locale
                binding.timerIndicatorTextview.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds));
            }

            @Override
            public void onFinish() {
                finishQuiz();
            }
        }.start();
    }

    // Tải câu hỏi từ danh sách
    private void loadQuestions() {
        selectedAnswer = "";
        if (currentQuestionIndex == questionModelList.size()) {
            finishQuiz();
            return;
        }

        // Lấy câu hỏi hiện tại và cập nhật giao diện
        TaskQuesModel currentQuestion = questionModelList.get(currentQuestionIndex);
        // Sử dụng tài nguyên chuỗi với tham số
        binding.questionIndicatorTextview.setText(getString(R.string.question_indicator, currentQuestionIndex + 1, questionModelList.size()));
        binding.questionProgressIndicator.setProgress((int) ((currentQuestionIndex / (float) questionModelList.size()) * 100));
        binding.questionTextview.setText(currentQuestion.getQuestion());
        binding.btn0.setText(currentQuestion.getOptions().get(0));
        binding.btn1.setText(currentQuestion.getOptions().get(1));
        binding.btn2.setText(currentQuestion.getOptions().get(2));
        binding.btn3.setText(currentQuestion.getOptions().get(3));
    }

    @Override
    public void onClick(View view) {
        resetButtonColors();
        if (view.getId() == R.id.next_btn) {
            if (selectedAnswer.isEmpty()) {
                Toast.makeText(this, getString(R.string.select_answer), Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedAnswer.equals(questionModelList.get(currentQuestionIndex).getCorrect())) {
                score++;
            }
            currentQuestionIndex++;
            loadQuestions();
        } else {
            Button clickedBtn = (Button) view;
            selectedAnswer = clickedBtn.getText().toString();
            clickedBtn.setBackgroundColor(getColor(R.color.my_primary));
        }
    }

    private void resetButtonColors() {
        binding.btn0.setBackgroundColor(getColor(R.color.grey));
        binding.btn1.setBackgroundColor(getColor(R.color.grey));
        binding.btn2.setBackgroundColor(getColor(R.color.grey));
        binding.btn3.setBackgroundColor(getColor(R.color.grey));
    }

    // Kết thúc quiz và hiển thị kết quả
    private void finishQuiz() {
        int totalQuestions = questionModelList.size();
        int percentage = (int) ((score / (float) totalQuestions) * 100);

        ScoreDialogBinding dialogBinding = ScoreDialogBinding.inflate(getLayoutInflater());
        dialogBinding.scoreProgressIndicator.setProgress(percentage);
        dialogBinding.scoreProgressText.setText(String.format(Locale.getDefault(), "%d%%", percentage));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogBinding.getRoot())
                .setCancelable(false)
                .setPositiveButton(getString(R.string.finish), (dialog, which) -> finish())
                .show();
    }
}
