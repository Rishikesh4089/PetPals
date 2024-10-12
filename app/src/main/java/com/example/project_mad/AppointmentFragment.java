package com.example.project_mad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AppointmentFragment extends Fragment {

    private ListView appointmentListView;
    private AppointmentAdapter appointmentAdapter;
    private ArrayList<Appointment> appointmentList = new ArrayList<>();
    private DatabaseReference userAppointmentsRef;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_appointment_fragment, container, false);

        // Initialize Firebase references
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            userAppointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");
        }

        appointmentListView = view.findViewById(R.id.appointment_recycler_view);
        appointmentAdapter = new AppointmentAdapter(getContext(), appointmentList);
        appointmentListView.setAdapter(appointmentAdapter);

        // Load appointments from Firebase
        loadAppointments();

        FloatingActionButton fabAddAppointment = view.findViewById(R.id.fab_add_appointment);
        fabAddAppointment.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AddAppointmentFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadAppointments() {
        userAppointmentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.clear(); // Clear existing appointments
                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        appointmentList.add(appointment);
                    }
                }
                appointmentAdapter.notifyDataSetChanged(); // Notify adapter about data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load appointments.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
