package com.example.appeng;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GameFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment and assign it to a view
        View view = inflater.inflate(R.layout.activity_game_fragment, container, false);

        // Initialize btnPlay by using the view to find the button
        Button btnPlay = view.findViewById(R.id.btnPlay);

        btnPlay.setOnClickListener(v -> {
            // Tạo Intent để chuyển sang GameWord Activity
            Intent intent = new Intent(getActivity(), GameWord.class);
            startActivity(intent);
        });


        // Return the view for this fragment
        return view;
    }
}
