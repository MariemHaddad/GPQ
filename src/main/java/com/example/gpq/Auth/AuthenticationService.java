package com.example.gpq.Auth;

import com.example.gpq.Configuration.JwtService;
import  static com.example.gpq.Entities.Role.*;
import lombok.extern.slf4j.Slf4j;
import com.example.gpq.Entities.AccountStatus;
import com.example.gpq.Entities.User;
import com.example.gpq.Repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthenticationResponse register(RegisterRequest request) {
        User user = null;
        if (request.getRole() == DIRECTEUR) {
            user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(DIRECTEUR)
                    .accountStatus(AccountStatus.PENDING)
                    .build();
            repository.save(user);
        } else if (request.getRole() == CHEFDEPROJET) {
            user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(CHEFDEPROJET)
                    .accountStatus(AccountStatus.PENDING)
                    .build();
            repository.save(user);
        } else if (request.getRole() == ADMIN) {
            user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(ADMIN)
                    .accountStatus(AccountStatus.PENDING)
                    .build();
            repository.save(user);
        } else if (request.getRole() == RQUALITE) {
            user = User.builder()
                    .nom(request.getNom())
                    .prenom(request.getPrenom())
                    .email(request.getEmail())
                    .motDePasse(passwordEncoder.encode(request.getPassword()))
                    .role(RQUALITE)
                    .accountStatus(AccountStatus.PENDING)
                    .build();
            repository.save(user);
        }
        var jwtToken = jwtService.generateToken(user);
        log.info("JWT Token generated: {}", jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail()).orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Vérifiez le statut du compte
        if (user.getAccountStatus() != AccountStatus.APPROVED) {
            throw new RuntimeException("Account not approved");
        }

        var jwtToken = jwtService.generateToken(user);
        log.info("JWT Token generated: {}", jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .role(user.getRole().toString())
                .id(user.getIdU())
                .build();
    }

  @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void changeAccountStatus(Long idU, AccountStatus status) {
        log.info("Validating account with ID: {} and status: {}", idU, status);
        User user = repository.findById(idU).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setAccountStatus(status);
        repository.save(user);
        log.info("Account validated successfully.");
    }
    public String refreshToken(String oldToken) {
        if (jwtService.isTokenExpired(oldToken)) {
            // Le token actuel est expiré, générez un nouveau token et retournez-le
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtService.extractUsername(oldToken));
            return jwtService.generateToken(userDetails);
        } else {
            // Le token actuel est encore valide, retournez-le sans aucun changement
            return oldToken;
        }
    }

    public List<User> getUsersByAccountStatus(AccountStatus status) {
        return  repository.findByAccountStatus(status);
    }
}