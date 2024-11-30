package com.example.appeng;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NguPhapActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> parentList; // Danh sách mục cha
    private HashMap<String, List<String>> childMap; // Map mục cha -> danh sách mục con

    private FirebaseFirestore db;
    private ExecutorService executorService; // ExecutorService để chạy công việc nền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngu_phap);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        expandableListView = findViewById(R.id.expandableListView);
        parentList = new ArrayList<>();
        childMap = new HashMap<>();

        // Khởi tạo Firestore và ExecutorService
        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        // Bắt đầu tải dữ liệu ngữ pháp trong nền
        loadGrammarDataInBackground();

        // Xử lý sự kiện khi nhấn vào mục con
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String parentTitle = parentList.get(groupPosition); // Lấy tên mục cha
            String childTitle = childMap.get(parentTitle).get(childPosition); // Lấy tên mục con

            // Chuyển đến màn hình chi tiết
            Intent intent = new Intent(NguPhapActivity.this, GrammarDetailActivity.class);
            intent.putExtra("parentTitle", parentTitle);
            intent.putExtra("childTitle", childTitle);
            startActivity(intent);
            return true;
        });
    }

    // Sử dụng ExecutorService để tải dữ liệu Firestore trong nền
    private void loadGrammarDataInBackground() {
        executorService.submit(() -> {
            CollectionReference grammarRef = db.collection("Grammar");

            grammarRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String parentTitle = document.getId(); // Mục cha (VD: "1. Danh Từ")
                        parentList.add(parentTitle);

                        // Tải dữ liệu sub-collection "Topics"
                        document.getReference().collection("Topics").get()
                                .addOnCompleteListener(topicTask -> {
                                    if (topicTask.isSuccessful()) {
                                        List<String> childList = new ArrayList<>();
                                        for (QueryDocumentSnapshot topicDoc : topicTask.getResult()) {
                                            String childTitle = topicDoc.getId(); // Mục con (VD: "Danh Từ là gì?")
                                            childList.add(childTitle);
                                        }
                                        // Thêm dữ liệu con vào Map
                                        childMap.put(parentTitle, childList);

                                        // Kiểm tra nếu tất cả các mục cha đã được xử lý
                                        if (childMap.size() == parentList.size()) {
                                            // Cập nhật giao diện trong main thread
                                            runOnUiThread(() -> {
                                                adapter = new ExpandableListAdapter(NguPhapActivity.this, parentList, childMap);
                                                expandableListView.setAdapter(adapter);
                                            });
                                        }
                                    } else {
                                        Log.e("Firestore", "Lỗi khi lấy dữ liệu sub-collection!", topicTask.getException());
                                    }
                                });
                    }
                } else {
                    Log.e("Firestore", "Lỗi khi lấy dữ liệu!", task.getException());
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo rằng ExecutorService được đóng khi Activity bị hủy
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
