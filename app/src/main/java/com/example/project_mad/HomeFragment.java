package com.example.project_mad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView progressText;
    private CalendarView calendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        progressBar = view.findViewById(R.id.circularProgressBar);
        progressText = view.findViewById(R.id.progressText);
        calendarView = view.findViewById(R.id.calendarView);

        // Set up circular progress bar
        setProgress(75); // Example of setting progress to 75%

        // Set up calendar view listener for selecting dates
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            // Show the selected date or handle event fetching for that date
            progressText.setText("Selected Date: " + date);
        });

        return view;
    }

    private void setProgress(int progress) {
        progressBar.setProgress(progress);
        progressText.setText(progress + "% Completed");
    }
}
