package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

// Assigns url path for server queries
@RestController
public class GeneralController {

    // CONTROLLER TO DO LIST
    //


    private AccountDao accountDao;
    private UserDao userDao;

    public GeneralController(AccountDao accountDao, UserDao userDao) {

        this.userDao = userDao;
        this.accountDao = accountDao;
    }

    // Get a list of all accounts
       @RequestMapping(path = "/account", method = RequestMethod.GET)
       public List<Account> listAccounts(@RequestParam(defaultValue = "0") int user_id) {
        List<Account> listOfAccounts = new ArrayList<>();

        if (user_id != 0) {
           listOfAccounts.add(accountDao.getAccountByUserId(user_id));
       }
        return listOfAccounts;

             }


    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public List<User> listUsers(@RequestParam(defaultValue = "") String username) {
        List<User> listOfUsers = new ArrayList<>();

        if (!username.equals("")) {
            listOfUsers = userDao.getUsers();
        } else {
            listOfUsers = userDao.getUsers();
        }

        return listOfUsers;
    }

    // Get a single account by it's ID
    // path declares where the method will pull it's data from
    // method declares what type of request is being used (GET, PUT, POST, DELETE)
    @RequestMapping(path = "/account/{id}", method = RequestMethod.GET)
    // @PathVariable grabs the part of the URL in brackets with the same name as the parameter (in this case "id")
    public Account getAccount(@PathVariable int id) {
        // Assign value to a new instance of account using the output from getAccountById method
        Account account = accountDao.getAccountById(id);
        // If the account is null, return an error, otherwise return the new account.
        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
        } else {
            return account;
        }
    }

    @RequestMapping(path = "/user/{username}", method = RequestMethod.GET)
    // @PathVariable grabs the part of the URL in brackets with the same name as the parameter (in this case "id")
    public User getUser(@PathVariable String username) {
        // Assign value to a new instance of user using the output from getUserByUsername method
        User user = userDao.getUserByUsername(username);
        // If the account is null, return an error, otherwise return the new account.
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        } else {
            return user;
        }
    }

    // THIS PATH IS A PLACEHOLDER
    @RequestMapping(path = "/user/test/{accountId}", method = RequestMethod.GET)
    public User getUserByAccountId(@PathVariable int accountId) {
        // Assign value to a new instance of user using the output from getUserByUsername method
        User user = userDao.getUserByAccountId(accountId);
        // If the account is null, return an error, otherwise return the new account.
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        } else {
            return user;
        }
    }


    // Create an account
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/account", method = RequestMethod.POST)
    public Account createAccount(@Valid @RequestBody Account Account) {
        return accountDao.createAccount(Account);
    }

    // Update an account selected by a chosen ID
    @RequestMapping(path = "account/{id}", method = RequestMethod.PUT)
    public Account updateAccount(@Valid @RequestBody Account Account, @PathVariable int id) {
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
