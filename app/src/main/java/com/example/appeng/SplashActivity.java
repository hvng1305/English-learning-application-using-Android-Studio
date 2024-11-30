package com.example.appeng;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class SplashActivity extends AppCompatActivity {
    public static int SPLASH_TIMER = 3000; // Thời gian hiển thị Splash (3 giây)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nếu dùng SplashScreen API từ Android 12 trở lên
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        setContentView(R.layout.activity_splash); // Layout của màn hình Splash (Bạn cần tạo layout này)

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Sau khi hiển thị Splash xong, chuyển đến LoginActivity
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Đóng SplashActivity để không quay lại
            }
        }, SPLASH_TIMER); // Thời gian chờ 3 giây
    }
}
