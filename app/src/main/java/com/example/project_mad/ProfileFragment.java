package com.example.project_mad;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project_mad.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private EditText ownerName, ownerAge, address, phone, petName, petAge, petType, breed;
    private RadioGroup gender, petGender;
    private Button btnSaveProfile;
    private LinearLayout ProfileDetails;
    private DatabaseReference databaseReference;
    private SharedPreferences sharedPreferences;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        ownerName = view.findViewById(R.id.ownerName);
        ownerAge = view.findViewById(R.id.ownerAge);
        gender = view.findViewById(R.id.gender);
        address = view.findViewById(R.id.address);
        phone = view.findViewById(R.id.phone);
        petName = view.findViewById(R.id.petName);
        petAge = view.findViewById(R.id.petAge);
        petGender = view.findViewById(R.id.petGender);
        petType = view.findViewById(R.id.petType);
        breed = view.findViewById(R.id.breed);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        ProfileDetails = view.findViewById(R.id.ProfileDetails);

        // SharedPreferences setup
        sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", getContext().MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Retrieve or generate the userId
        userId = sharedPreferences.getString("userId", null);
        if (userId == null) {
            // Generate a new userId and save it to SharedPreferences
            userId = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("userId", userId).apply();
        }

        // Load existing profile details
        loadProfile();

        // Set onClickListener for Save button
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        return view;
    }

    private void loadProfile() {
        databaseReference.child("users").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                // Profile exists, populate the fields with existing data
                UserProfile profile = task.getResult().getValue(UserProfile.class);
                if (profile != null) {
                    ownerName.setText(profile.ownerName);
                    ownerAge.setText(profile.ownerAge);
                    address.setText(profile.address);
                    phone.setText(profile.phone);
                    petName.setText(profile.petName);
                    petAge.setText(profile.petAge);
                    petType.setText(profile.petType);
                    breed.setText(profile.breed);

                    // Set the gender selection in the RadioGroup
                    if (profile.gender.equals("Male")) {
                        gender.check(R.id.radioMale);
                    } else if (profile.gender.equals("Female")) {
                        gender.check(R.id.radioFemale);
                    }

                    // Set the pet gender selection in the RadioGroup
                    if (profile.petGender.equals("Male")) {
                        petGender.check(R.id.radioPetMale);
                    } else if (profile.petGender.equals("Female")) {
                        petGender.check(R.id.radioPetFemale);
                    }
                }
            } else {
                // Handle the case where profile does not exist
                Toast.makeText(getContext(), "No profile found. Please fill in your details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        String ownerNameVal = ownerName.getText().toString();
        String ownerAgeVal = ownerAge.getText().toString();
        String addressVal = address.getText().toString();
        String phoneVal = phone.getText().toString();
        String petNameVal = petName.getText().toString();
        String petAgeVal = petAge.getText().toString();
        String petTypeVal = petType.getText().toString();
        String breedVal = breed.getText().toString();

        // Get the selected gender from RadioGroup
        String genderVal = gender.getCheckedRadioButtonId() == R.id.radioMale ? "Male" : "Female";

        // Get the selected pet gender from RadioGroup
        String petGenderVal = petGender.getCheckedRadioButtonId() == R.id.radioPetMale ? "Male" : "Female";

        if (!ownerNameVal.isEmpty() && !ownerAgeVal.isEmpty() && !addressVal.isEmpty() && !phoneVal.isEmpty()) {
            // Create a map to hold the fields to update
            Map<String, Object> updates = new HashMap<>();
            updates.put("ownerName", ownerNameVal);
            updates.put("ownerAge", ownerAgeVal);
            updates.put("gender", genderVal);
            updates.put("address", addressVal);
            updates.put("phone", phoneVal);
            updates.put("petName", petNameVal);
            updates.put("petAge", petAgeVal);
            updates.put("petGender", petGenderVal);
            updates.put("petType", petTypeVal);
            updates.put("breed", breedVal);

            // Update only the provided fields without affecting others
            databaseReference.child("users").child(userId).updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                    Toast.makeText(getContext(), "Failed to update profile: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Model class for UserProfile will be needed here

}
