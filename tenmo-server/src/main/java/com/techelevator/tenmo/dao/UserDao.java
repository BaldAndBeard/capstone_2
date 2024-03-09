package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.RegisterUserDto;
import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserDao {

    User getUserByAccountId(int accountId);

    List<User> getUsers();

    User getUserById(int id);

    User getUserByUsername(String username);

    User createUser(RegisterUserDto user);
}
