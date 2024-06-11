package com.example.gpq.Services;

import com.example.gpq.Entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;


public interface IUserService extends UserDetailsService {
    void registerUser(User user);
    Optional<User> getUserByEmail(String email);
    void updateUser(User user);
    User getUserById(Long userId);
    User findById(Long id);
    User findByEmail(String email);
}
