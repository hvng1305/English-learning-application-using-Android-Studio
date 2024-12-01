package com.example.appeng;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appeng.databinding.QuizItemRecyclerRowBinding;

import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.MyViewHolder> {

    private final List<QuizModel> quizModelList;

    // Constructor
    public QuizListAdapter(List<QuizModel> quizModelList) {
        this.quizModelList = quizModelList;
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final QuizItemRecyclerRowBinding binding;

        public MyViewHolder(@NonNull QuizItemRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(QuizModel model) {
            binding.quizTitleText.setText(model.getTitle());
            binding.quizSubtitleText.setText(model.getSubtitle());

            // Hiển thị trạng thái checkbox
            binding.quizCompletedCheckbox.setChecked(model.isCompleted());

            // Sự kiện khi nhấn checkbox
            binding.quizCompletedCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                model.setCompleted(isChecked); // Cập nhật trạng thái
            });

            // Sự kiện khi nhấn vào item
            binding.getRoot().setOnClickListener(view -> {
                if (!model.isCompleted()) { // Chỉ mở bài học khi chưa hoàn thành
                    Intent intent = new Intent(binding.getRoot().getContext(), QuizActivity.class);
                    QuizActivity.questionModelList = model.getQuestionList();
                    binding.getRoot().getContext().startActivity(intent);
                } else {
                    // Hiển thị Custom Toast khi bài học đã hoàn thành
                    showCustomToast(
                            binding.getRoot().getContext(),
                            R.drawable.asuccessicon
                    );
                }
            });
        }

        // Sửa lỗi khi không truyền null vào inflate và cho phép truyền message tùy chỉnh
        @SuppressLint("SetTextI18n")
        private void showCustomToast(Context context, int iconResId) {
            // Thay vì sử dụng null, dùng parent để tránh lỗi view root null
            View customToastView = LayoutInflater.from(context).inflate(
                    R.layout.custom_toast, (ViewGroup) null
            );

            // Bind các thành phần trong custom toast layout
            android.widget.TextView toastText = customToastView.findViewById(R.id.toast_text);
            ImageView toastIcon = customToastView.findViewById(R.id.toast_image);

            toastText.setText("Bạn đã hoàn thành bài học này!"); // Sử dụng giá trị message từ tham số
            toastIcon.setImageResource(iconResId);

            Toast toast = new Toast(context);
            toast.setView(customToastView);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QuizItemRecyclerRowBinding binding = QuizItemRecyclerRowBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return quizModelList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(quizModelList.get(position));
    }
}
