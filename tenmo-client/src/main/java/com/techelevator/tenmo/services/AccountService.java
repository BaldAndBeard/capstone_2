package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import org.springframework.web.client.RestTemplate;

public class AccountService {

    public static final String API_BASE_URL = "http://localhost:8080";
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken( String  authToken) {
        this.authToken = authToken;

    }

    public Account getAccount(int id) {
        Account account = null;
        return  account;

    }
    public Account getAccountbyUserID( int userID) {
        Account accountByUserID = null;
        return accountByUserID;
    }
    public Account updateAccount(int accountId, Account accountToUpdate ) {
        Account updatedAccount = null;
        return updatedAccount;
    }
    public Account createAccount(Account newAccount) {
        Account createdAccount = null;
        return createdAccount;

    }
    public void  deleteAccount( int accountID) {

    }


}
