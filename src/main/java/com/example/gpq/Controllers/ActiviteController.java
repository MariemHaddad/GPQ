package com.example.gpq.Controllers;

import com.example.gpq.Entities.Activite;
import com.example.gpq.Services.IActiviteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activites")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class ActiviteController {
    @Autowired
private IActiviteService activiteService;

    @GetMapping("/getActivities")
    public ResponseEntity<List<Activite>> getActivites() {
        List<Activite> activites = activiteService.getAllActivites();
        return ResponseEntity.ok(activites);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ajouter")
    public ResponseEntity<String> ajouterActivite(@RequestBody Activite activite) {
        activiteService.ajouterActivite(activite);
        return ResponseEntity.ok("Activité ajoutée avec succès.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/modifier/{id}")
    public ResponseEntity<String> modifierActivite(@PathVariable("id") Long id, @RequestBody String nouveauNom) {
        activiteService.modifierActivite(id, nouveauNom);
        return ResponseEntity.ok("Activité modifiée avec succès.");
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<String> supprimerActivite(@PathVariable("id") Long id) {
        activiteService.supprimerActivite(id);
        return ResponseEntity.ok("Activité supprimée avec succès.");
    }}

//modif role juste l'admin
//user ne peux modifier que son MDP
//chaque user possède son propre activité
//role: responsable qualité a l'accés à plusieurs activité  avec l'affectation de l'admin
//activité: qualité
//crud: activité