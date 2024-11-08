package com.example.project_mad;

public class HomeReminder {
    private String title;

    public HomeReminder(String title) {
        this.title = title;
    }

    public String getReminderText() {
        return title;
    }

    public void setReminderText(String title) {
        this.title = title;
    }
}
