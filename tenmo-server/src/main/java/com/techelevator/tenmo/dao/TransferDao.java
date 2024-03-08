package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {


        List<Transfer> getTransfers();

        Transfer getTransferById(int id);

        Transfer getTransferByUserId(int userId);

        Transfer createTransfer(Transfer transfer);

        Transfer updateTransfer(Transfer transfer);

        void deleteTransferById(int accountId);


       // Transfer getAllPendingTransfers(String transferStatusType);
}
