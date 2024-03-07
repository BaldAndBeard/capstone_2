package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface AccountDao {


        List<Account> getAccounts();

        Account getAccountById(int id);

        Account getAccountByUserId(int userId);

        Account createAccount(Account account);

        Account updateAccount(Account account);

        void deleteAccountById(int accountId);


}
