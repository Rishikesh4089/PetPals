package com.example.project_mad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AppointmentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Appointment> appointments;

    public AppointmentAdapter(Context context, ArrayList<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Object getItem(int position) {
        return appointments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false);
        }

        Appointment appointment = appointments.get(position);

        TextView doctorName = convertView.findViewById(R.id.doctor_name_view);
        TextView appointmentDate = convertView.findViewById(R.id.appointment_date_view);
        TextView appointmentTime = convertView.findViewById(R.id.appointment_time_view);

        doctorName.setText(appointment.getDoctorName());
        appointmentDate.setText(appointment.getDate());
        appointmentTime.setText(appointment.getTime());

        return convertView;
    }
}