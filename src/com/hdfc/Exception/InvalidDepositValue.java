package com.hdfc.Exception;

public class InvalidDepositValue extends RuntimeException {
    public InvalidDepositValue(String message) {
        super(message);
    }
}
