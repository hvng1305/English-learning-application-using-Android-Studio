package com.example.appeng;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GrammarDetailActivity extends AppCompatActivity {

    private TextView contentTextView;
    private FirebaseFirestore db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_detail);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Liên kết UI với mã
        TextView titleTextView = findViewById(R.id.titleTextView);
        contentTextView = findViewById(R.id.contentTextView);

        // Thêm chức năng cuộn cho TextView (dành cho nội dung dài)
        contentTextView.setMovementMethod(new ScrollingMovementMethod());

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();

        // Khởi tạo ExecutorService để xử lý bất đồng bộ
        executorService = Executors.newSingleThreadExecutor();

        // Nhận dữ liệu từ Intent
        String parentTitle = getIntent().getStringExtra("parentTitle"); // "Danh Từ"
        String childTitle = getIntent().getStringExtra("childTitle");   // "Danh Từ Là Gì"

        // Hiển thị tên mục con
        if (childTitle != null) {
            titleTextView.setText(childTitle);
        } else {
            titleTextView.setText("Tiêu đề không xác định");
        }

        // Lấy nội dung từ Firestore
        if (parentTitle != null && childTitle != null) {
            // Tải nội dung Firestore trong ExecutorService
            loadContent(parentTitle, childTitle);
        } else {
            contentTextView.setText("Không thể tải nội dung do lỗi dữ liệu.");
        }
    }

    private void loadContent(String parentTitle, String childTitle) {
        executorService.submit(() -> {
            // Truy cập tài liệu trong Firestore
            DocumentReference docRef = db.collection("Grammar").document(parentTitle)
                    .collection("Topics").document(childTitle);

            final StringBuilder displayContent = new StringBuilder();

            // Đặt callback bất đồng bộ để nhận kết quả từ Firestore
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Lấy các trường nội dung
                    String content = documentSnapshot.getString("content");
                    String noidung = documentSnapshot.getString("noidung");
                    String noidung1 = documentSnapshot.getString("noidung1");
                    String noidung2 = documentSnapshot.getString("noidung2");
                    String noidung3 = documentSnapshot.getString("noidung3");

                    // Ghép nội dung hiển thị
                    if (content != null && !content.isEmpty()) {
                        displayContent.append(content).append("\n\n");
                    }
                    if (noidung != null && !noidung.isEmpty()) {
                        displayContent.append(noidung).append("\n\n");
                    }
                    if (noidung1 != null && !noidung1.isEmpty()) {
                        displayContent.append(noidung1).append("\n\n");
                    }
                    if (noidung2 != null && !noidung2.isEmpty()) {
                        displayContent.append(noidung2).append("\n\n");
                    }
                    if (noidung3 != null && !noidung3.isEmpty()) {
                        displayContent.append(noidung3).append("\n\n");
                    }

                    // Cập nhật UI sau khi tải dữ liệu xong
                    runOnUiThread(() -> contentTextView.setText(displayContent.toString()));
                } else {
                    runOnUiThread(() -> contentTextView.setText("Không tìm thấy nội dung cho tiêu đề này."));
                }
            }).addOnFailureListener(e -> {
                // Nếu có lỗi, trả về thông báo lỗi
                runOnUiThread(() -> contentTextView.setText("Lỗi khi tải nội dung: " + e.getMessage()));
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng ExecutorService khi Activity bị hủy
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
