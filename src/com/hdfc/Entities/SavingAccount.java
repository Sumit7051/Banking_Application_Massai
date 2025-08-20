package com.hdfc.Entities;

import com.hdfc.enums.AccountType;

import java.math.BigDecimal;



public class SavingAccount  extends Account{
    
    public static final BigDecimal Interest_rate = new BigDecimal("4.5");
    public static final BigDecimal minimum_bal = new BigDecimal("1000");

    public SavingAccount() {
        super();
        setType(AccountType.SAVINGS);
    }

    public SavingAccount(String accoutNo, String customerId, BigDecimal balance) {
        super(accoutNo, customerId, AccountType.SAVINGS,balance);
    }

    @Override
    public BigDecimal getInterestRate() {

        return Interest_rate;
    }

    @Override
    public BigDecimal getMinimumBalance() {

        return minimum_bal;
    }
    @Override
    public String toString() {
        return "Savings_Account{" +
                "accoutNo='" + getAccoutNo() + '\'' +
                ", customerId='" + getCustomerId() + '\'' +
                ", type=" + getType() +
                ", balance=" + getBalance() +
                '}';
    }
}
