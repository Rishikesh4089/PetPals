package com.example.project_mad;

public class Appointment {

    private String doctorName;
    private String date;
    private String time;
    private String reason;
    private String type;

    public Appointment(String doctorName, String date, String time, String reason, String type) {
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.type = type;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }

    public String getType() {
        return type;
    }
}
