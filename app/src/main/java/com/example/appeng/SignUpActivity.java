package com.example.appeng;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Ánh xạ các view từ layout
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        Button signupButton = findViewById(R.id.signup_button);
        TextView loginRedirectText = findViewById(R.id.loginRedirectText);

        // Xử lý sự kiện nhấn nút đăng ký
        signupButton.setOnClickListener(view -> {
            String user = signupEmail.getText().toString().trim();
            String pass = signupPassword.getText().toString().trim();

            // Kiểm tra trường email và mật khẩu
            if (user.isEmpty()) {
                signupEmail.setError("Email không được để trống");
            }
            if (pass.isEmpty()) {
                signupPassword.setError("Mật khẩu không được để trống");
            } else {
                // Đăng ký người dùng với Firebase Authentication
                auth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showCustomToast("Đăng ký thành công", R.drawable.asuccessicon);
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại";
                        showCustomToast("Đăng ký thất bại: " + errorMessage, R.drawable.aerroricon);
                    }
                });
            }
        });

        // Xử lý sự kiện nhấn vào "Đăng nhập" để quay lại trang đăng nhập
        loginRedirectText.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));
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
}
