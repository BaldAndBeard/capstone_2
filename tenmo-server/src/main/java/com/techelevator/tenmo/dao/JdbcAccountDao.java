package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao{

    private final JdbcTemplate jdbcTemplate;
    private static final BigDecimal STARTING_BALANCE = new BigDecimal ("1000.00");

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //// GETS THE USER ACCOUNT BY ITS ID FROM THE DATABASE IN THE ACCOUNT TABLE
     @Override
     public Account getAccountById(int id) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance, FROM  account WHERE account_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
            if (results.next()) {
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException( "Unable to connect to server or database" , e);
        }
        return account;
     }

       @Override
       public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance FROM account";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                Account account = mapRowToAccount(results);
                accounts.add(account);
            }
        }catch ( CannotGetJdbcConnectionException e) {
            throw new DaoException(" Unable to connect to server or database", e);
        }
        return accounts;
       }


  //  @Override
    public Account getAccountByUserId(int userId) {

        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?;";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
            if (rowSet.next()) {
                account = mapRowToAccount(rowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;
    }

    public Account createAccount(Account account) {
        Account newAccount = null;

        String sql = "INSERT INTO account (user_id, balance) VALUES (?, ?)";
        int newAccountId = jdbcTemplate.update(sql, account.getUserId(), STARTING_BALANCE);
        newAccount = getAccountById(newAccountId);

        return newAccount;
    }

    public Account updateAccount(Account account) {
        Account newAccount = null;

        String sql = "UPDATE account " +
                "Set account_id = ?, " +
                "user_id = ?," +
                "balance = ? " +
                "WHERE account_id = ? " +
                "RETURNING account_id;";
        int newAccountId = jdbcTemplate.queryForObject(sql, int.class, account.getId(), account.getUserId(), account.getBalance(), account.getId());
        newAccount = getAccountById(newAccountId);

        return newAccount;
    }

    public void deleteAccountById(int accountId) {
        String sql = "DELETE FROM account WHERE account_id = ?";
        jdbcTemplate.update(sql, accountId);
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }



}
