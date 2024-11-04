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
import android.widget.Spinner;
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
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private ArrayList<String> listOptions;
    private ArrayAdapter<String> listAdapter;
    private boolean isFlagged = false;

    public RemindersFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminders, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("lists");
            ensureDefaultList();
        } else {
            Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
        }

        addReminderButton = rootView.findViewById(R.id.addReminderButton);
        eventsContainer = rootView.findViewById(R.id.eventsContainer);
        searchEditText = rootView.findViewById(R.id.search_bar);
        listOptions = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listOptions);
        listAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addReminderButton.setOnClickListener(view -> showAddReminderDialog());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchReminders(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        loadLists();
        loadReminders();
        return rootView;
    }

    private void ensureDefaultList() {
        userRef.child("reminders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    userRef.child("reminders").setValue("Default reminders list");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
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
            EditText descriptionEditText = dialogView.findViewById(R.id.notes);
            TextView dateEditText = dialogView.findViewById(R.id.Date);
            TextView timeEditText = dialogView.findViewById(R.id.Time);
            Spinner listSpinner = dialogView.findViewById(R.id.Listreminders);
            TextView flagButton = dialogView.findViewById(R.id.flag);
            TextView saveButton = dialogView.findViewById(R.id.add);

            RemindersFragment parentFragment = (RemindersFragment) getParentFragment();
            if (parentFragment != null) {
                listSpinner.setAdapter(parentFragment.listAdapter);
            }

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
                String list = listSpinner.getSelectedItem() != null ? listSpinner.getSelectedItem().toString() : "";

                if (!TextUtils.isEmpty(title)) {
                    String status = determineStatus(date, time);
                    if (status.equals("Unscheduled")) {
                        dateEditText.setText("");
                        timeEditText.setText("");
                    }
                    if (parentFragment != null) {
                        parentFragment.addNewReminder(list, title, description, date, time, status);
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


    private void addNewReminder(String list, String title, String description, String date, String time, String status) {
        DatabaseReference reminderListRef = userRef.child(list);
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

    // Load lists from Firebase
    private void loadLists() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listOptions.clear();
                for (DataSnapshot listSnapshot : snapshot.getChildren()) {
                    listOptions.add(listSnapshot.getKey());
                }
                listAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Error loading lists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReminders() {
        userRef.child("reminders").addValueEventListener(new ValueEventListener() {
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
        View reminderCard = LayoutInflater.from(getActivity()).inflate(R.layout.reminder_card, null);

        TextView titleTextView = reminderCard.findViewById(R.id.reminderTitle);
        TextView descriptionTextView = reminderCard.findViewById(R.id.reminderDescription);

        titleTextView.setText(title);
        descriptionTextView.setText(description);

        eventsContainer.addView(reminderCard);
    }

    private void searchReminders(String query) {
        // Implementation for searching reminders based on the title
    }
}
