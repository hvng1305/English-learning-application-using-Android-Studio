package com.example.appeng;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.appeng.databinding.TaskItemRecyclerRowBinding;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.MyViewHolder> {

    private final List<TaskQuizModel> taskModelList;

    // Constructor
    public TaskListAdapter(List<TaskQuizModel> taskModelList) {
        this.taskModelList = taskModelList;
    }

    // ViewHolder class
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TaskItemRecyclerRowBinding binding;

        public MyViewHolder(TaskItemRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(TaskQuizModel model) {
            binding.quizTitleText.setText(model.getTitle());
            binding.quizSubtitleText.setText(model.getSubtitle());
            binding.quizTimeText.setText(model.getTime() + " min");

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(binding.getRoot().getContext(), TaskQuiz.class);
                    TaskQuiz.questionModelList = model.getQuestionList(); // Chuyển danh sách câu hỏi
                    TaskQuiz.time = model.getTime();
                    binding.getRoot().getContext().startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaskItemRecyclerRowBinding binding = TaskItemRecyclerRowBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(taskModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return taskModelList.size();
    }
}
