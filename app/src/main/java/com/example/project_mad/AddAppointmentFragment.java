package com.example.project_mad;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class AddAppointmentFragment extends Fragment {

    private EditText appointmentTitleEditText, doctorNameEditText, appointmentReasonEditText;
    private Button dateButton, timeButton, addAppointmentButton;

    private String selectedDate = "", selectedTime = "";
    private DatabaseReference userAppointmentsRef;
    private String userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_appointment_fragment, container, false);

        // Initialize Firebase references
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userAppointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");
        }

        appointmentTitleEditText = view.findViewById(R.id.appointment_title);
        doctorNameEditText = view.findViewById(R.id.doctor_name);
        appointmentReasonEditText = view.findViewById(R.id.appointment_reason);
        dateButton = view.findViewById(R.id.appointment_date);
        timeButton = view.findViewById(R.id.appointment_time);
        addAppointmentButton = view.findViewById(R.id.add_appointment_button);

        // Set up button listeners
        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());
        addAppointmentButton.setOnClickListener(v -> addAppointment());

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateButton.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getActivity(),
                (view, selectedHour, selectedMinute) -> {
                    selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeButton.setText(selectedTime);
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void addAppointment() {
        String title = appointmentTitleEditText.getText().toString();
        String doctorName = doctorNameEditText.getText().toString();
        String appointmentReason = appointmentReasonEditText.getText().toString();

        if (!title.isEmpty() && !doctorName.isEmpty() && !selectedDate.isEmpty() && !selectedTime.isEmpty()) {
            HashMap<String, String> appointmentData = new HashMap<>();
            appointmentData.put("title", title);
            appointmentData.put("doctorName", doctorName);
            appointmentData.put("reason", appointmentReason);
            appointmentData.put("date", selectedDate);
            appointmentData.put("time", selectedTime);

            userAppointmentsRef.push().setValue(appointmentData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Appointment Added", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack(); // Go back to previous fragment
                } else {
                    Toast.makeText(getActivity(), "Failed to add appointment", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
