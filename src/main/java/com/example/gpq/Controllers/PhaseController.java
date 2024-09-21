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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(PhaseController.class); // Updated to PhaseController

    @Autowired
    public PhaseController(ApplicationService applicationService, IProjetService projetService,
                           IChecklistService checklistService, IPhaseService phaseService) {
        this.applicationService = applicationService;
        this.projetService = projetService;
        this.checklistService = checklistService;
        this.phaseService = phaseService;
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CHEFDEPROJET')")
    public ResponseEntity<String> deletePhase(@PathVariable Long id) {
        Optional<Phase> phaseOpt = phaseService.findById(id);
        if (phaseOpt.isEmpty()) {
            logger.error("Phase not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Phase non trouvée.");
        }

        Phase phase = phaseOpt.get();

        // Supprimez la checklist associée
        checklistService.deleteChecklistByPhase(phase);

        // Supprimez la phase
        phaseService.deletePhase(id);
        logger.info("Phase deleted with ID: " + id);
        return ResponseEntity.ok("{\"message\":\"Phase supprimée avec succès.\"}");
    }
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<Phase>> getPhasesByProjet(@PathVariable Long projetId) {
        Optional<Projet> projetOpt = projetService.findById(projetId);
        if (projetOpt.isEmpty()) {
            logger.error("Projet not found with ID: " + projetId);
            return ResponseEntity.notFound().build();
        }

        Projet projet = projetOpt.get();
        List<Phase> phases = applicationService.getPhasesByProjet(projet);
        logger.info("Fetched phases for projet ID: " + projetId);
        return ResponseEntity.ok(phases);
    }

    @PostMapping("/ajouterPhases")
    @PreAuthorize("hasRole('CHEFDEPROJET')")
    public ResponseEntity<String> ajouterPhases(
            @RequestBody List<Phase> phases,
            @RequestParam(value = "projetId") Long projetId) {

        logger.info("Attempting to add phases to project ID: " + projetId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("User not authenticated.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié.");
        }

        String username = authentication.getName();
        Optional<Projet> projetOpt = projetService.findById(projetId);
        if (projetOpt.isEmpty()) {
            logger.error("Project not found with ID: " + projetId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Projet non trouvé.");
        }

        Projet projet = projetOpt.get();
        if (!projet.getChefDeProjet().getEmail().equals(username)) {
            logger.warn("Unauthorized user attempt by: " + username);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Utilisateur non autorisé.");
        }

        for (Phase phase : phases) {
            try {
                // Set default state to EN_COURS
                phase.setEtat(EtatPhase.EN_COURS); // Use the enum value directly
                phase.setIdPh(null);
                Phase savedPhase = phaseService.ajouterPhase(phase, projet);
                logger.info("Phase added with ID: " + savedPhase.getIdPh());
            } catch (IllegalArgumentException e) {
                logger.error("Error adding phase: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Phases ajoutées avec succès.");
    }

    @PutMapping("/updatePhaseEtat/{id}")
    @PreAuthorize("hasRole('CHEFDEPROJET')")
    public ResponseEntity<String> updatePhaseEtat(@PathVariable Long id, @RequestBody EtatPhase newEtat) {
        Optional<Phase> phaseOpt = phaseService.findById(id);
        if (phaseOpt.isEmpty()) {
            logger.error("Phase not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Phase non trouvée.");
        }

        Phase phase = phaseOpt.get();
        phase.setEtat(newEtat);

        if (newEtat == EtatPhase.TERMINE && phase.getChecklist() == null) {
            Checklist checklist = checklistService.createChecklist(phase);
            phase.setChecklist(checklist);
            phaseService.save(phase);
        } else {
            phaseService.save(phase);
        }

        logger.info("Phase state updated for ID: " + id);
        return ResponseEntity.ok("État de la phase mis à jour avec succès.");
    }

    @PutMapping("/updatePhase/{id}")
    @PreAuthorize("hasRole('CHEFDEPROJET')")
    public ResponseEntity<String> updatePhase(@PathVariable Long id, @RequestBody Phase phaseDetails) {
        Optional<Phase> phaseOpt = phaseService.findById(id);
        if (phaseOpt.isEmpty()) {
            logger.error("Phase not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Phase non trouvée.");
        }

        Phase phase = phaseOpt.get();
        phase.setDescription(phaseDetails.getDescription());
        phase.setObjectifs(phaseDetails.getObjectifs());
        phase.setPlannedStartDate(phaseDetails.getPlannedStartDate());
        phase.setPlannedEndDate(phaseDetails.getPlannedEndDate());
        phase.setEffectiveStartDate(phaseDetails.getEffectiveStartDate());
        phase.setEffectiveEndDate(phaseDetails.getEffectiveEndDate());

        // Vérifie les valeurs d'effort avant de les mettre à jour
        if (phaseDetails.getEffortActuel() != null) {
            phase.setEffortActuel(phaseDetails.getEffortActuel());
        }
        if (phaseDetails.getEffortPlanifie() != null) {
            phase.setEffortPlanifie(phaseDetails.getEffortPlanifie());
        }

        if (phaseDetails.getEtat() == EtatPhase.TERMINE && phase.getChecklist() == null) {
            Checklist checklist = checklistService.createChecklist(phase);
            phase.setChecklist(checklist);
            phaseService.save(phase);
        } else {
            phaseService.save(phase);
        }

        logger.info("Phase updated for ID: " + id);
        return ResponseEntity.ok("Phase mise à jour avec succès.");
    }
    @GetMapping("/{phaseId}/effortVariance")
    public ResponseEntity<Double> getEffortVariance(@PathVariable Long phaseId) {
        double effortVariance = phaseService.calculerEffortVariance(phaseId);  // Appel de la méthode via l'interface
        return ResponseEntity.ok(effortVariance);
    }

    // Endpoint pour calculer le schedule variance
    @GetMapping("/{phaseId}/scheduleVariance")
    public ResponseEntity<Double> getScheduleVariance(@PathVariable Long phaseId) {
        double scheduleVariance = phaseService.calculerScheduleVariance(phaseId);  // Appel de la méthode via l'interface
        return ResponseEntity.ok(scheduleVariance);
    }

}
