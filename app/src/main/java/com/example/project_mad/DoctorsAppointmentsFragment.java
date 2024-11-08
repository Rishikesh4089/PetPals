package com.example.project_mad;

import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_mad.Appointment;
import com.example.project_mad.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import java.util.Calendar;

public class DoctorsAppointmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private List<Appointment> appointmentList;
    private FirebaseAuth mAuth;
    private DatabaseReference appointmentsRef;
    private FloatingActionButton addAppointmentFab;
    private ViewGroup tooltipContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_appointment_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.appointment_recycler_view);
        addAppointmentFab = rootView.findViewById(R.id.add_appointment_fab);
        appointmentList = new ArrayList<>();
        adapter = new AppointmentAdapter(appointmentList, this::deleteAppointment);

        tooltipContainer = rootView.findViewById(R.id.tooltip_container);

        showTooltip();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            appointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");
            loadAndSortAppointments();
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        addAppointmentFab.setOnClickListener(v -> showAddAppointmentDialog());
        return rootView;
    }

    private void showTooltip() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View tooltipView = inflater.inflate(R.layout.tooltip_medical_profile, tooltipContainer, false);

        tooltipContainer.addView(tooltipView);
        tooltipContainer.setVisibility(View.VISIBLE);

        TextView tooltipMessage = tooltipView.findViewById(R.id.tooltip_message);

        // Set up "medical profile" as a clickable link within the tooltip text
        String tooltipText = "Set up your medical profile and add a link to open that page";
        SpannableString spannableString = new SpannableString(tooltipText);

        int start = tooltipText.indexOf("medical profile");
        int end = start + "medical profile".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Open MedicalProfileFragment when "medical profile" is clicked
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MedicalProfileFragment());
                transaction.addToBackStack(null); // Ensures back button returns here
                transaction.commit();
            }
        };

        // Set the clickable span and customize the appearance
        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tooltipMessage.setText(spannableString);
        tooltipMessage.setMovementMethod(LinkMovementMethod.getInstance());
        tooltipMessage.setLinkTextColor(ContextCompat.getColor(getContext(), R.color.blue));

        // Remove the tooltip after a delay (5-7 seconds)
        new Handler().postDelayed(() -> {
            tooltipContainer.setVisibility(View.GONE);
            tooltipContainer.removeView(tooltipView);
        }, 6000); // Display for 6 seconds
    }

    private String selectedDate = "";
    private String selectedTime = "";

    private void showAddAppointmentDialog() {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View dialogView = inflater.inflate(R.layout.add_appointment_fragment, null);

        EditText doctorNameInput = dialogView.findViewById(R.id.doctor_name_input);
        EditText appointmentReasonInput = dialogView.findViewById(R.id.appointment_reason_input);
        Spinner appointmentTypeSpinner = dialogView.findViewById(R.id.appointment_type_spinner);
        Button saveAppointmentButton = dialogView.findViewById(R.id.save_appointment_button);

        Button selectDateButton = dialogView.findViewById(R.id.select_date_button);
        Button selectTimeButton = dialogView.findViewById(R.id.select_time_button);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        selectDateButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedDate = selectedYear + "-" + String.format("%02d", selectedMonth + 1) + "-" + String.format("%02d", selectedDay);
                        selectDateButton.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        selectTimeButton.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    (view, selectedHour, selectedMinute) -> {
                        selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                        selectTimeButton.setText(selectedTime);
                    }, hour, minute, true);
            timePickerDialog.show();
        });

        saveAppointmentButton.setOnClickListener(v -> {
            String doctorName = doctorNameInput.getText().toString().trim();
            String reason = appointmentReasonInput.getText().toString().trim();
            String type = appointmentTypeSpinner.getSelectedItem().toString();

            if (doctorName.isEmpty() || reason.isEmpty() || selectedDate.isEmpty() || selectedTime.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Appointment appointment = new Appointment(doctorName, selectedDate, selectedTime, reason, type);
            appointmentsRef.push().setValue(appointment).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Appointment added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Failed to add appointment", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void loadAndSortAppointments() {
        appointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentList.clear();
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }

                Collections.sort(appointmentList, new Comparator<Appointment>() {
                    @Override
                    public int compare(Appointment a1, Appointment a2) {
                        Date dateTime1 = a1.getDateTime();
                        Date dateTime2 = a2.getDateTime();

                        if (dateTime1 == null || dateTime2 == null) {
                            return 0;
                        }
                        return dateTime1.compareTo(dateTime2);
                    }
                });

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteAppointment(Appointment appointment) {
        appointmentsRef.orderByChild("date").equalTo(appointment.getDate()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot appointmentSnapshot : snapshot.getChildren()) {
                    Appointment retrievedAppointment = appointmentSnapshot.getValue(Appointment.class);
                    if (retrievedAppointment != null &&
                            retrievedAppointment.getDate().equals(appointment.getDate()) &&
                            retrievedAppointment.getTime().equals(appointment.getTime())) {

                        appointmentSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Appointment deleted", Toast.LENGTH_SHORT).show();
                                appointmentList.remove(appointment);
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(getActivity(), "Failed to delete appointment", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to delete appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
