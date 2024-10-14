package com.example.project_mad;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class DoctorsAppointmentsFragment extends Fragment {

    private ArrayList<Appointment> appointmentList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private FloatingActionButton addAppointmentFab;
    private PopupWindow tooltipWindow;

    private DatabaseReference appointmentsRef; // Firebase Database reference
    private FirebaseAuth mAuth; // FirebaseAuth instance

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.activity_appointment_fragment, container, false);

        // Show tooltip for medical profile
        rootView.post(this::showTooltip);

        recyclerView = rootView.findViewById(R.id.appointment_recycler_view);
        addAppointmentFab = rootView.findViewById(R.id.add_appointment_fab);

        // Set up Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get the current logged-in user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Set up reference to the appointments node for the logged-in user
            String userId = currentUser.getUid();
            appointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Set up RecyclerView with AppointmentAdapter
        adapter = new AppointmentAdapter(appointmentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        // Floating Action Button Click Listener
        addAppointmentFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddAppointmentDialog();
            }
        });

        return rootView;
    }

    // Method to show Tooltip
    private void showTooltip() {
        // Inflate the tooltip layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View tooltipView = inflater.inflate(R.layout.tooltip_medical_profile, null);

        // Create the popup window
        tooltipWindow = new PopupWindow(tooltipView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        // Set the location of the tooltip (Top Left)
        tooltipWindow.showAtLocation(getView(), Gravity.TOP | Gravity.RIGHT, 50, 100);

        // Set up the tooltip message with clickable and underlined text
        TextView tooltipMessage = tooltipView.findViewById(R.id.tooltip_message);

        // Full message text
        String fullMessage = "Set up your medical profile and add a link to open that page.";

        // Create a SpannableString
        SpannableString spannableString = new SpannableString(fullMessage);

        // Define the clickable span for "medical profile"
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Open Medical Profile Fragment when "medical profile" is clicked
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MedicalProfileFragment())
                        .addToBackStack(null)
                        .commit();

                // Close the tooltip
                tooltipWindow.dismiss();
            }
        };

        // Find the index of the words "medical profile" in the message
        int start = fullMessage.indexOf("medical profile");
        int end = start + "medical profile".length();

        // Apply the clickable and underline spans
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the SpannableString to the TextView
        tooltipMessage.setText(spannableString);
        tooltipMessage.setMovementMethod(LinkMovementMethod.getInstance()); // Make links clickable

        // Close button action: Dismiss the tooltip
        Button closeButton = tooltipView.findViewById(R.id.tooltip_close_button);
        closeButton.setOnClickListener(v -> tooltipWindow.dismiss());
    }

    // Method to show the Add Appointment Dialog
    private void showAddAppointmentDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_appointment_fragment, null);
        dialogBuilder.setView(dialogView);

        EditText doctorNameInput = dialogView.findViewById(R.id.doctor_name_input);
        EditText reasonInput = dialogView.findViewById(R.id.appointment_reason_input);
        Spinner appointmentTypeSpinner = dialogView.findViewById(R.id.appointment_type_spinner);
        Button selectDateButton = dialogView.findViewById(R.id.select_date_button);
        Button selectTimeButton = dialogView.findViewById(R.id.select_time_button);
        Button saveButton = dialogView.findViewById(R.id.save_appointment_button);

        final Calendar calendar = Calendar.getInstance();
        final int[] selectedYear = new int[1];
        final int[] selectedMonth = new int[1];
        final int[] selectedDay = new int[1];
        final int[] selectedHour = new int[1];
        final int[] selectedMinute = new int[1];

        // Date picker dialog
        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        (view, year, month, dayOfMonth) -> {
                            selectedYear[0] = year;
                            selectedMonth[0] = month;
                            selectedDay[0] = dayOfMonth;
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        // Time picker dialog
        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                        (view, hourOfDay, minute) -> {
                            selectedHour[0] = hourOfDay;
                            selectedMinute[0] = minute;
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        });

        // Save appointment
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String doctorName = doctorNameInput.getText().toString();
                String reason = reasonInput.getText().toString();
                String type = appointmentTypeSpinner.getSelectedItem().toString();
                String date = selectedDay[0] + "/" + (selectedMonth[0] + 1) + "/" + selectedYear[0];
                String time = selectedHour[0] + ":" + selectedMinute[0];

                // Add new appointment to list
                Appointment newAppointment = new Appointment(doctorName, date, time, reason, type);
                appointmentList.add(newAppointment);
                adapter.notifyDataSetChanged();

                // Save the appointment to Firebase
                if (appointmentsRef != null) {
                    String appointmentId = appointmentsRef.push().getKey(); // Generate unique ID
                    if (appointmentId != null) {
                        appointmentsRef.child(appointmentId).setValue(newAppointment)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getActivity(), "Appointment added to Firebase", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getActivity(), "Failed to add appointment", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }
}
