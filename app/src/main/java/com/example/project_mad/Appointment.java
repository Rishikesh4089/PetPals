package com.example.project_mad;

// Appointment.java

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment {
    private String doctorName;
    private String date;  // Format: yyyy-MM-dd
    private String time;  // Format: HH:mm
    private String reason;
    private String type;

    public Appointment() {}

    public Appointment(String doctorName, String date, String time, String reason, String type) {
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.type = type;
    }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Method to parse and return the Date object
    public Date getDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            return format.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
