package com.example.project_mad;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText ownerName, ownerAge, address, phone, petName, petAge, petType, breed;
    private RadioGroup gender, petGender;
    private Button btnSaveProfile, buttonSelectImage;
    private ImageView petProfileImage;
    private LinearLayout ProfileDetails;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String userId;
    private Uri imageUri;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        petProfileImage = view.findViewById(R.id.petProfileImage);
        buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
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

        // Initialize FirebaseAuth and Firebase Database
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("profile_photos");

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Load profile data and image
        loadProfile();

        buttonSelectImage.setOnClickListener(v -> openFileChooser());
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            petProfileImage.setImageURI(imageUri);
        }
    }

    private void saveProfile() {
        // Save profile data to Firebase Realtime Database
        String ownerNameStr = ownerName.getText().toString();
        String ownerAgeStr = ownerAge.getText().toString();
        String addressStr = address.getText().toString();
        String phoneStr = phone.getText().toString();
        String petNameStr = petName.getText().toString();
        String petAgeStr = petAge.getText().toString();
        String petTypeStr = petType.getText().toString();
        String breedStr = breed.getText().toString();

        String genderStr = gender.getCheckedRadioButtonId() == R.id.radioMale ? "Male" : "Female";
        String petGenderStr = petGender.getCheckedRadioButtonId() == R.id.radioPetMale ? "Male" : "Female";

        // Create a profile object
        UserProfile profile = new UserProfile(ownerNameStr, ownerAgeStr, addressStr, phoneStr, genderStr,
                petNameStr, petTypeStr, petAgeStr, petGenderStr, breedStr, null);

        // Save profile data to the database
        databaseReference.child("users").child(userId).setValue(profile)
                .addOnSuccessListener(aVoid -> {
                    if (imageUri != null) {
                        // Upload the image to Firebase Storage
                        StorageReference fileReference = storageReference.child(userId + ".jpg");
                        fileReference.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                        // Save the image URL with the userId in the database
                                        databaseReference.child("users").child(userId).child("petImageUrl").setValue(uri.toString())
                                                .addOnSuccessListener(aVoid1 -> Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    });
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(getContext(), "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadProfile() {
        databaseReference.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserProfile profile = dataSnapshot.getValue(UserProfile.class);
                    if (profile != null) {
                        ownerName.setText(profile.ownerName);
                        ownerAge.setText(profile.ownerAge);
                        address.setText(profile.address);
                        phone.setText(profile.phone);
                        petName.setText(profile.petName);
                        petAge.setText(profile.petAge);
                        petType.setText(profile.petType);
                        breed.setText(profile.breed);

                        if (profile.gender.equals("Male")) {
                            gender.check(R.id.radioMale);
                        } else {
                            gender.check(R.id.radioFemale);
                        }

                        if (profile.petGender.equals("Male")) {
                            petGender.check(R.id.radioPetMale);
                        } else {
                            petGender.check(R.id.radioPetFemale);
                        }

                        // Load and display the profile photo
                        if (profile.petImageUrl != null) {
                            Glide.with(ProfileFragment.this).load(profile.petImageUrl).into(petProfileImage);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "No profile found. Please fill in your details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load profile: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
