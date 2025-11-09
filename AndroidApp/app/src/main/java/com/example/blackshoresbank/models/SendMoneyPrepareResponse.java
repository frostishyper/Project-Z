package com.example.blackshoresbank.models;

public class SendMoneyPrepareResponse {
    private boolean success;
    private String recipientNumber;
    private String recipientUsername;
    private String transferAmount;
    private String transferFee;
    private String totalAmount;
    private String note;
    private String projectedBalance;
    private String error;
    private String currentBalance;
    private String required;

    public boolean isSuccess() { return success; }
    public String getRecipientNumber() { return recipientNumber; }
    public String getRecipientUsername() { return recipientUsername; }
    public String getTransferAmount() { return transferAmount; }
    public String getTransferFee() { return transferFee; }
    public String getTotalAmount() { return totalAmount; }
    public String getNote() { return note; }
    public String getProjectedBalance() { return projectedBalance; }
    public String getError() { return error; }
    public String getCurrentBalance() { return currentBalance; }
    public String getRequired() { return required; }
}