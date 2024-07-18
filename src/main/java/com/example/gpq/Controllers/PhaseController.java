package com.example.gpq.Controllers;

import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.EtatPhase;
import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.Projet;
import com.example.gpq.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/phases")
@CrossOrigin(origins = "http://localhost:4200")
public class PhaseController {
    private final ApplicationService applicationService;
    private final IProjetService projetService;
    private final IChecklistService checklistService;
    private final IPhaseService phaseService;

    @Autowired
    public PhaseController(ApplicationService applicationService, IProjetService projetService,
                           IChecklistService checklistService, IPhaseService phaseService) {
        this.applicationService = applicationService;
        this.projetService = projetService;
        this.checklistService = checklistService;
        this.phaseService = phaseService;
    }

    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<Phase>> getPhasesByProjet(@PathVariable Long projetId) {
        Optional<Projet> projetOpt = projetService.findById(projetId);
        if (projetOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Projet projet = projetOpt.get();
        List<Phase> phases = applicationService.getPhasesByProjet(projet);
        return ResponseEntity.ok(phases);
    }

    @PostMapping("/ajouterPhases")
    @PreAuthorize("hasRole('CHEFDEPROJET')")
    public ResponseEntity<String> ajouterPhases(
            @RequestBody List<Phase> phases,
            @RequestParam(value = "projetId") Long projetId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Optional<Projet> projetOpt = projetService.findById(projetId);
            if (projetOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Projet non trouvé.");
            }

            Projet projet = projetOpt.get();
            if (!projet.getChefDeProjet().getEmail().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Utilisateur non autorisé.");
            }

            for (Phase phase : phases) {
                Phase savedPhase = phaseService.ajouterPhase(phase, projet);
                if (savedPhase.getEtat() == EtatPhase.TERMINE) {
                    checklistService.createChecklist(savedPhase); // Crée la checklist avec les items
                }
            }
            return ResponseEntity.ok("Phases ajoutées avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié.");
        }
    }

    @PutMapping("/updatePhase/{id}")
    @PreAuthorize("hasRole('CHEFDEPROJET')")
    public ResponseEntity<String> updatePhase(@PathVariable Long id, @RequestBody Phase phaseDetails) {
        Optional<Phase> phaseOpt = phaseService.findById(id);
        if (phaseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Phase non trouvée.");
        }

        Phase phase = phaseOpt.get();
        phase.setDescription(phaseDetails.getDescription());
        phase.setObjectifs(phaseDetails.getObjectifs());
        phase.setPlannedStartDate(phaseDetails.getPlannedStartDate());
        phase.setPlannedEndDate(phaseDetails.getPlannedEndDate());
        phase.setEffectiveStartDate(phaseDetails.getEffectiveStartDate());
        phase.setEffectiveEndDate(phaseDetails.getEffectiveEndDate());
        phase.setEtat(phaseDetails.getEtat());

        if (phaseDetails.getEtat() == EtatPhase.TERMINE && phase.getChecklist() == null) {
            Checklist checklist = checklistService.createChecklist(phase);
            phase.setChecklist(checklist);
            phaseService.save(phase);
        } else {
            phaseService.save(phase);
        }

        return ResponseEntity.ok("Phase mise à jour avec succès.");
    }
}