package com.example.appeng;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class UserFragment extends Fragment {

    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_user_fragment, container, false);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Bind UI elements
        TextView tvWelcomeMessage = view.findViewById(R.id.tvWelcomeMessage);
        TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);
        Button btnPersonal = view.findViewById(R.id.btnPersonal);
        Button btnLogout = view.findViewById(R.id.btnLogout);
        Switch switchNotifications = view.findViewById(R.id.switchNotifications);

        // Display user information
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName == null || displayName.isEmpty()) {
                displayName = getString(R.string.default_user_name); // Sử dụng chuỗi tài nguyên
            }
            tvWelcomeMessage.setText(getString(R.string.welcome_message, displayName));
            tvUserEmail.setText(getString(R.string.user_email, currentUser.getEmail()));
        }

        // Load notification setting from SharedPreferences
        SharedPreferences sharedPreferences = getActivity() != null ?
                getActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE) : null;

        if (sharedPreferences != null) {
            boolean isNotificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true);
            switchNotifications.setChecked(isNotificationsEnabled);

            // Handle notification switch toggle
            switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("notifications_enabled", isChecked);
                editor.apply();

                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic("notifications")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("FCM", "Subscribed to notifications topic");
                                } else {
                                    Log.e("FCM", "Failed to subscribe to notifications topic", task.getException());
                                }
                            });
                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("notifications")
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Log.d("FCM", "Unsubscribed from notifications topic");
                                } else {
                                    Log.e("FCM", "Failed to unsubscribe from notifications topic", task.getException());
                                }
                            });
                }
            });
        }

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
            showCustomToast(R.string.logout_success_message, R.drawable.asuccessicon);
        });

        return view;
    }

    // Method to show Custom Toast
    private void showCustomToast(int messageResId, int imageResId) {
        if (getActivity() == null) return;

        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.custom_toast,
                getActivity().findViewById(R.id.toast_root));
        if (layout == null) return;

        TextView toastText = layout.findViewById(R.id.toast_text);
        toastText.setText(getString(messageResId));

        Toast toast = new Toast(getActivity().getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
