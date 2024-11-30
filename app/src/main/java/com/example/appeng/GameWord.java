package com.example.appeng;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GameWord extends AppCompatActivity {

    private LinearLayout chatBox;
    private ScrollView chatScrollView;
    private EditText etInputWord;
    private Button btnSubmit;

    private TextView tvPlayerScore, tvAiScore, tvTimer;

    private String currentWord = "start"; // Từ bắt đầu
    private final ArrayList<String> usedWords = new ArrayList<>(); // Danh sách các từ đã sử dụng
    private int playerScore = 0; // Điểm người chơi
    private int aiScore = 0; // Điểm của AI
    private CountDownTimer countDownTimer;

    private final int WINNING_SCORE = 40; // Điểm cần đạt để thắng
    private final int TIME_LIMIT = 15000; // 15 giây (giới hạn thời gian)

    private final List<String> aiWordList = Arrays.asList(
            "apple", "elephant", "tiger", "rabbit", "table", "eagle", "gold", "dream", "mountain",
            "night", "treasure", "echo", "ocean", "nectar", "rose", "egg", "game", "extra", "art"
    );

    // ExecutorService cho tác vụ nền
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_word);

        // Khởi tạo ExecutorService
        executorService = Executors.newFixedThreadPool(2); // Tối đa 2 thread

        // Liên kết giao diện
        chatBox = findViewById(R.id.chatBox);
        chatScrollView = findViewById(R.id.chatScrollView);
        etInputWord = findViewById(R.id.etInputWord);
        btnSubmit = findViewById(R.id.btnSubmit);

        tvPlayerScore = findViewById(R.id.tvPlayerScore);
        tvAiScore = findViewById(R.id.tvAiScore);
        tvTimer = findViewById(R.id.tvTimer);

        addChatMessage(getString(R.string.start_word, currentWord), true);

        usedWords.add(currentWord);

        btnSubmit.setOnClickListener(v -> {
            String inputWord = etInputWord.getText().toString().trim().toLowerCase();
            if (isValidWord(inputWord)) {
                int points = inputWord.length();
                playerScore += points;
                currentWord = inputWord;
                usedWords.add(inputWord);

                updateScores();
                addChatMessage(inputWord, false);

                if (playerScore >= WINNING_SCORE) {
                    stopGame();
                    showGameEndDialog(getString(R.string.player_win), true);
                    return;
                }

                startTimer();
                aiTurn(); // AI trả lời
            } else {
                addChatMessage(getString(R.string.invalid_word), true);
            }
            etInputWord.setText("");
        });

        startTimer();
    }

    private void aiTurn() {
        Callable<String> aiTask = this::findAiWord; // Nhiệm vụ nền tìm từ
        Future<String> aiFuture = executorService.submit(aiTask);

        executorService.execute(() -> {
            try {
                String aiWord = aiFuture.get(); // Lấy kết quả
                runOnUiThread(() -> processAiWord(aiWord));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void processAiWord(String aiWord) {
        if (aiWord != null) {
            int points = aiWord.length();
            aiScore += points;
            currentWord = aiWord;
            usedWords.add(aiWord);

            updateScores();
            addChatMessage(aiWord, true);

            if (aiScore >= WINNING_SCORE) {
                stopGame();
                showGameEndDialog(getString(R.string.ai_win), false);
                return;
            }
        } else {
            addChatMessage(getString(R.string.ai_no_word), true);
            stopGame();
            showGameEndDialog(getString(R.string.ai_no_word_win), true);
        }
    }

    private void updateScores() {
        tvPlayerScore.setText(getString(R.string.player_score, playerScore));
        tvAiScore.setText(getString(R.string.ai_score, aiScore));
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(TIME_LIMIT, 1000) {
            @SuppressLint("StringFormatMatches")
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(getString(R.string.time_remaining, millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                addChatMessage(getString(R.string.time_up), true);
                stopGame();
                showGameEndDialog(getString(R.string.time_up_loss), false);
            }
        }.start();
    }

    private void stopGame() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        etInputWord.setEnabled(false);
        btnSubmit.setEnabled(false);
    }

    private boolean isValidWord(String word) {
        if (word.isEmpty() || usedWords.contains(word)) {
            return false;
        }
        char lastChar = currentWord.charAt(currentWord.length() - 1);
        return word.charAt(0) == lastChar;
    }

    private String findAiWord() {
        char lastChar = currentWord.charAt(currentWord.length() - 1);
        for (String word : aiWordList) {
            if (word.charAt(0) == lastChar && !usedWords.contains(word)) {
                return word;
            }
        }
        return null;
    }

    private void addChatMessage(String message, boolean isLeftAligned) {
        View chatMessageView = LayoutInflater.from(this).inflate(
                isLeftAligned ? R.layout.chat_item_left : R.layout.chat_item_right, chatBox, false);
        TextView textView = chatMessageView.findViewById(R.id.chatMessage);
        textView.setText(message);
        chatBox.addView(chatMessageView);

        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void showGameEndDialog(String message, boolean isWin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isWin ? getString(R.string.congratulations) : getString(R.string.game_over));
        builder.setMessage(message);

        builder.setPositiveButton(getString(R.string.play_again), (dialog, which) -> {
            dialog.dismiss();
            restartGame();
        });

        builder.setNegativeButton(getString(R.string.exit), (dialog, which) -> {
            dialog.dismiss();
            finish(); // Kết thúc Activity
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void restartGame() {
        playerScore = 0;
        aiScore = 0;
        currentWord = "start";
        usedWords.clear();
        usedWords.add(currentWord);

        etInputWord.setEnabled(true);
        btnSubmit.setEnabled(true);

        updateScores();
        tvTimer.setText(getString(R.string.time_limit));
        chatBox.removeAllViews();

        addChatMessage(getString(R.string.start_word, currentWord), true);

        startTimer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown(); // Tắt ExecutorService khi Activity bị hủy
        }
    }
}
