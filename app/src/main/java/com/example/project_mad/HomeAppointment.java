package com.example.project_mad;

public class HomeAppointment {
    private String doctorName;
    private String reason;

    public HomeAppointment(String doctorName, String reason) {
        this.doctorName = doctorName;
        this.reason = reason;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
