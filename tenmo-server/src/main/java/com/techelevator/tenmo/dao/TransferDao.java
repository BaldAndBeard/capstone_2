package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {


        List<Transfer> getPendingTransfersByAccountID();

        Transfer getTransferById(int id);

        List<Transfer> getTransfersByAccountId(int accountId);

        Transfer createTransfer(Transfer transfer);

        Transfer updateTransfer(Transfer transfer);

        void deleteTransferById(int accountId);


       // Transfer getAllPendingTransfers(String transferStatusType);
}
