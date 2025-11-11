package com.example.blackshoresbank.models;

public class WalletBalanceRequest {
    private String AccountNumber;

    public WalletBalanceRequest(String accountNumber) {
        this.AccountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }
}