package com.example.project_mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeAppointmentAdapter extends RecyclerView.Adapter<HomeAppointmentAdapter.AppointmentViewHolder> {

    private List<HomeAppointment> appointmentList;

    public HomeAppointmentAdapter(List<HomeAppointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_list, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        HomeAppointment appointment = appointmentList.get(position);
        holder.doctorName.setText(appointment.getDoctorName()); // Bind doctor's name
        holder.reason.setText(appointment.getReason()); // Bind the reason for the appointment
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName, reason;

        public AppointmentViewHolder(View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctorName);
            reason = itemView.findViewById(R.id.reason);
        }
    }
}

