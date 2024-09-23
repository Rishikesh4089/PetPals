package com.example.petpals;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.project_mad.R;

import java.util.Calendar;

public class AppointmentFragment extends Fragment {

    private EditText doctorNameEditText, appointmentReasonEditText;
    private Button dateButton, timeButton, addAppointmentButton, deleteAppointmentButton;
    private ListView appointmentListView;

    private String selectedDate = "", selectedTime = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_appointment_fragment, container, false);

        doctorNameEditText = view.findViewById(R.id.doctor_name);
        appointmentReasonEditText = view.findViewById(R.id.appointment_reason);
        dateButton = view.findViewById(R.id.appointment_date);
        timeButton = view.findViewById(R.id.appointment_time);
        addAppointmentButton = view.findViewById(R.id.add_appointment_button);
        deleteAppointmentButton = view.findViewById(R.id.delete_appointment_button);
        appointmentListView = view.findViewById(R.id.appointment_list);

        // Set up date picker
        dateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getActivity(),
                    (view1, year1, month1, dayOfMonth) -> {
                        selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        dateButton.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Set up time picker
        timeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getActivity(),
                    (view12, hourOfDay, minute1) -> {
                        selectedTime = hourOfDay + ":" + minute1;
                        timeButton.setText(selectedTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        // Add appointment button
        addAppointmentButton.setOnClickListener(v -> {
            String doctorName = doctorNameEditText.getText().toString();
            String appointmentReason = appointmentReasonEditText.getText().toString();

            if (!doctorName.isEmpty() && !selectedDate.isEmpty() && !selectedTime.isEmpty()) {
                // Create appointment logic
                Toast.makeText(getActivity(), "Appointment Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete appointment button
        deleteAppointmentButton.setOnClickListener(v -> {
            // Delete appointment logic
            Toast.makeText(getActivity(), "Appointment Deleted", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
