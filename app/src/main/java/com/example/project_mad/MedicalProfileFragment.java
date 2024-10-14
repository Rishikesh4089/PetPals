package com.example.project_mad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class MedicalProfileFragment extends Fragment {

    private EditText vaccinationInput, medicalHistoryInput, allergiesInput, medicationsInput, behavioralTraitsInput;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_medical_profile, container, false);

        // Initialize Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize UI elements
        vaccinationInput = rootView.findViewById(R.id.vaccination_list_input);
        medicalHistoryInput = rootView.findViewById(R.id.medical_history_input);
        allergiesInput = rootView.findViewById(R.id.allergies_input);
        medicationsInput = rootView.findViewById(R.id.medications_input);
        behavioralTraitsInput = rootView.findViewById(R.id.behavioral_traits_input);
        saveButton = rootView.findViewById(R.id.save_medical_profile_button);

        // Save button listener
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMedicalProfile();
            }
        });

        return rootView;
    }

    private void saveMedicalProfile() {
        String userId = currentUser.getUid();

        String vaccinations = vaccinationInput.getText().toString();
        String medicalHistory = medicalHistoryInput.getText().toString();
        String allergies = allergiesInput.getText().toString();
        String medications = medicationsInput.getText().toString();
        String behavioralTraits = behavioralTraitsInput.getText().toString();

        // Create a map to store the medical profile data
        Map<String, Object> medicalProfile = new HashMap<>();
        medicalProfile.put("vaccinations", vaccinations);
        medicalProfile.put("medical_history", medicalHistory);
        medicalProfile.put("allergies", allergies);
        medicalProfile.put("medications", medications);
        medicalProfile.put("behavioral_traits", behavioralTraits);

        // Update the database under the 'medical_profile' node
        databaseReference.child("users").child(userId).child("medical_profile").updateChildren(medicalProfile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Medical Profile updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to update Medical Profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
