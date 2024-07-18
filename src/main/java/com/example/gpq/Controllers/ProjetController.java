package com.example.gpq.Controllers;

import com.example.gpq.Entities.*;
import com.example.gpq.Services.IActiviteService;
import com.example.gpq.Services.IClientService;
import com.example.gpq.Services.IProjetService;
import com.example.gpq.Services.IUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projet")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjetController {

    @Autowired
    private IProjetService projetService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IActiviteService activiteService;

    @Autowired
    private IClientService clientService;
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

    @PostMapping("/ajouter")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('DIRECTEUR')")
    public ResponseEntity<String> ajouterProjet(
            @RequestBody Projet projet,
            @RequestParam(value = "activiteId") Long activiteId,
            @RequestParam(value = "chefDeProjetNom", required = false) String chefDeProjetNom,
            @RequestParam("responsableQualiteNom") String responsableQualiteNom,
            @RequestParam("nomC") String nomC) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User utilisateurConnecte = userService.findByEmail(username);

            Activite activite = activiteService.findById(activiteId);
            if (activite == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Activité non trouvée.");
            }

            User responsableQualite = userService.findByNom(responsableQualiteNom);
            if (responsableQualite == null || !responsableQualite.getRole().equals(Role.RQUALITE)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Responsable qualité non trouvé ou invalide.");
            }

            Client client = clientService.findByNomC(nomC);
            if (client == null) {
                client = new Client();
                client.setNomC(nomC);
                client = clientService.save(client);
            }

            projet.setActivite(activite);
            projet.setResponsableQualite(responsableQualite);
            projet.setClient(client);

            if (utilisateurConnecte.getRole().equals(Role.CHEFDEPROJET)) {
                projet.setChefDeProjet(utilisateurConnecte);
                projetService.ajouterProjetAvecAffectation(utilisateurConnecte, projet, null, responsableQualiteNom);
            } else if (utilisateurConnecte.getRole().equals(Role.DIRECTEUR)) {
                User chefDeProjet = userService.findByNom(chefDeProjetNom);
                if (chefDeProjet == null || !chefDeProjet.getRole().equals(Role.CHEFDEPROJET)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chef de projet non trouvé ou invalide.");
                }
                projet.setChefDeProjet(chefDeProjet);
                projetService.ajouterProjetAvecAffectation(utilisateurConnecte, projet, chefDeProjetNom, responsableQualiteNom);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Utilisateur non autorisé.");
            }

            return ResponseEntity.ok("Projet ajouté avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié.");
        }
    }

    @GetMapping("/activites/{activiteId}/projets")
    public ResponseEntity<List<Projet>> getProjetsByActivite(@PathVariable Long activiteId) {
        Activite activite = activiteService.findById(activiteId);
        if (activite == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        List<Projet> projets = projetService.findByActivite(activite);
        return ResponseEntity.ok(projets);
    }
}