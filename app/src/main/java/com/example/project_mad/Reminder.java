package com.example.project_mad;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Reminder {
    private String id; // Unique identifier for the reminder
    private String title;
    private String description;
    private String status;

    // Default constructor required for calls to DataSnapshot.getValue(Reminder.class)
    public Reminder() {
    }

    public Reminder(String id, String title, String description, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
