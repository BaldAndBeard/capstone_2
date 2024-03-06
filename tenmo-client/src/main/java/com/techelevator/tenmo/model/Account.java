package com.techelevator.tenmo.model;

public class Account {

    private int id;


    private int userId;


    private double balance;


    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setBalance(double balance){
        this.balance = balance;
    }

}
