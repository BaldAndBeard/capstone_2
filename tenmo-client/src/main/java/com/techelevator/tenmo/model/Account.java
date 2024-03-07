package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Account {

    // THIS IS SUPER IMPORTANT
    public Account(){}
    public Account(int id, int userId, BigDecimal balance){
        this.id = id;
        this.userId = userId;
        this.balance = balance;

    }

    private int id;


    private int userId;


    private BigDecimal balance;


    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBalance(BigDecimal balance){
        this.balance = balance;
    }

}
