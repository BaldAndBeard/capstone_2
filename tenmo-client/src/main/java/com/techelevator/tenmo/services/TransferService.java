package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class TransferService {

    public static final String API_BASE_URL = "http://localhost:8080/transfer";
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken( String  authToken) {
        this.authToken = authToken;

    }

   public Transfer getTransfer(int id) {
        Transfer transfer = null;

       try {
           ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class);
           transfer = response.getBody();
       } catch (RestClientResponseException | ResourceAccessException e) {
           BasicLogger.log(e.getMessage());
       }

        return transfer;

    }
    public Transfer[] getAllTransfersByUserID(int userId) {
        Transfer[] transfers = null;

        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "/" + userId, HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfers;
    }

    public Transfer addTransfer(Transfer transferToAdd) {
        Transfer newTransfer = null;
        HttpEntity<Transfer> entity = makeTransferEntity(transferToAdd);

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL, HttpMethod.POST, entity, Transfer.class);
            newTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return newTransfer;
    }

    public Transfer requestTransfer(Transfer transferToRequest){
        Transfer requestedTransfer = null;
        HttpEntity<Transfer> entity = makeTransferEntity(transferToRequest);

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL, HttpMethod.POST, entity, Transfer.class);
            requestedTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return requestedTransfer;
    }
     public Transfer[] getAllPendingTransfers() {
        Transfer[] allPendingTransfers = null;

         try {
             ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "?transfer_status='Pending'", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
             allPendingTransfers = response.getBody();
         } catch (RestClientResponseException | ResourceAccessException e) {
             BasicLogger.log(e.getMessage());
         }


        return allPendingTransfers;
     }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

}
