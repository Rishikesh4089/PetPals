package com.example.project_mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private OnAppointmentDeleteListener deleteListener;

    public interface OnAppointmentDeleteListener {
        void onDelete(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> appointmentList, OnAppointmentDeleteListener deleteListener) {
        this.appointmentList = appointmentList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        holder.doctorNameTextView.setText(appointment.getDoctorName());
        holder.dateTextView.setText(appointment.getDate());
        holder.timeTextView.setText(appointment.getTime());
        holder.reasonTextView.setText(appointment.getReason());
        holder.typeTextView.setText(appointment.getType());

        // Set up the delete button
        holder.deleteButton.setOnClickListener(v -> deleteListener.onDelete(appointment));
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView doctorNameTextView, dateTextView, timeTextView, reasonTextView, typeTextView;
        Button deleteButton;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorNameTextView = itemView.findViewById(R.id.doctor_name);
            dateTextView = itemView.findViewById(R.id.appointment_date);
            timeTextView = itemView.findViewById(R.id.appointment_time);
            reasonTextView = itemView.findViewById(R.id.appointment_reason);
            typeTextView = itemView.findViewById(R.id.appointment_type);
            deleteButton = itemView.findViewById(R.id.delete_button); // Ensure your item_appointment layout has this button
        }
    }
}
