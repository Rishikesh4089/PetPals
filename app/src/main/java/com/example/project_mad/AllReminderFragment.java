package com.example.project_mad;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AllReminderFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private FirebaseAuth mAuth;
    private TextView noRemindersTextView;
    private DatabaseReference remindersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_reminder, container, false);

        recyclerView = rootView.findViewById(R.id.allReminderRecyclerView);
        noRemindersTextView = rootView.findViewById(R.id.noRemindersTextView);
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(getContext(), reminderList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            remindersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("reminders");

            loadAllReminders();
        }

        return rootView;
    }

    public static class Reminder {
        private String key;
        private String title;
        private String description;
        private String status;
        private String scheduleTime;

        public Reminder() {}

        public Reminder(String key, String title, String description, String status, String scheduleTime) {
            this.key = key;
            this.title = title;
            this.description = description;
            this.status = status;
            this.scheduleTime = scheduleTime;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
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

        public void setStatus(String status) {
            this.status = status;
        }

        public String getScheduleTime() {
            return scheduleTime;
        }

        public void setScheduleTime(String scheduleTime) {
            this.scheduleTime = scheduleTime;
        }
    }

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

            // Set the checkbox state and disable it if the reminder is completed
            if (reminder.getStatus().equals("completed")) {
                holder.checkBox.setChecked(true);
                holder.checkBox.setEnabled(false); // Disable checkbox
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(Color.GRAY)); // Set to grey
            } else {
                holder.checkBox.setChecked(false);
                holder.checkBox.setEnabled(true); // Enable checkbox if status is not "completed"
                holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        // Update the status in Firebase to "completed"
                        updateReminderStatus(reminder, "completed");
                    }
                });
            }
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
                            notifyDataSetChanged(); // Update UI
                        } else {
                            Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadAllReminders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        remindersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();
                for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                    Reminder reminder = reminderSnapshot.getValue(Reminder.class);
                    if (reminder != null && !"completed".equals(reminder.getStatus())) {
                        reminder.setKey(reminderSnapshot.getKey());
                        reminderList.add(reminder);
                    }
                }
                adapter.notifyDataSetChanged();

                if (reminderList.isEmpty()) {
                    noRemindersTextView.setVisibility(View.VISIBLE);
                } else {
                    noRemindersTextView.setVisibility(View.GONE);
                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load reminders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
