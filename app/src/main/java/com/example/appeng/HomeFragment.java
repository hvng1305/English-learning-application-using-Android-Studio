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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                Toast.makeText(getActivity(), getString(R.string.notification_clicked), Toast.LENGTH_SHORT).show();
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
            String word = binding.searchInput.getText().toString().trim();
            if (!word.isEmpty()) {
                getMeaning(word);
            } else {
                Toast.makeText(getContext(), getString(R.string.empty_search), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle Grammar button click
        btnGrammar.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NguPhapActivity.class);
            startActivity(intent);
        });

        // Handle Vocabulary button click
        btnVocabulary.setOnClickListener(v -> navigateToFragment(new LearnFragment()));

        // Handle Games button click
        btnGames.setOnClickListener(v -> navigateToFragment(new GameFragment()));

        // Handle Quiz button click
        btnQuiz.setOnClickListener(v -> navigateToFragment(new TaskFragment()));

        return view;
    }

    private void getMeaning(String word) {
        setInProgress(true);

        Call<List<WordResult>> call = RetrofitInstance.getDictionaryApi().getMeaning(word);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<WordResult>> call, @NonNull Response<List<WordResult>> response) {
                setInProgress(false);
                if (response.body() != null && !response.body().isEmpty()) {
                    setUI(response.body().get(0));
                } else {
                    Toast.makeText(getContext(), getString(R.string.word_not_found), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WordResult>> call, @NonNull Throwable t) {
                setInProgress(false);
                Toast.makeText(getContext(), getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUI(WordResult response) {
        binding.wordTextview.setText(response.getWord());
        binding.phoneticTextview.setText(response.getPhonetic());
        adapter.updateNewData(response.getMeanings());
    }

    private void setInProgress(boolean inProgress) {
        binding.searchBtn.setVisibility(inProgress ? View.INVISIBLE : View.VISIBLE);
        binding.progressBar.setVisibility(inProgress ? View.VISIBLE : View.INVISIBLE);
    }

    private void navigateToFragment(Fragment fragment) {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Để tránh memory leaks
    }
}
