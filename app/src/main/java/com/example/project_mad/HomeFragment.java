package com.example.project_mad;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;

public class HomeFragment extends Fragment {

    private TextView welcomeMessage;
    private CalendarView calendarView;
    private CircleImageView petProfileImage;
    private RecyclerView reminderRecyclerView, appointmentRecyclerView;

    private FirebaseAuth auth;
    private DatabaseReference userRef, remindersRef, appointmentsRef;

    private String selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    private HomeReminderAdapter reminderAdapter;
    private HomeAppointmentAdapter appointmentAdapter;

    private List<HomeReminder> reminderList = new ArrayList<>();
    private List<HomeAppointment> appointmentList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        calendarView = view.findViewById(R.id.calendarView);
        welcomeMessage = view.findViewById(R.id.welcomeMessage);
        petProfileImage = view.findViewById(R.id.photoImageView);

        // Initialize RecyclerViews
        reminderRecyclerView = view.findViewById(R.id.reminderRecyclerView);
        appointmentRecyclerView = view.findViewById(R.id.appointmentRecyclerView);

        // Set RecyclerView layout managers
        reminderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        appointmentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Firebase references
        auth = FirebaseAuth.getInstance();
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        remindersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("reminders");
        appointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");

        // Load user data
        loadUserData();

        // Load reminders and appointments for the selected date
        loadRemindersAndAppointments(selectedDate);

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);

            Log.d("HomeFragment", "Selected Date: " + selectedDate);

            loadRemindersAndAppointments(selectedDate);
        });

        return view;
    }

    private void loadUserData() {
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && isAdded()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    String petName = dataSnapshot.child("petName").getValue(String.class);
                    String petImageUrl = dataSnapshot.child("petImageUrl").getValue(String.class);

                    if (petName != null) {
                        welcomeMessage.setText("Welcome back, " + petName + "!");
                    }

                    if (petImageUrl != null) {
                        Glide.with(requireContext())
                                .load(petImageUrl)
                                .placeholder(R.drawable.ic_default_pet_image) // Optional placeholder image
                                .error(R.drawable.ic_default_pet_image)       // Error image if loading fails
                                .into(petProfileImage);
                    }
                }
            }
        });
    }

    private void loadRemindersAndAppointments(String date) {
        reminderList.clear();
        appointmentList.clear();

        // Load reminders for the selected date
        remindersRef.orderByChild("date").equalTo(date).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && isAdded()) {
                for (DataSnapshot reminderSnapshot : task.getResult().getChildren()) {
                    String title = reminderSnapshot.child("title").getValue(String.class);

                    if (title != null) {
                        reminderList.add(new HomeReminder(title));
                    }
                }

                // Set reminderAdapter and update RecyclerView
                reminderAdapter = new HomeReminderAdapter(reminderList);
                reminderRecyclerView.setAdapter(reminderAdapter);
            }
        });

        // Load appointments for the selected date
        appointmentsRef.orderByChild("date").equalTo(date).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && isAdded()) {
                for (DataSnapshot appointmentSnapshot : task.getResult().getChildren()) {
                    String doctorName = appointmentSnapshot.child("doctorName").getValue(String.class);
                    String reason = appointmentSnapshot.child("reason").getValue(String.class);
                    if (doctorName != null && reason != null) {
                        appointmentList.add(new HomeAppointment(doctorName, reason));
                    }
                }

                appointmentAdapter = new HomeAppointmentAdapter(appointmentList);
                appointmentRecyclerView.setAdapter(appointmentAdapter);
            }
        });
    }
}
