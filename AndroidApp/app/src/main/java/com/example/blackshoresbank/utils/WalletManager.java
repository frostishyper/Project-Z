package com.example.blackshoresbank.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.blackshoresbank.models.WalletBalanceRequest;
import com.example.blackshoresbank.models.WalletBalanceResponse;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletManager {

    public interface WalletBalanceCallback {
        void onSuccess(String balance);
        void onError(String error);
    }

    public static void fetchBalance(Context context, String accountNumber, WalletBalanceCallback callback) {
        WalletBalanceRequest request = new WalletBalanceRequest(accountNumber);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<WalletBalanceResponse> call = apiService.GetWalletBalance(request);

        call.enqueue(new Callback<WalletBalanceResponse>() {
            @Override
            public void onResponse(Call<WalletBalanceResponse> call, Response<WalletBalanceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WalletBalanceResponse result = response.body();

                    if (result.isSuccess()) {
                        callback.onSuccess(result.getBalance());
                    } else {
                        callback.onError(result.getError());
                    }
                } else {
                    callback.onError("Failed to fetch balance");
                }
            }

            @Override
            public void onFailure(Call<WalletBalanceResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}