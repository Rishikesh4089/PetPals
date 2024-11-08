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

import java.util.ArrayList;
import java.util.List;

public class CompletedReminderFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;
    private FirebaseAuth mAuth;
    private DatabaseReference remindersRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_completed_reminder, container, false);

        recyclerView = rootView.findViewById(R.id.completeReminderRecyclerView);
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(getContext(), reminderList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            remindersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("reminders");

            loadCompletedReminders();
        }

        return rootView;
    }

    // Reminder data class with a unique key field
    public static class Reminder {
        private String key; // Unique key from Firebase
        private String title;
        private String description;
        private String status; // "scheduled" or "completed"

        public Reminder() {
        }

        public Reminder(String key, String title, String description, String status) {
            this.key = key;
            this.title = title;
            this.description = description;
            this.status = status;
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

            // Always check the checkbox since the reminder status is "completed"
            holder.checkBox.setChecked(true);

            // Disable the checkbox so the user cannot uncheck it
            holder.checkBox.setEnabled(false);
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
    }

    // Method to load completed reminders
    private void loadCompletedReminders() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference remindersRef = FirebaseDatabase.getInstance()
                .getReference("users").child(userId).child("reminders");

        remindersRef.orderByChild("status").equalTo("completed")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reminderList.clear();
                        for (DataSnapshot reminderSnapshot : snapshot.getChildren()) {
                            Reminder reminder = reminderSnapshot.getValue(Reminder.class);
                            if (reminder != null) {
                                reminder.setKey(reminderSnapshot.getKey()); // Set the unique key
                                reminderList.add(reminder);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Failed to load completed reminders", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
