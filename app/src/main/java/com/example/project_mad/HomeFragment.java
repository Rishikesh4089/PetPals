package com.example.project_mad;

import android.net.Uri;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;

public class HomeFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView progressText, welcomeMessage, reminderText;
    private CalendarView calendarView;
    private CircleImageView petProfileImage;

    private FirebaseAuth auth;
    private DatabaseReference userRef, remindersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        calendarView = view.findViewById(R.id.calendarView);
        welcomeMessage = view.findViewById(R.id.welcomeMessage);
        reminderText = view.findViewById(R.id.reminderText);
        petProfileImage = view.findViewById(R.id.photoImageView);

        // Firebase references
        auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        remindersRef = FirebaseDatabase.getInstance().getReference("reminders");

        // Set up circular progress bar


        // Set up calendar view listener for selecting dates
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            String date = dayOfMonth + "/" + (month + 1) + "/" + year;
            progressText.setText("Selected Date: " + date);
        });

        // Load user data
        loadUserData();

        // Load today's reminders
        loadTodayReminders();

        return view;
    }


    private void loadUserData() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    String petName = dataSnapshot.child("petName").getValue(String.class);
                    String petImageUrl = dataSnapshot.child("petImageUrl").getValue(String.class);

                    welcomeMessage.setText("Welcome back, " + petName + "!");

                    if (petImageUrl != null) {
                        // Load the image from the URL using Glide
                        Glide.with(this)
                                .load(petImageUrl)
                                .placeholder(R.drawable.ic_default_pet_image) // Optional placeholder image
                                .error(R.drawable.ic_default_pet_image)       // Error image if loading fails
                                .into(petProfileImage);
                    }
                }
            }
        });
    }


    private void loadTodayReminders() {
        String todayDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        remindersRef.orderByChild("date").equalTo(todayDate).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                StringBuilder remindersBuilder = new StringBuilder();
                for (DataSnapshot reminderSnapshot : task.getResult().getChildren()) {
                    String title = reminderSnapshot.child("title").getValue(String.class);
                    if (title != null) {
                        remindersBuilder.append(title).append("\n");
                    }
                }
                reminderText.setText(remindersBuilder.toString().trim());
            }
        });
    }
}
