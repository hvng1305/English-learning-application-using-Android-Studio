package com.example.appeng;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private FirebaseAuth auth;
    TextView forgotPassword;

    private ExecutorService executorService; // ExecutorService

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Các view từ layout
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        Button loginButton = findViewById(R.id.login_button);
        TextView signupRedirectText = findViewById(R.id.signUpRedirectText);
        forgotPassword = findViewById(R.id.forgot_password);

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Khởi tạo ExecutorService với 1 thread
        executorService = Executors.newSingleThreadExecutor();

        // Xử lý sự kiện nhấn nút đăng nhập
        loginButton.setOnClickListener(v -> {
            String email = loginEmail.getText().toString();
            String pass = loginPassword.getText().toString();

            // Kiểm tra email và mật khẩu có hợp lệ không
            if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                if (!pass.isEmpty()) {
                    executorService.submit(() -> auth.signInWithEmailAndPassword(email, pass)
                            .addOnSuccessListener(authResult -> runOnUiThread(() -> {
                                showCustomToast("Đăng nhập thành công", R.drawable.asuccessicon);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }))
                            .addOnFailureListener(e -> runOnUiThread(() ->
                                    showCustomToast("Đăng nhập thất bại", R.drawable.aerroricon)
                            )));
                } else {
                    loginPassword.setError("Không được để trống");
                }
            } else if (email.isEmpty()) {
                loginEmail.setError("Không được để trống");
            } else {
                loginEmail.setError("Vui lòng nhập email đúng định dạng");
            }
        });

        // Chuyển đến trang đăng ký khi nhấn vào text "Đăng ký"
        signupRedirectText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class))
        );

        // Xử lý sự kiện nhấn vào text "Quên mật khẩu"
        forgotPassword.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
            EditText emailBox = dialogView.findViewById(R.id.emailBox);
            builder.setView(dialogView);
            AlertDialog dialog = builder.create();
            // Xử lý nút "Đặt lại mật khẩu"
            dialogView.findViewById(R.id.btnReset).setOnClickListener(v -> {
                String userEmail = emailBox.getText().toString();
                if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    showCustomToast("Vui lòng nhập email đã đăng ký", R.drawable.aiconwarning);
                    return;
                }

                // Gửi email đặt lại mật khẩu
                executorService.submit(() -> auth.sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(task -> runOnUiThread(() -> {
                            if (task.isSuccessful()) {
                                showCustomToast("Kiểm tra email của bạn", R.drawable.asuccessicon);
                                dialog.dismiss();
                            } else {
                                showCustomToast("Gửi không thành công", R.drawable.aerroricon);
                            }
                        })));
            });

            // Xử lý nút "Hủy"
            dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());

            // Thiết lập nền trong suốt cho dialog
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            dialog.show();
        });
    }

    // Hàm tạo Custom Toast
    private void showCustomToast(String message, int imageResId) {
        // Inflate layout custom_toast.xml
        View layout = getLayoutInflater().inflate(R.layout.custom_toast, findViewById(R.id.toast_root));

        // Thiết lập nội dung
        ImageView toastImage = layout.findViewById(R.id.toast_image);
        toastImage.setImageResource(imageResId);

        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setText(message);

        // Tạo Toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Dừng ExecutorService khi Activity bị hủy
        executorService.shutdown();
    }
}
