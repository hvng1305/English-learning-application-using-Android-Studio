package com.example.appeng;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import com.example.appeng.databinding.ActivityLearnFragmentBinding;

public class LearnFragment extends Fragment {

    private ActivityLearnFragmentBinding binding;
    private List<QuizModel> quizModelList;
    private QuizListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho Fragment này
        binding = ActivityLearnFragmentBinding.inflate(inflater, container, false);

        // Khởi tạo danh sách và lấy dữ liệu
        quizModelList = new ArrayList<>();
        getDataFromFirebase();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        if (binding != null) { // Kiểm tra binding trước khi sử dụng
            binding.progressBar.setVisibility(View.GONE);
            adapter = new QuizListAdapter(quizModelList);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerView.setAdapter(adapter);
        }
    }

    private void getDataFromFirebase() {
        if (binding != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }

        // Truy vấn từ key "0" đến key "4"
        FirebaseDatabase.getInstance().getReference()
                .orderByKey()
                .startAt("0")
                .endAt("4")
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    quizModelList.clear(); // Xóa danh sách cũ để không bị trùng lặp
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            QuizModel quizModel = snapshot.getValue(QuizModel.class);
                            if (quizModel != null) {
                                quizModelList.add(quizModel);
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
