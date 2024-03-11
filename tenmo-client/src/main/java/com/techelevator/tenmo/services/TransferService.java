package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
// Transfer Service Communicates with API in order to het data
public class TransferService {

    public static final String API_BASE_URL = "http://localhost:8080/transfer";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken( String  authToken) {
        this.authToken = authToken;
    }

    public Transfer getTransfer(int id) {
        Transfer transfer = null;

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();

        }catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfer;
    }

    public Transfer [] getAllTransfersByAccountID(int accountId) {
        Transfer[] transfers = new Transfer[]{};

        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "?account_id=" + accountId,HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return transfers;
    }

    public Transfer createTransfer(Transfer transferToAdd) {
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

     public Transfer[] getAllPendingTransfers() {
        Transfer[] allPendingTransfers = null;

         try {
             ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL + "?transfer_status_id=1", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
             allPendingTransfers = response.getBody();
         } catch (RestClientResponseException | ResourceAccessException e) {
             BasicLogger.log(e.getMessage());
         }


        return allPendingTransfers;
     }

    public Transfer updateTransfer(Transfer transferToUpdate) {

        Transfer updatedTransfer = null;

        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + "/" + transferToUpdate.getTransferId(), HttpMethod.PUT, makeTransferEntity(transferToUpdate), Transfer.class);
            updatedTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }

        return updatedTransfer;
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
