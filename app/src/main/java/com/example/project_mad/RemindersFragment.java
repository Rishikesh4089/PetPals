package com.example.project_mad;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class RemindersFragment extends Fragment {
    private FloatingActionButton addReminderButton;
    private LinearLayout eventsContainer;
    private androidx.appcompat.widget.SearchView search_bar;
    private TextView noRemindersTextView;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private ReminderReceiver reminderReceiver;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reminders, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("reminders");
        } else {
            Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
        }

        addReminderButton = rootView.findViewById(R.id.addReminderButton);
        eventsContainer = rootView.findViewById(R.id.eventsContainer);
        search_bar = rootView.findViewById(R.id.search_bar);

        noRemindersTextView = rootView.findViewById(R.id.noRemindersTextView);

        addReminderButton.setOnClickListener(view -> showAddReminderDialog());

        reminderReceiver = new ReminderReceiver();
        IntentFilter filter = new IntentFilter("com.example.project_mad.REMINDER_EVENT");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().registerReceiver(reminderReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        }

        setupButtonListeners(rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (reminderReceiver != null) {
            getActivity().unregisterReceiver(reminderReceiver);
        }
    }

    private void setupButtonListeners(View rootView) {
        rootView.findViewById(R.id.todaybutton).setOnClickListener(v -> openFragment(new TodayReminderFragment()));
        rootView.findViewById(R.id.scheduledbutton).setOnClickListener(v -> openFragment(new ScheduledReminderFragment()));
        rootView.findViewById(R.id.allbutton).setOnClickListener(v -> openFragment(new AllReminderFragment()));
        rootView.findViewById(R.id.flaggedbutton).setOnClickListener(v -> openFragment(new FlagReminderFragment()));
        rootView.findViewById(R.id.completedbutton).setOnClickListener(v -> openFragment(new CompletedReminderFragment()));
    }

    private void openFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void showAddReminderDialog() {
        AddReminderDialog dialog = AddReminderDialog.newInstance(userRef);
        dialog.show(getChildFragmentManager(), "AddReminderDialog");
    }

    public static class AddReminderDialog extends BottomSheetDialogFragment {
        private boolean isFlagged = false;
        private DatabaseReference reminderRef;

        public static AddReminderDialog newInstance(DatabaseReference ref) {
            AddReminderDialog fragment = new AddReminderDialog();
            fragment.reminderRef = ref;
            return fragment;
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

            flagButton.setOnClickListener(v -> {
                isFlagged = !isFlagged;
                flagButton.setText(isFlagged ? "Flagged" : "Flag");
            });

            dateEditText.setOnClickListener(v -> openDatePicker(dateEditText));
            timeEditText.setOnClickListener(v -> openTimePicker(timeEditText));

            saveButton.setOnClickListener(v -> {
                String title = titleEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String date = dateEditText.getText().toString().trim();
                String time = timeEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(title)) {
                    String status = determineStatus(date, time);

                    HashMap<String, Object> reminderData = new HashMap<>();
                    reminderData.put("title", title);
                    reminderData.put("description", description);
                    reminderData.put("date", date);
                    reminderData.put("time", time);
                    reminderData.put("status", status);
                    reminderData.put("flagged", isFlagged);

                    if (reminderRef != null) {
                        String key = reminderRef.push().getKey();
                        reminderRef.child(key).setValue(reminderData);
                        Toast.makeText(getActivity(), "Reminder Saved", Toast.LENGTH_SHORT).show();
                    }

                    Intent reminderIntent = new Intent("com.example.project_mad.REMINDER_EVENT");
                    reminderIntent.putExtra("reminder_title", title);
                    reminderIntent.putExtra("reminder_description", description);
                    requireActivity().sendBroadcast(reminderIntent);

                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Title cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });

            return dialogView;
        }

        private String determineStatus(String date, String time) {
            if (isFlagged) return "flagged";
            if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) return "unscheduled";
            return "scheduled";
        }

        private void openDatePicker(TextView dateEditText) {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(requireContext(), (view, year, month, day) -> {
                dateEditText.setText(day + "/" + (month + 1) + "/" + year);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        }

        private void openTimePicker(TextView timeEditText) {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                timeEditText.setText(formattedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }
    }
}
