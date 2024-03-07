package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

// Assigns url path for server queries
@RestController
@RequestMapping(path = "/account")
public class AccountController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private AccountDao accountDao;

    public AccountController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, AccountDao accountDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.accountDao = accountDao;
    }

    // Get a list of all accounts

    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Account> list(@RequestParam(defaultValue = "0") int user_id, @RequestParam(defaultValue = "") String transfer_status_type) {
        List<Account> listOfAccounts = new ArrayList<>();

        if (user_id != 0) {
            listOfAccounts.add(accountDao.getAccountByUserId(user_id));
        } else if (transfer_status_type.equals("Pending")) {
            // listOfAccounts.add(accountDao.getAllPendingAccounts(transfer_status_type));
        } else {
            listOfAccounts = accountDao.getAccounts();
        }

        return listOfAccounts;
    }

    // Get a single account by it's ID
    // path declares where the method will pull it's data from
    // method declares what type of request is being used (GET, PUT, POST, DELETE)
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    // @PathVariable grabs the part of the URL in brackets with the same name as the parameter (in this case "id")
    public Account get(@PathVariable int id) {
        // Assign value to a new instance of account using the output from getAccountById method
        Account account = accountDao.getAccountById(id);
        // If the account is null, return an error, otherwise return the new account.
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return account;
        }
    }


    // Create an account
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/", method = RequestMethod.POST)
    public Account create(@Valid @RequestBody Account Account) {
        return accountDao.createAccount(Account);
    }

    // Update an account selected by a chosen ID
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public Account update(@Valid @RequestBody Account Account, @PathVariable int id) {
        // The id on the path takes precedence over the id in the request body, if any
        Account.setId(id);
        try {
            Account updatedAccount = accountDao.updateAccount(Account);
            return updatedAccount;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        }
    }

    // Delete an account selected by a given ID
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "/account/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable int id) {
        accountDao.deleteAccountById(id);
    }

    @RequestMapping(path = "/whoami")
    public String whoAmI(Principal principal) {
        String name = principal.getName();
        return name;
    }


}
