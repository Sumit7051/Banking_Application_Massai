package com.hdfc.Entities;

import com.hdfc.enums.AccountType;

import java.math.BigDecimal;

public class CurrentAccount  extends Account{

    public static final BigDecimal Interest_rate = BigDecimal.ZERO;
    public static final BigDecimal minimum_bal = BigDecimal.ZERO;

    public CurrentAccount(){
        super();
        setType(AccountType.CURRENT);
    }

    public CurrentAccount(String accoutNo, String customerId, BigDecimal balance) {
        super(accoutNo, customerId, AccountType.CURRENT, balance);
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
        return "Current_Account{" +
                "accoutNo='" + getAccoutNo() + '\'' +
                ", customerId='" + getCustomerId() + '\'' +
                ", type=" + getType() +
                ", balance=" + getBalance() +
                '}';
    }
}
