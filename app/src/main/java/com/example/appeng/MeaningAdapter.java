package com.example.appeng;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.example.appeng.databinding.MeaningRecyclerRowBinding;

public class MeaningAdapter extends RecyclerView.Adapter<MeaningAdapter.MeaningViewHolder> {

    private List<Meaning> meaningList;

    public MeaningAdapter(List<Meaning> meaningList) {
        this.meaningList = meaningList;
    }

    public static class MeaningViewHolder extends RecyclerView.ViewHolder {
        private final MeaningRecyclerRowBinding binding;

        public MeaningViewHolder(MeaningRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Meaning meaning) {
            binding.partOfSpeechTextview.setText(meaning.getPartOfSpeech());

            // Join definitions with numbering
            StringBuilder definitionsText = new StringBuilder();
            List<Definition> definitions = meaning.getDefinitions();
            for (int i = 0; i < definitions.size(); i++) {
                definitionsText.append(i + 1).append(". ").append(definitions.get(i).getDefinition()).append("\n\n");
            }
            binding.definitionsTextview.setText(definitionsText.toString());

            // Set synonyms if available
            if (meaning.getSynonyms().isEmpty()) {
                binding.synonymsTitleTextview.setVisibility(View.GONE);
                binding.synonymsTextview.setVisibility(View.GONE);
            } else {
                binding.synonymsTitleTextview.setVisibility(View.VISIBLE);
                binding.synonymsTextview.setVisibility(View.VISIBLE);
                binding.synonymsTextview.setText(String.join(", ", meaning.getSynonyms()));
            }

            // Set antonyms if available
            if (meaning.getAntonyms().isEmpty()) {
                binding.antonymsTitleTextview.setVisibility(View.GONE);
                binding.antonymsTextview.setVisibility(View.GONE);
            } else {
                binding.antonymsTitleTextview.setVisibility(View.VISIBLE);
                binding.antonymsTextview.setVisibility(View.VISIBLE);
                binding.antonymsTextview.setText(String.join(", ", meaning.getAntonyms()));
            }
        }
    }

    public void updateNewData(List<Meaning> newMeaningList) {
        meaningList = newMeaningList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MeaningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        MeaningRecyclerRowBinding binding = MeaningRecyclerRowBinding.inflate(inflater, parent, false);
        return new MeaningViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return meaningList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MeaningViewHolder holder, int position) {
        holder.bind(meaningList.get(position));
    }
}


