package com.example.project_mad;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatbotFragment extends Fragment {
    private EditText inputMessage;
    private LinearLayout messageContainer;
    private ScrollView chatScrollView;
    private ProgressBar progressBar;
    private Button sendButton;

    private ChatGPTClient chatGPTClient;
    private List<JSONObject> messages; // List to hold message history

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);
        inputMessage = view.findViewById(R.id.input_message);
        messageContainer = view.findViewById(R.id.message_container);
        chatScrollView = view.findViewById(R.id.chat_scroll_view);
        sendButton = view.findViewById(R.id.send_button);
        progressBar = view.findViewById(R.id.progress_bar);

        // Initialize the ChatGPTClient with your API key
        String apiKey = "sk-5AHKF-UuqBinVqzcv8O-KyCAU5dkPpFi6H6SZ7bwkiT3BlbkFJztc3L_iVtMqBBw8qkEq2c4W1ZLwA9e0TQO2Ct2o5cA"; // Replace with your OpenAI API key
        chatGPTClient = new ChatGPTClient(apiKey);

        messages = new ArrayList<>(); // Initialize the message history

        sendButton.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void sendMessage() {
        String message = inputMessage.getText().toString().trim();
        if (!message.isEmpty()) {
            if (message.length() <= 500) { // Check length
                addMessage("User: " + message);
                inputMessage.setText("");
                progressBar.setVisibility(View.VISIBLE);
                sendButton.setEnabled(false); // Disable button

                // Create the user message JSON object
                try {
                    JSONObject userMessage = new JSONObject();
                    userMessage.put("role", "user");
                    userMessage.put("content", message);
                    messages.add(userMessage); // Add user message to the list

                    getChatResponse();
                } catch (JSONException e) {
                    Log.e("ChatbotFragment", "JSON creation error: " + e.getMessage());
                }
            } else {
                addMessage("Bot: Message is too long. Please shorten it.");
            }
        }
    }

    private void getChatResponse() {
        // Create the request JSON object
        try {
            JSONObject requestJson = new JSONObject();
            requestJson.put("model", "gpt-3.5-turbo-0125"); // Specify the model
            requestJson.put("messages", new JSONArray(messages)); // Add message history

            chatGPTClient.getChatResponse(requestJson.toString(), new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    // Make sure to run UI updates on the main thread
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        sendButton.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null) {
                            String responseBody;
                            try {
                                responseBody = response.body().string();
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                String reply = jsonResponse.getJSONArray("choices").getJSONObject(0)
                                        .getJSONObject("message").getString("content");
                                addMessage("Bot: " + reply);

                                // Add bot message to the history
                                JSONObject botMessage = new JSONObject();
                                botMessage.put("role", "assistant");
                                botMessage.put("content", reply);
                                messages.add(botMessage);
                            } catch (JSONException e) {
                                Log.e("ChatbotFragment", "JSON parsing error: " + e.getMessage());
                                addMessage("Bot: Error parsing response.");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Log.e("ChatbotFragment", "Error: " + response.code() + " - " + response.message());
                            addMessage("Bot: Sorry, there was an error processing your request.");
                        }
                        // Scroll to the bottom of the chat
                        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
                    });
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    // Make sure to run UI updates on the main thread
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        sendButton.setEnabled(true);
                        Log.e("ChatbotFragment", "Error: " + e.getMessage());
                        addMessage("Bot: Error: " + e.getMessage());
                        // Scroll to the bottom of the chat
                        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
                    });
                }
            });
        } catch (JSONException e) {
            Log.e("ChatbotFragment", "JSON creation error: " + e.getMessage());
        }
    }

    private void addMessage(String message) {
        TextView textView = new TextView(requireContext());
        textView.setText(message);
        messageContainer.addView(textView);
        // Optional: Add some padding for better spacing
        textView.setPadding(16, 8, 16, 8);
    }
}
