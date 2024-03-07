package com.techelevator.tenmo.model;

public class Transfer {

    private int transferId;

    private int transferTypeId;

    private int transferStatusId;

    private int accountFrom;

    private int accountTo;

    private double amount;

    public Transfer (int transferId, int transferTypeId, int transferStatusId, int accountFrom, int accountTo, double amount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }


    public int getTransferId() {
        return transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public double getAmount() {
        return amount;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }


    public void setAmount(double amount){
        this.amount = amount;
    }
}
