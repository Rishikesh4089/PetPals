package com.example.project_mad;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mad.R;
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

    private FloatingActionButton addReminderButton;
    private LinearLayout eventsContainer;
    private EditText searchEditText;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    public RemindersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminders, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Initialize Firebase reference with the current user's userId
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("Reminders");
        } else {
            // Handle the case when no user is signed in (for example, show a login screen or error)
            Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
        }
        // Initialize Firebase reference

        // Initialize UI elements
        addReminderButton = rootView.findViewById(R.id.addReminderButton);
        eventsContainer = rootView.findViewById(R.id.eventsContainer);
        searchEditText = rootView.findViewById(R.id.search_bar);

        // Add reminder button click listener
        addReminderButton.setOnClickListener(view -> showAddReminderDialog());

        // Search bar functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchReminders(s.toString()); // Trigger search as the user types
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });

        // Load reminders on start
        loadReminders();

        return rootView;
    }

    // Show the add reminder dialog
    private void showAddReminderDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.dialog_add_reminder, null);

        EditText titleEditText = dialogView.findViewById(R.id.reminderTitleEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.reminderDescriptionEditText);
        EditText dateEditText = dialogView.findViewById(R.id.reminderDateEditText);
        EditText timeEditText = dialogView.findViewById(R.id.reminderTimeEditText);
        Spinner listSpinner = dialogView.findViewById(R.id.reminderListSpinner);
        Spinner statusSpinner = dialogView.findViewById(R.id.reminderStatusSpinner);

        // Define the list of options for the spinners
        String[] reminderOptions = {"Completed", "Scheduled", "Flagged"};

        // Set the array of options to both spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, reminderOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSpinner.setAdapter(adapter);
        statusSpinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setPositiveButton("Save", (dialogInterface, i) -> {
                    String title = titleEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    String date = dateEditText.getText().toString();
                    String time = timeEditText.getText().toString();
                    String list = listSpinner.getSelectedItem().toString();
                    String status = statusSpinner.getSelectedItem().toString();

                    if (!TextUtils.isEmpty(title)) {
                        addNewReminder(title, description, time, list, date, status);
                    } else {
                        Toast.makeText(getActivity(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }

    // Add new reminder to Firebase
    private void addNewReminder(String title, String description, String time, String list, String date, String status) {
        String reminderId = userRef.push().getKey();
        if (reminderId == null) {
            Toast.makeText(getActivity(), "Error generating reminder ID", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> reminderMap = new HashMap<>();
        reminderMap.put("title", title);
        reminderMap.put("description", description);
        reminderMap.put("time", time);
        reminderMap.put("list", list);
        reminderMap.put("date", date);
        reminderMap.put("status", status);

        userRef.child(reminderId).setValue(reminderMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Reminder added", Toast.LENGTH_SHORT).show();
                loadReminders();  // Refresh the reminder list
            } else {
                Toast.makeText(getActivity(), "Error adding reminder", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load reminders from Firebase
    private void loadReminders() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsContainer.removeAllViews();
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    String title = reminderSnapshot.child("title").getValue(String.class);
                    String description = reminderSnapshot.child("description").getValue(String.class);
                    addReminderCard(title, description);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error loading reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add a reminder card to the UI
    private void addReminderCard(String title, String description) {
        // Custom code to add a reminder card to your eventsContainer LinearLayout
        // Inflate a layout for each reminder and add it to the container
    }

    // Search for reminders in Firebase by title
    private void searchReminders(String query) {
        userRef.orderByChild("title").startAt(query).endAt(query + "\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsContainer.removeAllViews();
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    String title = reminderSnapshot.child("title").getValue(String.class);
                    String description = reminderSnapshot.child("description").getValue(String.class);
                    addReminderCard(title, description);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Search failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Show reminders by their status
    private void showRemindersByStatus(String status) {
        userRef.orderByChild("status").equalTo(status).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsContainer.removeAllViews();
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    String title = reminderSnapshot.child("title").getValue(String.class);
                    String description = reminderSnapshot.child("description").getValue(String.class);
                    addReminderCard(title, description);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error loading reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mark reminders on the calendar (requires CalendarView and customization)
    private void showCalendarMarkers() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    String date = reminderSnapshot.child("date").getValue(String.class);
                    markDateOnCalendar(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error marking dates", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Mark date on CalendarView (custom method)
    private void markDateOnCalendar(String date) {
        // Your logic to mark a date on the CalendarView widget
    }
}
