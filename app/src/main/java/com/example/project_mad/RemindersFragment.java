package com.example.project_mad;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.Calendar;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project_mad.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class RemindersFragment extends Fragment {

    private FloatingActionButton addReminderButton;
    private LinearLayout eventsContainer;
    private EditText searchEditText;
    private TextView noRemindersTextView; // TextView to show "No reminders found"
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    public RemindersFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminders, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        } else {
            Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
        }

        addReminderButton = rootView.findViewById(R.id.addReminderButton);
        eventsContainer = rootView.findViewById(R.id.eventsContainer);
        searchEditText = rootView.findViewById(R.id.search_bar);
        noRemindersTextView = rootView.findViewById(R.id.noRemindersTextView); // Ensure this is defined in your XML

        addReminderButton.setOnClickListener(view -> showAddReminderDialog());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                toggleVisibility(!query.isEmpty());
                searchReminders(query);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadReminders();
        setupButtonListeners(rootView);
        return rootView;
    }

    private void setupButtonListeners(View rootView) {
        rootView.findViewById(R.id.todaybutton).setOnClickListener(v -> openFragment(new TodayReminderFragment()));
        rootView.findViewById(R.id.scheduledbutton).setOnClickListener(v -> openFragment(new ScheduledReminderFragment()));
        rootView.findViewById(R.id.allbutton).setOnClickListener(v -> openFragment(new AllReminderFragment()));
        rootView.findViewById(R.id.flaggedbutton).setOnClickListener(v -> openFragment(new FlagReminderFragment()));
        rootView.findViewById(R.id.completedbutton).setOnClickListener(v -> openFragment(new CompletedReminderFragment()));
    }

    private void openFragment(Fragment fragment) {
        if (fragment != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showAddReminderDialog() {
        AddReminderDialog dialog = AddReminderDialog.newInstance();
        dialog.show(getChildFragmentManager(), "AddReminderDialog");
    }

    public static class AddReminderDialog extends BottomSheetDialogFragment {
        private boolean isFlagged = false;

        public static AddReminderDialog newInstance() {
            return new AddReminderDialog();
        }

        @NonNull
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View dialogView = inflater.inflate(R.layout.dialog_add_reminder, container, false);
            EditText titleEditText = dialogView.findViewById(R.id.title);
            TextView cancelbutton = dialogView.findViewById(R.id.cancel);
            EditText descriptionEditText = dialogView.findViewById(R.id.notes);
            TextView dateEditText = dialogView.findViewById(R.id.Date);
            TextView timeEditText = dialogView.findViewById(R.id.Time);
            TextView flagButton = dialogView.findViewById(R.id.flag);
            TextView saveButton = dialogView.findViewById(R.id.add);

            cancelbutton.setOnClickListener(v -> dismiss());
            isFlagged = false;

            flagButton.setOnClickListener(v -> {
                isFlagged = !isFlagged;
                flagButton.setText(isFlagged ? "Flagged" : "Flag");
            });

            dateEditText.setOnClickListener(v -> openDatePicker(dateEditText));
            timeEditText.setOnClickListener(v -> openTimePicker(timeEditText));

            saveButton.setOnClickListener(v -> {
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String date = dateEditText.getText().toString();
                String time = timeEditText.getText().toString();

                if (!TextUtils.isEmpty(title)) {
                    String status = determineStatus(date, time);
                    if (status.equals("Unscheduled")) {
                        dateEditText.setText("");
                        timeEditText.setText("");
                    }
                    if (getParentFragment() != null) {
                        ((RemindersFragment) getParentFragment()).addNewReminder(title, description, date, time, status);
                    }
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            return dialogView;
        }

        private String determineStatus(String date, String time) {
            if (isFlagged) {
                return "Flagged";
            } else if ("Date".equals(date) || "Time".equals(time)) {
                return "Unscheduled";
            } else {
                return "Scheduled";
            }
        }

        private void openDatePicker(TextView dateEditText) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                dateEditText.setText(selectedDate);
            }, year, month, day);

            datePickerDialog.show();
        }

        private void openTimePicker(TextView timeEditText) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, selectedHour, selectedMinute) -> {
                String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                timeEditText.setText(selectedTime);
            }, hour, minute, true);

            timePickerDialog.show();
        }
    }

    private void addNewReminder(String title, String description, String date, String time, String status) {
        DatabaseReference reminderListRef = userRef.child("reminders");
        String reminderId = reminderListRef.push().getKey();

        if (reminderId == null) {
            Toast.makeText(getActivity(), "Error generating reminder ID", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> reminderMap = new HashMap<>();
        reminderMap.put("title", title);
        reminderMap.put("description", description);
        reminderMap.put("date", date);
        reminderMap.put("time", time);
        reminderMap.put("status", status);

        reminderListRef.child(reminderId).setValue(reminderMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Reminder added", Toast.LENGTH_SHORT).show();
                loadReminders();
            } else {
                Toast.makeText(getActivity(), "Error adding reminder", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load reminders from Firebase
    private void loadReminders() {
        userRef.child("reminders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsContainer.removeAllViews(); // Clear previous views
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    String title = reminderSnapshot.child("title").getValue(String.class);
                    String description = reminderSnapshot.child("description").getValue(String.class);
                    String date = reminderSnapshot.child("date").getValue(String.class);
                    String time = reminderSnapshot.child("time").getValue(String.class);
                    String status = reminderSnapshot.child("status").getValue(String.class);

                    // Inflate your reminder view here and add it to eventsContainer
                    View cardView = LayoutInflater.from(getActivity()).inflate(R.layout.reminder_card, eventsContainer, false);
                    TextView titleView = cardView.findViewById(R.id.reminderTitle);
                    TextView descriptionView = cardView.findViewById(R.id.reminderDescription);

                    titleView.setText(title);
                    descriptionView.setText(description);

                    eventsContainer.addView(cardView);
                }
                noRemindersTextView.setVisibility(snapshot.exists() ? View.GONE : View.VISIBLE); // Show or hide the "No reminders found" message
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error loading reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchReminders(String query) {
        // Clear the previous views
        eventsContainer.removeAllViews();

        // Load reminders from Firebase
        userRef.child("reminders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false; // Flag to check if any reminders matched
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    String title = reminderSnapshot.child("title").getValue(String.class);
                    String description = reminderSnapshot.child("description").getValue(String.class);

                    if ((title != null && title.toLowerCase().contains(query.toLowerCase())) ||
                            (description != null && description.toLowerCase().contains(query.toLowerCase()))) {

                        View cardView = LayoutInflater.from(getActivity()).inflate(R.layout.reminder_card, eventsContainer, false);
                        TextView titleView = cardView.findViewById(R.id.reminderTitle);
                        TextView descriptionView = cardView.findViewById(R.id.reminderDescription);

                        titleView.setText(title);
                        descriptionView.setText(description);

                        eventsContainer.addView(cardView);
                        found = true; // Found a matching reminder
                    }
                }
                if (!found && !TextUtils.isEmpty(query)) {
                    noRemindersTextView.setVisibility(View.VISIBLE);
                } else {
                    noRemindersTextView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error loading reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleVisibility(boolean isSearching) {
        // Toggle the visibility of the buttons and other elements
        int visibility = isSearching ? View.GONE : View.VISIBLE;
        getView().findViewById(R.id.todaybutton).setVisibility(visibility);
        getView().findViewById(R.id.scheduledbutton).setVisibility(visibility);
        getView().findViewById(R.id.allbutton).setVisibility(visibility);
        getView().findViewById(R.id.flaggedbutton).setVisibility(visibility);
        getView().findViewById(R.id.calendarView).setVisibility(visibility);
        getView().findViewById(R.id.completedbutton).setVisibility(visibility);
        noRemindersTextView.setVisibility(isSearching ? View.VISIBLE : View.GONE);
    }
}
