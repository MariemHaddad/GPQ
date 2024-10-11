package com.example.gpq.Auth;

import com.example.gpq.Configuration.JwtService;
import  static com.example.gpq.Entities.Role.*;

import com.example.gpq.Services.EmailServiceImpl;
import lombok.extern.slf4j.Slf4j;
import com.example.gpq.Entities.AccountStatus;
import com.example.gpq.Entities.User;
import com.example.gpq.Repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
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
    private final EmailServiceImpl emailService;
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
        try {
            // Tenter d'authentifier l'utilisateur
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            // Si les informations d'identification sont invalides, renvoyer un message d'erreur approprié
            throw new RuntimeException("Adresse mail ou Mot de passe Incorrect");  // Message d'erreur générique
        } catch (AuthenticationException e) {
            // Gestion des autres erreurs d'authentification
            throw new RuntimeException("Authentication failed"); // Message d'erreur générique
        }

        // Récupérer l'utilisateur après une authentification réussie
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Adresse mail ou Mot de passe Incorrect")); // Utiliser le même message

        // Vérifiez le statut du compte uniquement après une authentification réussie
        if (user.getAccountStatus() != AccountStatus.APPROVED) {
            // Si le compte n'est pas approuvé, renvoyer un message d'erreur
            throw new RuntimeException("Votre compte n'est pas approuver ou bloquer, Veuillez contacter l'admin!"); // Message spécifique
        }

        // Si tout va bien, générez le jeton JWT
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

        // Récupère l'utilisateur depuis la base de données
        User user = repository.findById(idU)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Modifie le statut du compte de l'utilisateur
        user.setAccountStatus(status);
        repository.save(user);

        log.info("Account validated successfully.");

        // Si l'utilisateur est approuvé, envoie un e-mail
        if (status == AccountStatus.APPROVED) {
            emailService.sendEmail(
                    user.getEmail(),
                    "Votre compte est approuvé",
                    "Félicitations, votre compte a été approuvé. Bienvenue entre nous ! :)"
            );
            log.info("Approval email sent to user: {}", user.getEmail());
        }
    }

    public List<User> getUsersByAccountStatus(AccountStatus status) {
        return  repository.findByAccountStatus(status);
    }
}