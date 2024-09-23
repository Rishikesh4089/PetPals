package com.example.project_mad;

public class Appointment {
    private String doctorName;
    private String reason;
    private String date;
    private String time;

    public Appointment(String doctorName, String reason, String date, String time) {
        this.doctorName = doctorName;
        this.reason = reason;
        this.date = date;
        this.time = time;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getReason() {
        return reason;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
