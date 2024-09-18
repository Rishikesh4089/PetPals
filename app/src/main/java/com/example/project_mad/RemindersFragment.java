package com.example.project_mad;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;

public class RemindersFragment extends Fragment {

    private CalendarView calendarView;
    private LinearLayout eventsContainer;
    private TextView noRemindersTextView;
    private FloatingActionButton addReminderButton;
    private DatabaseReference userRef;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminders, container, false);

        // Initialize the views
        calendarView = rootView.findViewById(R.id.calendarView);
        eventsContainer = rootView.findViewById(R.id.eventsContainer);
        addReminderButton = rootView.findViewById(R.id.addReminderButton);

        // Add a TextView for "No Reminders" message
        noRemindersTextView = new TextView(getContext());
        noRemindersTextView.setText("No reminders set");
        noRemindersTextView.setTextSize(18);
        noRemindersTextView.setTextColor(getResources().getColor(android.R.color.black));
        noRemindersTextView.setVisibility(View.GONE); // Initially hidden
        eventsContainer.addView(noRemindersTextView);

        // Get the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("reminders");
            fetchReminders();
        }

        // Set up CalendarView listener
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String dateKey = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                showRemindersForDate(dateKey);
            }
        });

        // Set up the FloatingActionButton click listener to add a new reminder
        addReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open a dialog or activity to add a new reminder
                // After saving, call fetchReminders() to update the display
            }
        });

        return rootView;
    }

    /**
     * Fetch reminders from Firebase and display them in the eventsContainer.
     */
    private void fetchReminders() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsContainer.removeAllViews(); // Clear previous views

                if (snapshot.exists()) {
                    for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                        String date = reminderSnapshot.getKey();
                        String reminder = reminderSnapshot.getValue(String.class);
                        addEventCard(date, reminder);
                    }
                } else {
                    // No reminders exist
                    noRemindersTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load reminders.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Display reminders for the selected date.
     */
    private void showRemindersForDate(String date) {
        userRef.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String reminder = snapshot.getValue(String.class);
                    showReminderDialog(date, reminder);
                } else {
                    Toast.makeText(getContext(), "No events for this date", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load reminder for the selected date.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Display a dialog showing the reminder details.
     */
    private void showReminderDialog(String date, String reminder) {
        new AlertDialog.Builder(getContext())
                .setTitle("Reminder for " + date)
                .setMessage(reminder)
                .setPositiveButton("OK", null)
                .show();
    }

    /**
     * Add a CardView to the eventsContainer for each reminder.
     */
    private void addEventCard(String date, String reminder) {
        // Hide the "No reminders set" message if any reminders exist
        noRemindersTextView.setVisibility(View.GONE);

        // Create a new CardView and set its properties
        CardView cardView = new CardView(getContext());
        cardView.setCardElevation(6);
        cardView.setRadius(8);
        cardView.setContentPadding(16, 16, 16, 16);

        // Create a LinearLayout to hold the reminder information
        LinearLayout cardLayout = new LinearLayout(getContext());
        cardLayout.setOrientation(LinearLayout.VERTICAL);

        // Create and add TextViews for date and reminder
        TextView dateTextView = new TextView(getContext());
        dateTextView.setText("Date: " + date);
        dateTextView.setTextSize(16);
        dateTextView.setTextColor(getResources().getColor(android.R.color.black));
        cardLayout.addView(dateTextView);

        TextView reminderTextView = new TextView(getContext());
        reminderTextView.setText("Reminder: " + reminder);
        reminderTextView.setTextSize(14);
        reminderTextView.setTextColor(getResources().getColor(android.R.color.black));
        cardLayout.addView(reminderTextView);

        // Add the LinearLayout to the CardView
        cardView.addView(cardLayout);

        // Add the CardView to the eventsContainer
        eventsContainer.addView(cardView);
    }
}
