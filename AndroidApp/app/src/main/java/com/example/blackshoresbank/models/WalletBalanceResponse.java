package com.example.blackshoresbank.models;

public class WalletBalanceResponse {
    private boolean success;
    private String balance;
    private String accountHolder;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public String getBalance() {
        return balance;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public String getError() {
        return error;
    }
}