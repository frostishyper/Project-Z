package com.example.blackshoresbank.network;

// Import Request & Response Models Here
import com.example.blackshoresbank.models.LoginRequest;
import com.example.blackshoresbank.models.LoginResponse;
import com.example.blackshoresbank.models.RegisterRequest;
import com.example.blackshoresbank.models.RegisterResponse;
import com.example.blackshoresbank.models.WalletBalanceRequest;
import com.example.blackshoresbank.models.WalletBalanceResponse;
import com.example.blackshoresbank.models.CardCashInRequest;
import com.example.blackshoresbank.models.CardCashInResponse;

// Retrofit Protocols & Procedures
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/register")
    Call<RegisterResponse> RegisterAccount(@Body RegisterRequest request);

    @POST("api/login")
    Call<LoginResponse> LoginAccount(@Body LoginRequest request);

    @POST("api/wallet")
    Call<WalletBalanceResponse> GetWalletBalance(@Body WalletBalanceRequest request);

    @POST("api/CardCashIn")
    Call<CardCashInResponse> CardCashIn(@Body CardCashInRequest request);
}
