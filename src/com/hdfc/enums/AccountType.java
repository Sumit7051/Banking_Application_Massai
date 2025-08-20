package com.hdfc.enums;

public enum AccountType {

    SAVINGS("SAVINGS Account"),
    CURRENT("CURRENT ACCOUNT");


    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName()
    {
        return displayName;
    }
    @Override
    public String toString()
    {
        return displayName;
    }
}
