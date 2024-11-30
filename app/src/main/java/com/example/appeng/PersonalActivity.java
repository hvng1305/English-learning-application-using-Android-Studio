package com.example.appeng;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class PersonalActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private EditText edtName;
    private Button btnSave, btnChooseImage;
    private Uri selectedImageUri;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        // Initialize Firebase components
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Bind UI elements
        imgProfile = findViewById(R.id.imgProfile);
        edtName = findViewById(R.id.edtName);
        btnSave = findViewById(R.id.btnSave);
        btnChooseImage = findViewById(R.id.btnChooseImage);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");

        // Load current user data (if available)
        loadUserData();

        // Choose Image button
        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 100);
        });

        // Save button
        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            if (TextUtils.isEmpty(newName) && selectedImageUri == null) {
                showCustomToast("No changes to save", R.drawable.aiconwarning);
            } else {
                saveUserData(newName);
            }
        });
    }

    private void loadUserData() {
        if (currentUser != null) {
            // Load current display name
            String displayName = currentUser.getDisplayName();
            if (!TextUtils.isEmpty(displayName)) {
                edtName.setText(displayName);
            }

            // Load profile image from Firebase (if any)
            String userId = currentUser.getUid();
            StorageReference profileImageRef = storageReference.child("profile_images/" + userId + ".jpg");
            profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // Load image using a library like Glide or Picasso
                Glide.with(this).load(uri).into(imgProfile);
            }).addOnFailureListener(e -> {
                // Handle failure (optional)
                imgProfile.setImageResource(R.drawable.user_2);
            });
        }
    }

    private void saveUserData(String newName) {
        progressDialog.show();

        // Update image if selected
        if (selectedImageUri != null) {
            uploadProfileImage(newName);
        } else {
            updateFirestore(newName, null);
        }
    }

    private void uploadProfileImage(String newName) {
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        StorageReference profileImageRef = storageReference.child("profile_images/" + userId + ".jpg");

        profileImageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    updateFirestore(newName, imageUrl);
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showCustomToast("Failed to upload image", R.drawable.aerroricon);
                });
    }

    private void updateFirestore(String newName, String imageUrl) {
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        Map<String, Object> updates = new HashMap<>();
        if (!TextUtils.isEmpty(newName)) updates.put("name", newName);
        if (imageUrl != null) updates.put("profileImage", imageUrl);

        firestore.collection("users").document(userId)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    showCustomToast("Profile updated successfully", R.drawable.asuccessicon);

                    // Update Firebase User profile if name changed
                    if (!TextUtils.isEmpty(newName)) {
                        currentUser.updateProfile(new UserProfileChangeRequest.Builder()
                                .setDisplayName(newName)
                                .build());
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showCustomToast("Failed to update profile", R.drawable.aerroricon);
                });
    }

    private void showCustomToast(String message, int imageResId) {
        View layout = LayoutInflater.from(this).inflate(R.layout.custom_toast, findViewById(R.id.toast_root));
        TextView toastText = layout.findViewById(R.id.toast_text);
        ImageView toastImage = layout.findViewById(R.id.toast_image);

        toastText.setText(message);
        toastImage.setImageResource(imageResId);

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfile.setImageURI(selectedImageUri);
        }
    }
}
