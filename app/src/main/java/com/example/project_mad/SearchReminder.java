package com.example.project_mad;

public class SearchReminder {
    private String title;
    private String description;
    private String date;
    private String time;
    private String status;

    // Constructor, Getters and Setters
    public SearchReminder(String title, String description, String date, String time, String status) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Other getters and setters if needed
}
