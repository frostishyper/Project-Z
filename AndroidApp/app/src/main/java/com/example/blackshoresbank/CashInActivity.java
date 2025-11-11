package com.example.blackshoresbank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class CashInActivity extends BaseActivity {

    private ImageView buttonBack;
    private LinearLayout itemPartnerVisa;
    private LinearLayout itemPartnerMastercard;
    private LinearLayout itemPartnerAmex;
    private LinearLayout itemPartnerJcb;
    private LinearLayout itemPartnerDiscover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cash_in);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        buttonBack = findViewById(R.id.button_back);
        itemPartnerVisa = findViewById(R.id.item_partner_visa);
        itemPartnerMastercard = findViewById(R.id.item_partner_mastercard);
        itemPartnerAmex = findViewById(R.id.item_partner_amex);
        itemPartnerJcb = findViewById(R.id.item_partner_jcb);
        itemPartnerDiscover = findViewById(R.id.item_partner_discover);
    }

    private void setupClickListeners() {
        // Back button returns to previous page
        buttonBack.setOnClickListener(v -> finish());

        // Partner click listeners
        itemPartnerVisa.setOnClickListener(v -> openCardDetails("Visa"));
        itemPartnerMastercard.setOnClickListener(v -> openCardDetails("MasterCard"));
        itemPartnerAmex.setOnClickListener(v -> openCardDetails("American Express"));
        itemPartnerJcb.setOnClickListener(v -> openCardDetails("Japan Credit Bureau"));
        itemPartnerDiscover.setOnClickListener(v -> openCardDetails("Discover"));
    }

    private void openCardDetails(String cardType) {
        Intent intent = new Intent(this, CardCashInActivity.class);
        intent.putExtra("CARD_TYPE", cardType);
        startActivity(intent);
    }
}
