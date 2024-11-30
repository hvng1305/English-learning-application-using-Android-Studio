package com.example.appeng;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.example.appeng.databinding.ActivityTaskFragmentBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskFragment extends Fragment {

    private ActivityTaskFragmentBinding binding;
    private List<TaskQuizModel> quizModelList;
    private TaskListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho Fragment này
        binding = ActivityTaskFragmentBinding.inflate(inflater, container, false);

        // Khởi tạo danh sách và lấy dữ liệu
        quizModelList = new ArrayList<>();
        getDataFromFirebase();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        if (binding != null) { // Kiểm tra binding trước khi sử dụng
            binding.progressBar.setVisibility(View.GONE);
            adapter = new TaskListAdapter(quizModelList);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerView.setAdapter(adapter);
        }
    }

    private void getDataFromFirebase() {
        if (binding != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        // Truy vấn từ key "5" đến key "7"
        FirebaseDatabase.getInstance().getReference()
                .orderByKey()
                .startAt("5")
                .endAt("7")
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    quizModelList.clear(); // Xóa danh sách cũ để không bị trùng lặp
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            // Sử dụng TaskQuizModel thay vì QuizModel
                            TaskQuizModel taskQuizModel = snapshot.getValue(TaskQuizModel.class);
                            if (taskQuizModel != null) {
                                quizModelList.add(taskQuizModel);  // Thêm TaskQuizModel vào danh sách
                            }
                        }
                    }
                    if (binding != null) {
                        setupRecyclerView();
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding != null) {
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
