package com.example.gpq.Auth;

import com.example.gpq.Configuration.JwtService;
import com.example.gpq.Entities.AccountStatus;
import com.example.gpq.Entities.User;
import com.example.gpq.Services.EmailServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticatonController {
    private final AuthenticationService service;
    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        // Définir l'état du compte sur "PENDING"
        request.setAccountStatus(AccountStatus.PENDING);
        emailService.sendEmail("haddadmariem32@gmail.com", "Nouveau compte en attente", "Un nouveau compte utilisateur est en attente de validation.");
        return ResponseEntity.ok(service.register(request));
    }

    // Méthode pour changer le statut du compte
    @PostMapping("/change-account-status")
    public ResponseEntity<?> changeAccountStatus(@RequestParam Long idU, @RequestParam AccountStatus status) {
        log.info("Attempting to change account status for user with ID: {} to status: {}", idU, status);

        // Appel du service pour changer le statut du compte
        service.changeAccountStatus(idU, status);

        log.info("Account status changed successfully.");

        // Retourne une réponse JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", "Account status changed successfully.");
        return ResponseEntity.ok(response);
    }

        @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            AuthenticationResponse response = service.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Retournez une réponse avec un message d'erreur
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage())); // Utilisation de Map pour renvoyer un message d'erreur
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.ok("User logged out successfully.");
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (jwtService.validateToken(refreshToken, null)){
            String newToken = jwtService.generateTokenFromRefreshToken(refreshToken);
            return ResponseEntity.ok(Collections.singletonMap("token", newToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/pending-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getPendingUsers() {
        List<User> pendingUsers = service.getUsersByAccountStatus(AccountStatus.PENDING);
        return ResponseEntity.ok(pendingUsers);
    }

}