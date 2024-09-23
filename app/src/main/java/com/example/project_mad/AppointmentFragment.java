package com.example.project_mad;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project_mad.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class AppointmentFragment extends Fragment {

    private EditText doctorNameEditText, appointmentReasonEditText;
    private Button dateButton, timeButton, addAppointmentButton, deleteAppointmentButton;
    private ListView appointmentListView;

    private String selectedDate = "", selectedTime = "";
    private DatabaseReference userAppointmentsRef;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_appointment_fragment, container, false);

        // Initialize Firebase references
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            // Reference to the current user's "appointments" node in Firebase
            userAppointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");
        }

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
                // Create appointment data
                HashMap<String, String> appointmentData = new HashMap<>();
                appointmentData.put("doctorName", doctorName);
                appointmentData.put("reason", appointmentReason);
                appointmentData.put("date", selectedDate);
                appointmentData.put("time", selectedTime);

                // Save appointment data to Firebase under the user's appointments node
                userAppointmentsRef.push().setValue(appointmentData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Appointment Added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to add appointment", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete appointment button (this part would require logic to identify which appointment to delete)
        deleteAppointmentButton.setOnClickListener(v -> {
            // Delete appointment logic goes here
            // Example: userAppointmentsRef.child("appointmentId").removeValue();
            Toast.makeText(getActivity(), "Appointment Deleted", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
