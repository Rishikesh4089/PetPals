package com.example.project_mad;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoDisplayFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_TEXT = "text";
    private static final String ARG_IMAGE = "image";

    public static InfoDisplayFragment newInstance(String title, String text, int imageResId) {
        InfoDisplayFragment fragment = new InfoDisplayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_TEXT, text);
        args.putInt(ARG_IMAGE, imageResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_info_display_fragment, container, false);
        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView infoTextView = view.findViewById(R.id.infoTextView);
        ImageView infoImageView = view.findViewById(R.id.infoImageView);

        if (getArguments() != null) {
            String title = getArguments().getString(ARG_TITLE);
            String text = getArguments().getString(ARG_TEXT);
            int imageResId = getArguments().getInt(ARG_IMAGE);

            titleTextView.setText(title);
            infoTextView.setText(text);
            infoImageView.setImageResource(imageResId);
        }

        return view;
    }
}
