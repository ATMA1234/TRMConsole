package com.example.ticketingtool_library.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ticketingtool_library.MainActivity;
import com.example.ticketingtool_library.R;

import java.util.Objects;

public class Settings extends Fragment {

    public Settings() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initialize(view);
        return view;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------
    private void initialize(View view){
        EditText editText = ((MainActivity) Objects.requireNonNull(getActivity())).getEditText();
        editText.setVisibility(View.GONE);
    }
}