package com.example.project_mad;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("reply") // Change this according to the API response structure
    private String reply;

    public String getReply() {
        return reply; // Adjust according to your API response structure
    }
}
