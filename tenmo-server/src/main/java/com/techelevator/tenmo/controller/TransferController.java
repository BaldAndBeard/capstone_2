package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/transfer")
public class TransferController {

    private TransferDao transferDao;

    public TransferController(TransferDao transferDao) {
        this.transferDao = transferDao;
    }

        // Get a list of all accounts
        @RequestMapping(path = "", method = RequestMethod.GET)
        public List<Transfer> list(@RequestParam(defaultValue = "0") int account_id, @RequestParam(defaultValue = "") String transfer_status_type) {
            List<Transfer> listOfTransfers = new ArrayList<>();

            if (account_id != 0) {
                listOfTransfers = transferDao.getTransfersByAccountId(account_id);
            } else if (transfer_status_type.equals("Pending")) {
                listOfTransfers = (transferDao.getPendingTransfersByAccountID());
            }

            return listOfTransfers;
        }

        // Get a single transfer by it's ID
        // path declares where the method will pull it's data from
        // method declares what type of request is being used (GET, PUT, POST, DELETE)
        @RequestMapping(path = "/{id}", method = RequestMethod.GET)
        // @PathVariable grabs the part of the URL in brackets with the same name as the parameter (in this case "id")
        public Transfer get(@PathVariable int id) {
            // Assign value to a new instance of transfer using the output from getTransferById method
            Transfer transfer = transferDao.getTransferById(id);
            // If the transfer is null, return an error, otherwise return the new transfer.
            if (transfer == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer Not Found");
            } else {
                return transfer;
            }
        }


        // Create an transfer
        @ResponseStatus(HttpStatus.CREATED)
        @RequestMapping(path = "", method = RequestMethod.POST)
        public Transfer create(@Valid @RequestBody Transfer transfer) {
            return transferDao.createTransfer(transfer);
        }

        // Update an transfer selected by a chosen ID
        @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
        public Transfer update(@Valid @RequestBody Transfer transfer, @PathVariable int id) {
            // The id on the path takes precedence over the id in the request body, if any
            transfer.setTransferId(id);
            try {
                Transfer updateTransfer = transferDao.updateTransfer(transfer);
                return updateTransfer;
            } catch (DaoException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer Not Found");
            }
        }

        // Delete an transfer selected by a given ID
        @ResponseStatus(HttpStatus.NO_CONTENT)
        @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
        public void delete(@PathVariable int id) {
            transferDao.deleteTransferById(id);
        }

        @RequestMapping(path = "/whoami")
        public String whoAmI(Principal principal) {
            String name = principal.getName();
            return name;
        }





}
