package com.hdfc.Entities;

import com.hdfc.Exception.InsufficientBalance;
import com.hdfc.Exception.InvalidDepositValue;
import com.hdfc.enums.AccountType;
import java.math.BigDecimal;
import java.util.Objects;

public abstract class Account {
    private String accoutNo;
    private String customerId;
    private AccountType type;
    private BigDecimal balance;

    public Account() {
    }

    public Account(String accoutNo, String customerId, AccountType type, BigDecimal balance) {
        this.accoutNo = accoutNo;
        this.customerId = customerId;
        this.type = type;
        this.balance = balance;
    }

    public String getAccoutNo() {
        return accoutNo;
    }

    public void setAccoutNo(String accoutNo) {
        this.accoutNo = accoutNo;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accoutNo='" + accoutNo + '\'' +
                ", customerId='" + customerId + '\'' +
                ", type=" + type +
                ", balance=" + balance +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (getClass() != obj.getClass()) {
            return false;
        }
        Account acc = (Account) obj;
        return Objects.equals(accoutNo,acc.accoutNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accoutNo);
    }

    public synchronized void deposit(BigDecimal amount) throws InvalidDepositValue {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositValue("Deposit Value Should be Greater than Zero");
        }
        this.balance = this.balance.add(amount);
    }

    public synchronized void withdraw(BigDecimal amount) throws InsufficientBalance {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalance("Insufficient Balance");
        }
        this.balance = this.balance.subtract(amount);
    }

    public abstract BigDecimal getInterestRate();
    public abstract BigDecimal getMinimumBalance();

    public BigDecimal calculateInterest() {
        return balance.multiply(getInterestRate().divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP));
    }
}
