package com.example.gpq.Services;

import com.example.gpq.Entities.Role;
import com.example.gpq.Entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;


public interface IUserService extends UserDetailsService {
    void registerUser(User user);
    Optional<User> getUserByEmail(String email);
  User findByNom(String nom);
    void updateUser(User user);
    User getUserById(Long userId);
    User findById(Long id);
    User findByEmail(String email);

    List<User> findByRole(Role chefdeprojet);
}
