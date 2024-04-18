package com.example.gpq.Services;

import com.example.gpq.Entities.User;
import org.springframework.stereotype.Service;


public interface IUserService {
    void registerUser(User user);

    User getUserByEmail(String email);
    void updateUser(User user);

    User getUserById(Long id);
}

