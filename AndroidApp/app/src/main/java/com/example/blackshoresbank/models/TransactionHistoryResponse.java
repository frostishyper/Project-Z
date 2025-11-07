package com.example.blackshoresbank.models;

import java.util.List;

public class TransactionHistoryResponse {
    private boolean success;
    private int transactionCount;
    private List<Transaction> transactions;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public String getError() {
        return error;
    }

    public static class Transaction {
        private int transactionId;
        private String referenceId;
        private String direction;
        private String senderType;
        private String senderNumber;
        private String recipientType;
        private String recipientNumber;
        private String merchantName;
        private String amount;
        private String fee;
        private String note;
        private String date;

        public int getTransactionId() {
            return transactionId;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public String getDirection() {
            return direction;
        }

        public String getSenderType() {
            return senderType;
        }

        public String getSenderNumber() {
            return senderNumber;
        }

        public String getRecipientType() {
            return recipientType;
        }

        public String getRecipientNumber() {
            return recipientNumber;
        }

        public String getMerchantName() {
            return merchantName;
        }

        public String getAmount() {
            return amount;
        }

        public String getFee() {
            return fee;
        }

        public String getNote() {
            return note;
        }

        public String getDate() {
            return date;
        }
    }
}