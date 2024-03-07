package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

// Assigns url path for sever queries
@RestController
public class AccountController {


    private JdbcAccountDao jdbcAccountDao;

    public AccountController() {
        this.jdbcAccountDao = new JdbcAccountDao(new JdbcTemplate());
    }

    // Get a list of all accounts

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Account> list() {

        return jdbcAccountDao.getAccounts();
    }

    // Get a single account by it's ID
    // path declares where the method will pull it's data from
    // method declares what type of request is being used (GET, PUT, POST, DELETE)
    @RequestMapping(path = "/account/{id}", method = RequestMethod.GET)
    // @PathVariable grabs the part of the URL in brackets with the same name as the parameter (in this case "id")
    public Account get(@PathVariable int id) {
        // Assign value to a new instance of account using the output from getAccountById method
        Account account = jdbcAccountDao.getAccountById(id);
        // If the account is null, return an error, otherwise return the new account.
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return account;
        }
    }


    // Create an account
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/account", method = RequestMethod.POST)
    public Account create(@Valid @RequestBody Account Account) {
        return jdbcAccountDao.createAccount(Account);
    }

    // Update an account selected by a chosen ID
    @RequestMapping(path = "/account/{id}", method = RequestMethod.PUT)
    public Account update(@Valid @RequestBody Account Account, @PathVariable int id) {
        // The id on the path takes precedence over the id in the request body, if any
        Account.setId(id);
        try {
            Account updatedAccount = jdbcAccountDao.updateAccount(Account);
            return updatedAccount;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        }
    }

    // Delete an account selected by a given ID
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "/account/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable int id) {
        jdbcAccountDao.deleteAccountById(id);
    }

    @RequestMapping(path = "/whoami")
    public String whoAmI(Principal principal) {
        String name = principal.getName();
        return name;
    }




}
