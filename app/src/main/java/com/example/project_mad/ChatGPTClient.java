package com.example.project_mad;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class ChatGPTClient {
    private static final String BASE_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client;
    private final String apiKey;

    // Constructor with API key as a parameter
    public ChatGPTClient(String apiKey) {
        this.apiKey = "df"; // Set the API key from constructor parameter
        this.client = new OkHttpClient();
    }

    public void getChatResponse(String message, Callback callback) {
        try {
            // Create the JSON payload
            JSONObject json = new JSONObject();
            json.put("model", "gpt-3.5-turbo"); // Use the appropriate model

            // Prepare the messages array
            JSONArray messages = new JSONArray();
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.put(userMessage);
            json.put("messages", messages);

            // Create the request body
            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));

            // Build the request
            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            // Execute the request asynchronously
            client.newCall(request).enqueue(callback);
        } catch (JSONException e) {
            // Handle JSON exceptions
            e.printStackTrace();
            callback.onFailure(null, new IOException("Failed to create JSON payload: " + e.getMessage()));
        }
    }
}
