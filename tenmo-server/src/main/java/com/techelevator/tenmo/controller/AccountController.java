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

@RestController
public class AccountController {


    private JdbcAccountDao jdbcAccountDao;

    public AccountController() {
        this.jdbcAccountDao = new JdbcAccountDao(new JdbcTemplate());
    }

    @PreAuthorize("permitAll")
    @RequestMapping(path = "", method = RequestMethod.GET)
    public List<Account> list() {

        return jdbcAccountDao.getAccounts();
    }

    @RequestMapping(path = "/account/{id}", method = RequestMethod.GET)
    public Account get(@PathVariable int id) {
        Account Account = jdbcAccountDao.getAccountById(id);
        if (Account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return Account;
        }
    }


    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/account", method = RequestMethod.POST)
    public Account create(@Valid @RequestBody Account Account) {
        return jdbcAccountDao.createAccount(Account);
    }


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
