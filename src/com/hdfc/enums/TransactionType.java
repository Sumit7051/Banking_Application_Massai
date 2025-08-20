package com.hdfc.enums;

public enum TransactionType {
    DEPOSIT("DEPOSIT"),
    WITHDRAW("WITHDRAW"),
    TRANSFER("Transfer");
    private final String displayName;

    TransactionType(String displayName)
    {
        this.displayName = displayName;
    }

    public String getDisplayName()
    {
        return displayName;
    }
    @Override
    public String toString()
    {
        return  displayName;
    }
}
