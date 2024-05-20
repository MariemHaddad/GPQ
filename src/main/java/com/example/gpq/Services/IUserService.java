package com.example.gpq.Services;

import com.example.gpq.Entities.User;

import java.util.Optional;


public interface IUserService {
    void registerUser(User user);

    Optional<User> getUserByEmail(String email);
    void updateUser(User user);

    User getUserById(Long id);
}

