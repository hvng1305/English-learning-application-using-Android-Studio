package com.example.appeng;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Liên kết với BottomNavigationView
        nav = findViewById(R.id.nav);

        // Đặt fragment mặc định là HomeFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        // Sử dụng lambda thay cho lớp ẩn danh cho OnItemSelectedListener
        nav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            // Sử dụng if-else để kiểm tra item được chọn
            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.learn) {
                selectedFragment = new LearnFragment();
            } else if (item.getItemId() == R.id.game) {
                selectedFragment = new GameFragment();
            } else if (item.getItemId() == R.id.task) {
                selectedFragment = new TaskFragment();
            } else if (item.getItemId() == R.id.user) {
                selectedFragment = new UserFragment();
            }

            // Nếu fragment được chọn khác null, thay đổi fragment hiện tại
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            // Trả về true để đánh dấu rằng sự kiện đã được xử lý
            return true;
        });
    }
}
