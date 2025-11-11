package com.example.blackshoresbank.models;

public class SendMoneyPrepareRequest {
    private String UserNumber;
    private String RecipientNumber;
    private String SendAmount;
    private String Note;

    public SendMoneyPrepareRequest(String userNumber, String recipientNumber,
                                   String sendAmount, String note) {
        this.UserNumber = userNumber;
        this.RecipientNumber = recipientNumber;
        this.SendAmount = sendAmount;
        this.Note = note;
    }

    public String getUserNumber() { return UserNumber; }
    public String getRecipientNumber() { return RecipientNumber; }
    public String getSendAmount() { return SendAmount; }
    public String getNote() { return Note; }
}