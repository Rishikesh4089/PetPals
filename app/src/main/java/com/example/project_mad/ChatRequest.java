package com.example.project_mad;

import com.google.gson.Gson;

public class ChatRequest {
    private String message;

    public ChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
