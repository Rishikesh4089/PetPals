package com.example.project_mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeReminderAdapter extends RecyclerView.Adapter<HomeReminderAdapter.ReminderViewHolder> {

    private List<HomeReminder> reminderList;

    public HomeReminderAdapter(List<HomeReminder> reminderList) {
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        HomeReminder reminder = reminderList.get(position);
        holder.reminderTextView.setText(reminder.getReminderText()); // Bind reminder text
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView reminderTextView;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            reminderTextView = itemView.findViewById(R.id.reminderTextView);
        }
    }
}
