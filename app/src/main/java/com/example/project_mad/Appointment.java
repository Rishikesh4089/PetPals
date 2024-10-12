package com.example.project_mad;

public class Appointment {
    private String title;
    private String doctorName;
    private String reason;
    private String date;
    private String time;

    // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    public Appointment() {}

    public Appointment(String title, String doctorName, String reason, String date, String time) {
        this.title = title;
        this.doctorName = doctorName;
        this.reason = reason;
        this.date = date;
        this.time = time;
    }

    public String getTitle() { return title; }
    public String getDoctorName() { return doctorName; }
    public String getReason() { return reason; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}
