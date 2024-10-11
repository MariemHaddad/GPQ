package com.example.gpq.Services;

import com.example.gpq.Entities.Role;
import com.example.gpq.Entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;


public interface IUserService extends UserDetailsService {
    void saveResetToken(String token, User user);
    Optional<User> getUserByResetToken(String token);
    void registerUser(User user);
    Optional<User> getUserByEmail(String email);
  User findByNom(String nom);
    void updateUser(User user);
    User getUserById(Long userId);
    User findById(Long id);
    User findByEmail(String email);
    List<User> getAllUsers();
    void blockUser(Long id); // Doit correspondre à l'implémentation dans la classe de service
    User updateUser(Long id, User updatedUser);
    List<User> findByRole(Role chefdeprojet);
}
