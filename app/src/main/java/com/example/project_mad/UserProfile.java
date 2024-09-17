package com.example.project_mad;

public class UserProfile {
    public String ownerName;
    public String ownerAge;
    public String gender;
    public String address;
    public String phone;
    public String petName;
    public String petAge;
    public String petGender;
    public String petType;
    public String breed;
    public String petImageUrl;  // Add this field to store the image URL

    // Default constructor
    public UserProfile() {
    }

    // Parameterized constructor
    public UserProfile(String ownerName, String ownerAge, String address, String phone, String gender,
                       String petName, String petType, String petAge, String petGender, String breed,
                       String petImageUrl) {
        this.ownerName = ownerName;
        this.ownerAge = ownerAge;
        this.address = address;
        this.phone = phone;
        this.gender = gender;
        this.petName = petName;
        this.petType = petType;
        this.petAge = petAge;
        this.petGender = petGender;
        this.breed = breed;
        this.petImageUrl = petImageUrl;  // Set the image URL
    }
}
