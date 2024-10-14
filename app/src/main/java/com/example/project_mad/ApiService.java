package com.example.project_mad;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    // The endpoint to get the response from the FAQ bot
    @POST("v1/faq/670ab44259126800072d9ee5/get") // Use the correct endpoint
    Call<ChatResponse> getResponse(@Header("Authorization") String authorization, @Body ChatRequest chatRequest);
}
