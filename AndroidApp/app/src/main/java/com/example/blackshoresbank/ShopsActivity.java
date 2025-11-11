package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.blackshoresbank.models.GetShopRequest;
import com.example.blackshoresbank.models.GetShopResponse;
import com.example.blackshoresbank.network.ApiService;
import com.example.blackshoresbank.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shops);

        ImageView backBtn = findViewById(R.id.button_back);
        backBtn.setOnClickListener(v -> finish());

        LinearLayout steamItem = findViewById(R.id.item_partner_steam);
        LinearLayout wuwaItem = findViewById(R.id.item_partner_wuwa);

        steamItem.setOnClickListener(v -> fetchShopData("Valve Corp", SteamShopActivity.class));
        wuwaItem.setOnClickListener(v -> fetchShopData("Kuro Games", WuwaShopActivity.class));
    }

    private void fetchShopData(String merchantName, Class<?> targetActivity) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);


        GetShopRequest request = new GetShopRequest(merchantName);

        Call<GetShopResponse> call = apiService.getShopListings(request);

        call.enqueue(new Callback<GetShopResponse>() {
            @Override
            public void onResponse(Call<GetShopResponse> call, Response<GetShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GetShopResponse result = response.body();

                    if (result.isSuccess()) {
                        Intent intent = new Intent(ShopsActivity.this, targetActivity);
                        // Pass the whole JSON string to the next activity
                        intent.putExtra("shop_data", new com.google.gson.Gson().toJson(result));
                        startActivity(intent);
                    } else {
                        Toast.makeText(ShopsActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle non-2xx responses (like 404, 500)
                    Toast.makeText(ShopsActivity.this, "Failed to load shop", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetShopResponse> call, Throwable t) {
                // Handle network errors (no connection, timeout)
                Toast.makeText(ShopsActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}