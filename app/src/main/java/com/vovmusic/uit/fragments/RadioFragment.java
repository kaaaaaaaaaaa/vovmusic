package com.vovmusic.uit.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vovmusic.uit.R;

public class RadioFragment extends Fragment {
//    private TextView tvRoom1;
//    private TextView tvRoom2;
//    private ImageView room1;
//    private ImageView room2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_radio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        this.tvRoom1 = view.findViewById(R.id.tvRoom1);
//        this.tvRoom1.setSelected(true); // Text will be moved
//
//        this.tvRoom2 = view.findViewById(R.id.tvRoom2);
//        this.tvRoom2.setSelected(true); // Text will be moved
//
//        this.room1 = view.findViewById(R.id.ivRoom1);
//        this.room2 = view.findViewById(R.id.ivRoom2);
//
//        String roomName1 = this.tvRoom1.getText().toString().trim();
//        room1.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), RadioActivity.class);
//            intent.putExtra("room", 1);
//            intent.putExtra("ROOMNAME1", roomName1);
//            startActivity(intent);
//        });
//
//        String roomName2 = this.tvRoom2.getText().toString().trim();
//        room2.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), RadioActivity.class);
//            intent.putExtra("room", 2);
//            intent.putExtra("ROOMNAME2", roomName2);
//            startActivity(intent);
//        });
    }
}