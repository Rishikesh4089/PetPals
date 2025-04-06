package com.example.project_mad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchReminderAdapter extends RecyclerView.Adapter<SearchReminderAdapter.ReminderViewHolder> {
    private List<Reminder> remindersList;
    private Context context;

    public SearchReminderAdapter(Context context, List<Reminder> remindersList) {
        this.context = context;
        this.remindersList = remindersList;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.reminder_card, parent, false);
        return new ReminderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = remindersList.get(position);
        holder.title.setText(reminder.getTitle());
        holder.description.setText(reminder.getDescription());
    }

    @Override
    public int getItemCount() {
        return remindersList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reminderTitle);
            description = itemView.findViewById(R.id.reminderDescription);
        }
    }

    // This method will allow us to update the list when search results change
    public void updateData(List<Reminder> newReminders) {
        this.remindersList = newReminders;
        notifyDataSetChanged();
    }
}
