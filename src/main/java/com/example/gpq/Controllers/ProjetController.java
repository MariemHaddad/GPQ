package com.example.gpq.Controllers;

import com.example.gpq.Entities.Projet;
import com.example.gpq.Entities.User;
import com.example.gpq.Services.IProjetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projet")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjetController {
    @Autowired
    private IProjetService projetService;

    @PostMapping("/ajouter")
    public ResponseEntity<String> ajouterProjet(@RequestBody Projet projet,
                                                @RequestParam(value = "chefDeProjetId", required = false) Long chefDeProjetId,
                                                @RequestParam("responsableQualiteId") Long responsableQualiteId,
                                                HttpServletRequest request) {
        // Obtenir l'utilisateur connecté depuis la session
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User utilisateurConnecte = (User) session.getAttribute("user");
            // Appeler le service pour ajouter le projet avec affectation des utilisateurs
            projetService.ajouterProjetAvecAffectation(utilisateurConnecte, projet, chefDeProjetId, responsableQualiteId);
            return ResponseEntity.ok("Projet ajouté avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié.");
        }
    }
}