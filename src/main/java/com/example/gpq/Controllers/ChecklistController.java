package com.example.gpq.Controllers;

import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.ChecklistItem;
import com.example.gpq.Entities.StatusChecklist;
import com.example.gpq.Services.IChecklistService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;


@RestController
@RequestMapping("/api/checklists")
@CrossOrigin(origins = "http://localhost:4200")
public class ChecklistController {

    private final IChecklistService checklistService;
    private static final Logger logger = LoggerFactory.getLogger(ChecklistController.class);

    @Autowired
    public ChecklistController(IChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    // Méthode existante pour initialiser une checklist
    @PostMapping("/initialize")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<?> initializeChecklist(@RequestParam Long phaseId) {
        try {
            logger.debug("Initializing checklist for phaseId: {}", phaseId);
            Checklist checklist = checklistService.initializeChecklist(phaseId);
            logger.debug("Checklist initialized successfully: {}", checklist);
            return ResponseEntity.ok(checklist);
        } catch (IllegalArgumentException e) {
            logger.error("Error initializing checklist: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error initializing checklist: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de l'initialisation de la checklist.");
        }
    }

    // Nouvelle méthode pour récupérer une checklist par ID de phase
    @GetMapping("/byPhase/{phaseId}")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<?> getChecklistByPhaseId(@PathVariable Long phaseId) {
        try {
            Checklist checklist = checklistService.findByPhaseId(phaseId);
            if (checklist != null) {
                return ResponseEntity.ok(checklist);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Checklist not found for phase ID: " + phaseId);
            }
        } catch (Exception e) {
            logger.error("Unexpected error retrieving checklist for phaseId: {}", phaseId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue lors de la récupération de la checklist.");
        }
    }


    @PutMapping("/updateStatus/{checklistId}")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<?> updateChecklistStatus(@PathVariable Long checklistId,
                                                   @RequestBody Map<String, Object> payload) {
        try {
            StatusChecklist status = StatusChecklist.valueOf(payload.get("status").toString());
            String remarque = payload.get("remarque").toString();
            checklistService.updateChecklistStatus(checklistId, status, remarque);
            return ResponseEntity.ok(Map.of("message", "Statut de la checklist mis à jour avec succès."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Une erreur est survenue lors de la mise à jour du statut de la checklist."));
        }
    }

    @PutMapping("/updateItems/{checklistId}")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<?> updateChecklistItems(@PathVariable Long checklistId,
                                                  @RequestBody List<ChecklistItem> updatedItems) {
        logger.debug("Updating checklist items for checklistId: {}", checklistId);
        try {
            checklistService.updateChecklistItems(checklistId, updatedItems);
            return ResponseEntity.ok(Map.of("message", "Items de la checklist mis à jour avec succès."));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating checklist items: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error updating checklist items: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Une erreur est survenue lors de la mise à jour des items de la checklist."));
        }
    }
}