package com.example.gpq.Services;

import com.example.gpq.Entities.Role;
import com.example.gpq.Entities.User;
import com.example.gpq.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Transactional
    @Override
    public void registerUser(User user) {
        // Logique d'enregistrement de l'utilisateur
        userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Assuming userRepository is your JPA repository for User
    }
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id); // Suppression de l'utilisateur
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
            user.setNom(updatedUser.getNom());
            user.setPrenom(updatedUser.getPrenom());
            user.setEmail(updatedUser.getEmail());
            user.setRole(updatedUser.getRole());
            return userRepository.save(user);
        }).orElseThrow(() -> new NoSuchElementException("User not found with id " + id));
    }

    @Override
    public void saveResetToken(String token, User user) {
        user.setResetToken(token); // Set the reset token
        userRepository.save(user);  // Save the user with the new token
    }

    @Override
    public Optional<User> getUserByResetToken(String token) {
        return userRepository.findByResetToken(token); // Assuming this is correctly implemented
    }




    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public User findByNom(String nom) {
        return userRepository.findByNom(nom);
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }

    @Override
    public User loadUserByUsername(String email) {
        return findByEmail(email);
    }
}



