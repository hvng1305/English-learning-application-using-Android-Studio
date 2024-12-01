package com.example.appeng;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appeng.databinding.ActivityQuizBinding;
import com.example.appeng.databinding.ScoreDialogBinding;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    public static List<QuestionModel> questionModelList;

    private ActivityQuizBinding binding;
    private int currentQuestionIndex = 0;
    private String selectedAnswer = "";
    private int score = 0;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Xử lý sự kiện quay lại
        binding.toolbar.setNavigationOnClickListener(v -> navigateToLearnFragment());

        // Ánh xạ các nút
        binding.btn0.setOnClickListener(this);
        binding.btn1.setOnClickListener(this);
        binding.btn2.setOnClickListener(this);
        binding.btn3.setOnClickListener(this);
        binding.nextBtn.setOnClickListener(this);

        loadQuestionsAsync();
    }

    private void navigateToLearnFragment() {
        finish(); // Kết thúc Activity hiện tại
    }

    private void loadQuestionsAsync() {
        executorService.execute(() -> {
            // Mô phỏng tải câu hỏi hoặc dữ liệu khác nếu cần
            runOnUiThread(this::loadQuestions);
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadQuestions() {
        selectedAnswer = "";
        resetButtonColors(); // Đặt lại màu sắc cho các nút

        if (currentQuestionIndex == questionModelList.size()) {
            finishQuiz();
            return;
        }

        binding.questionIndicatorTextview.setText(
                "Question " + (currentQuestionIndex + 1) + "/ " + questionModelList.size()
        );
        binding.questionProgressIndicator.setProgress(
                (int) ((float) currentQuestionIndex / questionModelList.size() * 100)
        );

        QuestionModel currentQuestion = questionModelList.get(currentQuestionIndex);
        binding.questionTextview.setText(currentQuestion.getQuestion());
        binding.btn0.setText(currentQuestion.getOptions().get(0));
        binding.btn1.setText(currentQuestion.getOptions().get(1));
        binding.btn2.setText(currentQuestion.getOptions().get(2));
        binding.btn3.setText(currentQuestion.getOptions().get(3));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.next_btn) {
            if (selectedAnswer.isEmpty()) {
                showCustomToast("Vui lòng chọn câu trả lời trước khi tiếp tục", R.drawable.aiconwarning);
                return;
            }

            QuestionModel currentQuestion = questionModelList.get(currentQuestionIndex);
            if (selectedAnswer.equals(currentQuestion.getCorrect())) {
                score++;
            } else {
                currentQuestion.setUserAnswer(selectedAnswer); // Lưu câu trả lời sai
            }
            currentQuestionIndex++;
            loadQuestionsAsync();
        } else {
            resetButtonColors(); // Reset màu nút khác trước khi đổi màu
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

    @SuppressLint("SetTextI18n")
    private void finishQuiz() {
        int totalQuestions = questionModelList.size();
        int percentage = (int) ((float) score / totalQuestions * 100);

        ScoreDialogBinding dialogBinding = ScoreDialogBinding.inflate(getLayoutInflater());
        dialogBinding.scoreProgressIndicator.setProgress(percentage);
        dialogBinding.scoreProgressText.setText(percentage + " %");

        if (percentage > 60) {
            dialogBinding.scoreTitle.setText("Tuyệt quá! Bạn đã học được thêm nhiều từ mới!");
            dialogBinding.scoreTitle.setTextColor(Color.BLUE);
            showCustomToast("Chúc mừng bạn đã hoàn thành bài học!", R.drawable.asuccessicon);
        } else {
            dialogBinding.scoreTitle.setText("Ôi không! Hãy cố gắng hơn lần sau!");
            dialogBinding.scoreTitle.setTextColor(Color.RED);
            showCustomToast("Hãy luyện tập thêm nhé!", R.drawable.aiconwarning);
        }

        dialogBinding.scoreSubtitle.setText(score + " trên " + totalQuestions + " câu trả lời đúng");

        StringBuilder wrongAnswers = new StringBuilder();
        for (QuestionModel question : questionModelList) {
            if (question.getUserAnswer() != null) {
                wrongAnswers.append("Q: ").append(question.getQuestion()).append("\n")
                        .append("Câu trả lời của bạn: ").append(question.getUserAnswer()).append("\n")
                        .append("Câu trả lời đúng: ").append(question.getCorrect()).append("\n\n");
            }
        }
        dialogBinding.wrongAnswersText.setText(wrongAnswers.toString());

        dialogBinding.finishBtn.setOnClickListener(v -> finish());

        new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .setCancelable(false)
                .show();
    }

    private void showCustomToast(String message, int imageResId) {
        View layout = LayoutInflater.from(this).inflate(R.layout.custom_toast, findViewById(R.id.toast_root));
        ImageView toastImage = layout.findViewById(R.id.toast_image);
        TextView toastText = layout.findViewById(R.id.toast_text);

        toastImage.setImageResource(imageResId);
        toastText.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Đóng ExecutorService khi Activity bị hủy
    }
}
