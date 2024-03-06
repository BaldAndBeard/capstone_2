package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.web.client.RestTemplate;

public class TransferService {

    public static final String API_BASE_URL = "http://localhost:8080";
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken( String  authToken) {
        this.authToken = authToken;

    }

   public Transfer getTransfer(int id) {
        Transfer transfer = null;

        return transfer;

    }
    public Transfer[] getAllTransfersByUserID(int userId) {
        Transfer[] transfers = null;

        return transfers;
    }
    public Transfer requestTransfer(int requestUserID, double amount){
        Transfer requestedTransfer = null;

        return requestedTransfer;
    }
     public Transfer[] getAllPendingTransfers() {
        Transfer[] allPendingTransfers = null;

        return allPendingTransfers;
     }
}
