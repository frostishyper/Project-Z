package com.example.blackshoresbank.models;

public class SendMoneyCommitResponse {
    private boolean success;
    private String message;
    private String referenceId;
    private String newBalance;
    private String amountSent;
    private String feePaid;
    private String error;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getReferenceId() { return referenceId; }
    public String getNewBalance() { return newBalance; }
    public String getAmountSent() { return amountSent; }
    public String getFeePaid() { return feePaid; }
    public String getError() { return error; }
}