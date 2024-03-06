package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {

    public static final String API_BASE_URL = "http://localhost:8080/Account/";
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken( String  authToken) {
        this.authToken = authToken;

    }

    public Account getAccount(int id) {
        Account account = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + id, HttpMethod.GET, makeAuthEntity(), Account.class);
            account = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return  account;

    }
    // Define the url path for finding by userID
    public Account getAccountbyUserID( int userID) {
        Account accountByUserID = null;

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL + "?user_id=" + userID, HttpMethod.GET, makeAuthEntity(), Account.class);
            accountByUserID = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return accountByUserID;
    }
    public Account updateAccount(Account accountToUpdate ) {
        Account updatedAccount = null;
        HttpEntity<Account> entity = makeAccountEntity(accountToUpdate);

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL, HttpMethod.PUT, entity, Account.class);
            updatedAccount = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return updatedAccount;
    }
    public Account createAccount(Account newAccount) {
        Account createdAccount = null;
        HttpEntity<Account> entity = makeAccountEntity(newAccount);

        try {
            ResponseEntity<Account> response = restTemplate.exchange(API_BASE_URL, HttpMethod.POST, entity, Account.class);
            createdAccount = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return createdAccount;

    }
    public void  deleteAccount( int accountID) {
        try {
            restTemplate.delete(API_BASE_URL + accountID, HttpMethod.DELETE, makeAuthEntity(), void.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(account, headers);
    }


}
