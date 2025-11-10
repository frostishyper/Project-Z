package com.example.blackshoresbank.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.blackshoresbank.models.TransactionHistoryRequest;
import com.example.blackshoresbank.models.TransactionHistoryResponse;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionManager {

    public interface TransactionCallback {
        void onSuccess(List<TransactionHistoryResponse.Transaction> transactions);
        void onError(String error);
    }

    private static Handler refreshHandler;
    private static Runnable refreshRunnable;

    // Fetch transactions once
    public static void fetchTransactions(Context context, String accountNumber, TransactionCallback callback) {
        TransactionHistoryRequest request = new TransactionHistoryRequest(accountNumber);
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<TransactionHistoryResponse> call = apiService.GetTransactionHistory(request);

        call.enqueue(new Callback<TransactionHistoryResponse>() {
            @Override
            public void onResponse(Call<TransactionHistoryResponse> call, Response<TransactionHistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TransactionHistoryResponse result = response.body();

                    if (result.isSuccess()) {
                        callback.onSuccess(result.getTransactions());
                    } else {
                        callback.onError(result.getError());
                    }
                } else {
                    callback.onError("Failed to fetch transactions");
                }
            }

            @Override
            public void onFailure(Call<TransactionHistoryResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    // Auto-refresh transactions at interval (in milliseconds)
    public static void startAutoRefresh(Context context, String accountNumber,
                                        TransactionCallback callback, long intervalMs) {
        stopAutoRefresh(); // Stop any existing refresh

        refreshHandler = new Handler(Looper.getMainLooper());
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                fetchTransactions(context, accountNumber, callback);
                refreshHandler.postDelayed(this, intervalMs);
            }
        };

        // Initial fetch
        refreshHandler.post(refreshRunnable);
    }

    // Stop auto-refresh (call in onPause/onDestroy)
    public static void stopAutoRefresh() {
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }
}