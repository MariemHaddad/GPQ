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
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    public User findByNom(String nom) {
        return userRepository.findByNom(nom);
    }

    @Override
    public List<User> findByRole(Role role) {
        // Logique pour récupérer les utilisateurs par rôle depuis le repository
        return userRepository.findByRole(role);
    }
    @Override
    public User loadUserByUsername(String email) {
        return findByEmail(email);
    }


}



