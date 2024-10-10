package com.example.gpq.Controllers;

import com.example.gpq.Entities.Role;
import com.example.gpq.Entities.User;
import com.example.gpq.Services.EmailServiceImpl;
import com.example.gpq.Services.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
   IUserService userService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmailServiceImpl emailService;

    @GetMapping("/all-users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/registeruser")
    public ResponseEntity<String> registerUser(@RequestBody User user, HttpServletRequest request) {
        // Vérifier la validité du mot de passe
        if (!isPasswordValid(user.getMotDePasse())) {
            return ResponseEntity.badRequest().body("Le mot de passe doit contenir au moins 6 caractères, dont au moins une lettre majuscule, une lettre minuscule et un chiffre.");
        }
        String hashedPassword = hashPassword(user.getMotDePasse());
        user.setMotDePasse(hashedPassword);

        userService.registerUser(user);

        // Créer une session pour l'utilisateur
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user); // Stocker l'utilisateur dans la session

        // Construire le nom d'utilisateur à partir du nom et du prénom de l'utilisateur
        String username = user.getPrenom() + " " + user.getNom();

        // Ajouter le nom de l'utilisateur à la session
        session.setAttribute("username", username);

        String roleMessage = getRoleMessage(user.getRole());

        return ResponseEntity.ok("Inscription réussie pour " + roleMessage + ".");
    }
    private String getRoleMessage(Role role) {
        if (role != null) {
            switch (role) {
                case CHEFDEPROJET:
                    return "le chef de projet";
                case ADMIN:
                    return "l'admin";
                case DIRECTEUR:
                    return "le directeur";
                default:
                    return "l'utilisateur";
            }
        } else {
            return "le rôle n'est pas défini"; // Ou renvoyez un message d'erreur approprié
        }
    }
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    private boolean isPasswordValid(String password) {
        // Au moins 6 caractères, une lettre majuscule, une lettre minuscule et un chiffre
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    @GetMapping("/user-role")
    public ResponseEntity<String> getUserRole(HttpServletRequest request) {
        // Vérifiez si l'utilisateur est authentifié
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // Utilisateur authentifié, vérifiez les autorisations appropriées
            User user = (User) session.getAttribute("user");
            if (user.getRole() != null) {
                // Renvoyez le rôle de l'utilisateur
                String role = user.getRole().toString();
                return ResponseEntity.ok(role);
            } else {
                // Le rôle n'est pas défini, renvoyez une erreur
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Le rôle de l'utilisateur n'est pas défini.");
            }
        } else {
            // Utilisateur non authentifié, renvoyez une erreur
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié.");
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // Invalider la session actuelle
            return ResponseEntity.ok("Déconnexion réussie.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Aucune session à déconnecter.");
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("L'e-mail est manquant ou vide.");
        }

        Optional<User> userOptional = userService.getUserByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }

        User user = userOptional.get(); // Get the user from the Optional
        String token = generateResetToken();

        // Now you can pass the User object to saveResetToken
        userService.saveResetToken(token, user); // Pass the token and User object

        String resetLink =  "http://localhost:4200/reset-password?token=" + token;
        String message = "Bonjour,\n\nPour réinitialiser votre mot de passe, cliquez sur le lien suivant : \n" + resetLink + "\n\nCordialement,\nVotre équipe.";
        emailService.sendEmail(email, "Réinitialisation du mot de passe", message);

        return ResponseEntity.ok("Un lien de réinitialisation de mot de passe a été envoyé à " + email + ".");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        System.out.println("Received token: " + token);
        System.out.println("Received new password: " + newPassword);

        if (token == null || token.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("Token ou mot de passe manquant.");
        }

        Optional<User> optionalUser = userService.getUserByResetToken(token);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token invalide.");
        }

        User user = optionalUser.get(); // Get the user from the Optional
        user.setMotDePasse(hashPassword(newPassword));
        user.setResetToken(null); // Reset the token after use
        userService.updateUser(user);

        return ResponseEntity.ok("Votre mot de passe a été réinitialisé avec succès.");
    }
    private String generateResetToken() {
        return UUID.randomUUID().toString();
    }
    @GetMapping("/chefsdeprojet")
    public ResponseEntity<List<User>> getChefsDeProjet() {
        List<User> chefsDeProjet = userService.findByRole(Role.CHEFDEPROJET);
        return ResponseEntity.ok(chefsDeProjet);
    }

    @GetMapping("/responsablesqualite")
    public ResponseEntity<List<User>> getResponsablesQualite() {
        List<User> responsablesQualite = userService.findByRole(Role.RQUALITE);
        return ResponseEntity.ok(responsablesQualite);
    }

}


