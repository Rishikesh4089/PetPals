package com.example.project_mad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminderList;
    private Context context;
    private OnReminderCheckedListener listener;

    public interface OnReminderCheckedListener {
        void onReminderChecked(Reminder reminder, boolean isChecked);
    }

    public ReminderAdapter(List<Reminder> reminderList, Context context, OnReminderCheckedListener listener) {
        this.reminderList = reminderList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reminder_card, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.reminderTitle.setText(reminder.getTitle());
        holder.reminderDescription.setText(reminder.getDescription());
        holder.checkBox.setChecked("completed".equals(reminder.getStatus()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onReminderChecked(reminder, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView reminderTitle;
        TextView reminderDescription;
        CheckBox checkBox;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            reminderTitle = itemView.findViewById(R.id.reminderTitle);
            reminderDescription = itemView.findViewById(R.id.reminderDescription);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
