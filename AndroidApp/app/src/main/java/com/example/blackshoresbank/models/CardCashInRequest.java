package com.example.blackshoresbank.models;

// Request model for Card Cash-In API call.
// Sends all necessary card and transaction details to the backend.
public class CardCashInRequest {
    private String AccountNumber;
    private String CardType;
    private String CardNumberInput;
    private String ExpiryMonthInput;
    private String ExpiryYearInput;
    private String CVVInput;
    private double CashInAmountInput;

    // Constructor
    public CardCashInRequest(String accountNumber, String cardType, String cardNumberInput,
                             String expiryMonthInput, String expiryYearInput,
                             String cvvInput, double cashInAmountInput) {
        this.AccountNumber = accountNumber;
        this.CardType = cardType;
        this.CardNumberInput = cardNumberInput;
        this.ExpiryMonthInput = expiryMonthInput;
        this.ExpiryYearInput = expiryYearInput;
        this.CVVInput = cvvInput;
        this.CashInAmountInput = cashInAmountInput;
    }

    // Getters
    public String getAccountNumber() { return AccountNumber; }
    public String getCardType() { return CardType; }
    public String getCardNumberInput() { return CardNumberInput; }
    public String getExpiryMonthInput() { return ExpiryMonthInput; }
    public String getExpiryYearInput() { return ExpiryYearInput; }
    public String getCVVInput() { return CVVInput; }
    public double getCashInAmountInput() { return CashInAmountInput; }

}
