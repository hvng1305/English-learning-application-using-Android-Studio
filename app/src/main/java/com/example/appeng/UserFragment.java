package com.example.appeng;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class UserFragment extends Fragment {

    private TextView tvWelcomeMessage, tvUserEmail;
    private Button btnPersonal, btnLogout;
    private Switch switchNotifications;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_fragment, container, false);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        sharedPreferences = getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        // Bind UI elements
        tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnPersonal = view.findViewById(R.id.btnPersonal);
        btnLogout = view.findViewById(R.id.btnLogout);
        switchNotifications = view.findViewById(R.id.switchNotifications);

        // Display user information
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = "User";  // Default name if display name is not available
            }
            tvWelcomeMessage.setText("Welcome, " + displayName);
            tvUserEmail.setText("Email: " + currentUser.getEmail());
        }

        // Load notification setting from SharedPreferences
        boolean isNotificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
        switchNotifications.setChecked(isNotificationsEnabled);

        // Handle notification switch toggle
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notifications_enabled", isChecked);
            editor.apply();

            // Subscribe or unsubscribe from the notifications topic in FCM
            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("notifications")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("FCM", "Chủ đề đã đăng ký nhận thông báo");
                            } else {
                                Log.e("FCM", "Không thể đăng ký chủ đề thông báo", task.getException());
                            }
                        });
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("notifications")
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("FCM", "Đã hủy đăng ký chủ đề thông báo");
                            } else {
                                Log.e("FCM", "Chủ đề không thể hủy đăng ký nhận thông báo", task.getException());
                            }
                        });
            }
        });

        // Handle Personal button click
        btnPersonal.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalActivity.class);
            startActivity(intent);
        });

        // Handle Logout button click
        btnLogout.setOnClickListener(v -> {
            firebaseAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Custom Toast for Logout
            showCustomToast("Bạn đã đăng xuất thành công!", R.drawable.asuccessicon);
        });

        return view;
    }

    // Method to show Custom Toast
    private void showCustomToast(String message, int imageResId) {
        // Inflate custom toast layout
        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.custom_toast, getActivity().findViewById(R.id.toast_root));
        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setText(message);

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
