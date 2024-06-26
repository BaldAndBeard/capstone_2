package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {


    private final JdbcTemplate jdbcTemplate;
    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // GETS THE USER ACCOUNT BY ITS ID FROM THE DATABASE IN THE ACCOUNT TABLE
    @Override
    public Transfer getTransferById(int id) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE transfer_id = ?";
        try {
            //RESULTS = ROW OF INFO, CONTAINING account_id, user_id, balance, from the account table
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                transfer = mapRowToTransfer(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getPendingTransfersByAccountID() {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE transfer_status_id = 1";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Transfer transfer = mapRowToTransfer(results);
                transfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }

    @Override
    public List<Transfer> getTransfersByAccountId(int accountId) {

        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE account_from = ? OR account_to = ?";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, accountId, accountId);
            while (rowSet.next()) {
                Transfer transfer = mapRowToTransfer(rowSet);
                transfers.add(transfer);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return transfers;
    }

    public Transfer createTransfer(Transfer transfer) {
        Transfer newTransfer = null;

        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES(?, ?, ?, ?, ?) RETURNING transfer_id;";
        int newTransferId = jdbcTemplate.queryForObject(sql, int.class, transfer.getTransferTypeId(), transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        newTransfer = getTransferById(newTransferId);

        return newTransfer;
    }


    public Transfer updateTransfer(Transfer transfer) {
        Transfer newTransfer = null;

        String sql = "UPDATE transfer " +
                "SET transfer_status_id = ? " +
                "WHERE transfer_id = ?;";
        int newTransferId = jdbcTemplate.update(sql, transfer.getTransferStatusId(), transfer.getTransferId());
        newTransfer = getTransferById(newTransferId);

        return newTransfer;
    }

    public void deleteTransferById(int accountId) {
        String sql = "DELETE FROM account WHERE account_id = ?";
        jdbcTemplate.update(sql, accountId);
    }

    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    }


}
