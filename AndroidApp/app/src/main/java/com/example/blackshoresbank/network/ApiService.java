package com.example.blackshoresbank.network;

// Import Request & Response Models Here
import com.example.blackshoresbank.models.LoginRequest;
import com.example.blackshoresbank.models.LoginResponse;
import com.example.blackshoresbank.models.RegisterRequest;
import com.example.blackshoresbank.models.RegisterResponse;

// Retrofit Protocols & Procedures
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/register")
    Call<RegisterResponse> RegisterAccount(@Body RegisterRequest request);

    @POST("api/login")
    Call<LoginResponse> LoginAccount(@Body LoginRequest request);

    // Future routes are to be added here
}