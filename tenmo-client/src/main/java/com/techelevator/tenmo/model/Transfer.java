package com.techelevator.tenmo.model;

import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.UserService;

import java.math.BigDecimal;

public class Transfer {
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }



    public void setUserAccount(Account userAccount) {
        this.userAccount = userAccount;
    }

    private User currentUser;
    private Account userAccount;

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    private User recipient;

    private int transferId;

    private int transferTypeId;

    private int transferStatusId;

    private int accountFrom;

    private int accountTo;

    private BigDecimal amount;

    // THIS IS SUPER IMPORTANT
    public Transfer(){}

    public Transfer (int transferId, int transferTypeId, int transferStatusId, int accountFrom, int accountTo, BigDecimal amount) {
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

    public int getTransferStatusId(){
        return transferStatusId;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public BigDecimal getAmount() {
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


    public void setAmount(BigDecimal amount){
        this.amount = amount;
    }


//    @Override
//    public String toString() {
//
//        User receiver = recipient;
//        User sender = currentUser;
//
//        return "Transfer{" +
//                "id =" + transferId +
//                ", Sender ='" + receiver.getUsername() +
//                ", Receiver =" + sender.getUsername() +
//                ", Amount = " + amount +
//                ", Transfer Type = " + transferTypeId +
//                ", Transfer Status = " + transferStatusId +
//                '}';
//    }


}
