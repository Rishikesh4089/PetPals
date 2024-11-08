package com.example.project_mad;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodayReminderFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private FirebaseAuth mAuth;
    private DatabaseReference remindersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_today_reminder, container, false);

        recyclerView = rootView.findViewById(R.id.todayReminderRecyclerView);
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(getContext(), reminderList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            remindersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("reminders");

            loadTodayReminders(); // Only load reminders when the page opens
        }

        return rootView;
    }

    // Reminder data class with a unique key and oldStatus field
    public static class Reminder {
        private String key; // Unique key from Firebase
        private String title;
        private String description;
        private String status; // "scheduled", "completed", or "flagged"
        private String oldStatus; // Stores the previous status
        private String date;
        private String time;

        public Reminder() {
        }

        public Reminder(String key, String title, String description, String status, String date, String time) {
            this.key = key;
            this.title = title;
            this.description = description;
            this.status = status;
            this.oldStatus = status; // Initialize oldStatus with the current status
            this.date = date;
            this.time = time;
        }

        public void setStatus(String status) {
            this.oldStatus = this.status; // Save current status as oldStatus
            this.status = status;
        }

        public String getOldStatus() {
            return oldStatus;
        }

        public String getKey() {
            return key;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getStatus() {
            return status;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public void setKey(String key){
            this.key = key;
        }
    }

    // Adapter for RecyclerView
    public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

        private List<Reminder> reminderList;
        private Context context;

        public ReminderAdapter(Context context, List<Reminder> reminderList) {
            this.context = context;
            this.reminderList = reminderList;
        }

        @NonNull
        @Override
        public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reminder_card, parent, false);
            return new ReminderViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
            Reminder reminder = reminderList.get(position);
            holder.reminderTitle.setText(reminder.getTitle());
            holder.reminderDescription.setText(reminder.getDescription());

            holder.checkBox.setChecked(reminder.getStatus().equals("completed"));

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String newStatus;
                if (isChecked) {
                    // When checked, set status to "completed"
                    reminder.setStatus("completed");
                    newStatus = "completed";
                } else {
                    // When unchecked, revert to oldStatus
                    newStatus = reminder.getOldStatus();
                    reminder.setStatus(newStatus);
                }
                updateReminderStatus(reminder, newStatus);
            });
        }

        @Override
        public int getItemCount() {
            return reminderList.size();
        }

        public class ReminderViewHolder extends RecyclerView.ViewHolder {
            public TextView reminderTitle, reminderDescription;
            public CheckBox checkBox;

            public ReminderViewHolder(View itemView) {
                super(itemView);
                reminderTitle = itemView.findViewById(R.id.reminderTitle);
                reminderDescription = itemView.findViewById(R.id.reminderDescription);
                checkBox = itemView.findViewById(R.id.checkBox);
            }
        }

        private void updateReminderStatus(Reminder reminder, String newStatus) {
            reminder.setStatus(newStatus);
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference reminderRef = FirebaseDatabase.getInstance()
                    .getReference("users").child(userId).child("reminders").child(reminder.getKey());

            reminderRef.child("status").setValue(newStatus)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Reminder status updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadTodayReminders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference remindersRef = FirebaseDatabase.getInstance()
                .getReference("users").child(userId).child("reminders");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf.format(calendar.getTime());

        remindersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    Reminder reminder = reminderSnapshot.getValue(Reminder.class);
                    if (reminder != null && currentDate.equals(reminder.getDate())
                            && ("scheduled".equals(reminder.getStatus()) || "flagged".equals(reminder.getStatus()))) {
                        reminder.setKey(reminderSnapshot.getKey());
                        reminderList.add(reminder);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
