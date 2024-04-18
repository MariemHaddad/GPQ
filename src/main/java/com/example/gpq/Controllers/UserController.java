package com.example.gpq.Controllers;

import com.example.gpq.Entities.Role;
import com.example.gpq.Entities.User;
import com.example.gpq.Services.IUserService;
import com.example.gpq.Services.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {
    @Autowired
   IUserService userService;
    @Autowired
    private JavaMailSender javaMailSender;
  /*  @Autowired
private IUserService userService;
*/

  @PostMapping("/registeruser")
  public ResponseEntity<String> registerUser(@RequestBody User user) {
      // Vérifier la validité du mot de passe
      if (!isPasswordValid(user.getMotDePasse())) {
          return ResponseEntity.badRequest().body("Le mot de passe doit contenir au moins 6 caractères, dont au moins une lettre majuscule, une lettre minuscule et un chiffre.");
      }
      String hashedPassword = hashPassword(user.getMotDePasse());
      user.setMotDePasse(hashedPassword);

      userService.registerUser(user);

      String roleMessage = getRoleMessage(user.getRole());

      return ResponseEntity.ok("Inscription réussie pour " + roleMessage + ".");
  }


    private String getRoleMessage(Role role) {
        if (role != null) {
            switch (role) {
                case Chef_de_projet:
                    return "le chef de projet";
                case Admin:
                    return "l'admin";
                case Directeur:
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
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody User loginUser, HttpServletRequest request) {
        // Vérifier l'authentification
        User existingUser = userService.getUserByEmail(loginUser.getEmail());
        if (existingUser != null) {
            // Vérifier si les mots de passe correspondent
            if (passwordEncoder.matches(loginUser.getMotDePasse(), existingUser.getMotDePasse())) {
                // Créer une session pour l'utilisateur
                HttpSession session = request.getSession(true);
                session.setAttribute("user", existingUser); // Stocker l'utilisateur dans la session
                return ResponseEntity.ok("Authentification réussie.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect.");
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'e-mail est manquant ou vide.");
        }

        User user = userService.getUserByEmail(email);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }

        String newPassword = generateRandomPassword();
        String hashedPassword = hashPassword(newPassword);
        user.setMotDePasse(hashedPassword);
        userService.updateUser(user);

        String message = "Bonjour,\n\n" +
                "Votre nouveau mot de passe est : " + newPassword + "\n\n" +
                "Cordialement,\n" +
                "Votre équipe.";

        sendEmail(email, "Réinitialisation du mot de passe", message);

        return ResponseEntity.ok("Un nouveau mot de passe a été envoyé à " + email + ".");
    }

    private String generateRandomPassword() {
        // Générer un mot de passe aléatoire avec 8 caractères
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (Math.random() * characters.length());
            newPassword.append(characters.charAt(index));
        }
        return newPassword.toString();
    }

    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
    @GetMapping("/authenticate")
    public ResponseEntity<String> getAuthenticatePage() {
        // Vous pouvez retourner une réponse ou rediriger vers une page spécifique si nécessaire
        return ResponseEntity.ok("Page d'authentification utilisateur");
    }

}


