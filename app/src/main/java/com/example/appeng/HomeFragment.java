package com.example.appeng;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.appeng.databinding.ActivityHomeFragmentBinding;
import java.util.Collections;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class HomeFragment extends Fragment {

    private ActivityHomeFragmentBinding binding;
    private MeaningAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho Fragment này
        binding = ActivityHomeFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set up Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.notification);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.notification) {
                Toast.makeText(getActivity(), "Thông báo đã được nhấn!", Toast.LENGTH_SHORT).show();  // "Notification clicked!" -> "Thông báo đã được nhấn!"
                return true;
            }
            return false;
        });

        // Gán các Button từ layout vào các biến
        Button btnGrammar = view.findViewById(R.id.btnGrammar);
        Button btnVocabulary = view.findViewById(R.id.btnVocabulary);
        Button btnGames = view.findViewById(R.id.btnGames);
        Button btnQuiz = view.findViewById(R.id.btnQuiz);

        // Set up RecyclerView và Adapter
        adapter = new MeaningAdapter(Collections.emptyList());
        binding.meaningRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.meaningRecyclerView.setAdapter(adapter);

        // Set up sự kiện khi nhấn nút tìm kiếm
        binding.searchBtn.setOnClickListener(v -> {
            String word = binding.searchInput.getText().toString();
            getMeaning(word);
        });

        // Handle Personal button click
        btnGrammar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NguPhapActivity.class);
            startActivity(intent);
        });

        // Handle Personal button click
        btnVocabulary.setOnClickListener(v -> {
            // Thay vì Intent, sử dụng FragmentTransaction để thay thế fragment
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new LearnFragment());
            transaction.addToBackStack(null); // Cho phép quay lại Fragment trước đó
            transaction.commit();
        });


        // Handle Personal button click
        btnGames.setOnClickListener(v -> {
            // Thay vì Intent, sử dụng FragmentTransaction để thay thế fragment
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new GameFragment());
            transaction.addToBackStack(null); // Cho phép quay lại Fragment trước đó
            transaction.commit();
        });

        // Handle Personal button click
        btnQuiz.setOnClickListener(v -> {
            // Thay vì Intent, sử dụng FragmentTransaction để thay thế fragment
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new TaskFragment());
            transaction.addToBackStack(null); // Cho phép quay lại Fragment trước đó
            transaction.commit();
        });

        return view;
    }

    private void getMeaning(String word) {
        setInProgress(true);

        Call<List<WordResult>> call = RetrofitInstance.getDictionaryApi().getMeaning(word);
        call.enqueue(new Callback<List<WordResult>>() {
            @Override
            public void onResponse(@NonNull Call<List<WordResult>> call, @NonNull Response<List<WordResult>> response) {
                setInProgress(false);
                if (response.body() != null && !response.body().isEmpty()) {
                    setUI(response.body().get(0));
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy từ", Toast.LENGTH_SHORT).show();  // "Word not found" -> "Không tìm thấy từ"
                }
            }

            @Override
            public void onFailure(Call<List<WordResult>> call, Throwable t) {
                setInProgress(false);
                Toast.makeText(getContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show();  // "Something went wrong" -> "Đã xảy ra lỗi"
            }
        });
    }

    private void setUI(WordResult response) {
        binding.wordTextview.setText(response.getWord());
        binding.phoneticTextview.setText(response.getPhonetic());
        adapter.updateNewData(response.getMeanings());
    }

    private void setInProgress(boolean inProgress) {
        if (inProgress) {
            binding.searchBtn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.searchBtn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Để tránh memory leaks
    }
}
