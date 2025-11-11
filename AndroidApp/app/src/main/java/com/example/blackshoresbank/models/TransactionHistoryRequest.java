package com.example.blackshoresbank.models;

public class TransactionHistoryRequest {
    private String AccountNumber;

    public TransactionHistoryRequest(String accountNumber) {
        this.AccountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return AccountNumber;
    }
}