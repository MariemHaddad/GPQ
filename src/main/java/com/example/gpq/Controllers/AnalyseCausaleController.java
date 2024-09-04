package com.example.gpq.Controllers;

import com.example.gpq.Entities.*;
import com.example.gpq.Services.IAnalyseCausaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analyseCausale")
@CrossOrigin(origins = "http://localhost:4200")
public class AnalyseCausaleController {

    @Autowired
    private IAnalyseCausaleService analyseCausaleService;
    private static final Logger logger = LoggerFactory.getLogger(ChecklistController.class);

    @PostMapping("/add")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<String> ajouterAnalyseCausale(
            @RequestParam Long checklistId,
            @RequestBody AnalyseCausale analyseCausale) {

        // Log the incoming data
        System.out.println("Received AnalyseCausale: " + analyseCausale);

        // Rechercher la checklist par son ID
        Checklist checklist = analyseCausaleService.getChecklistById(checklistId);
        if (checklist == null) {
            return ResponseEntity.badRequest().body("Checklist avec l'ID spécifié n'existe pas.");
        }

        // Vérifier le statut de la checklist
        if (checklist.getStatus() != StatusChecklist.REFUSE) {
            return ResponseEntity.badRequest().body("L'analyse causale ne peut être ajoutée que lorsque le statut de la checklist est REFUSE.");
        }

        // Assigner la checklist à l'analyse causale
        analyseCausale.setChecklist(checklist);

        // Valider les autres conditions
        if (analyseCausale.getMethodeAnalyse() == MethodeAnalyse.FIVE_WHYS &&
                (analyseCausale.getCinqPourquoi() == null || analyseCausale.getCinqPourquoi().isEmpty())) {
            return ResponseEntity.badRequest().body("Le modèle Five Whys nécessite d'ajouter les 5 Pourquoi.");
        }

        if (analyseCausale.getMethodeAnalyse() == MethodeAnalyse.ISHIKAWA &&
                (analyseCausale.getCausesIshikawa() == null || analyseCausale.getCausesIshikawa().isEmpty())) {
            return ResponseEntity.badRequest().body("Le modèle Ishikawa nécessite de remplir le diagramme Ishikawa.");
        }

        // Assigner la liste de Pourquoi à l'AnalyseCausale
        if (analyseCausale.getCinqPourquoi() != null) {
            for (Pourquoi pourquoi : analyseCausale.getCinqPourquoi()) {
                pourquoi.setAnalyseCausale(analyseCausale);
            }
        }

        AnalyseCausale savedAnalyse = analyseCausaleService.saveAnalyseCausale(analyseCausale);
        return ResponseEntity.ok("Analyse causale ajoutée avec succès.");

    }
    @GetMapping("/byChecklist/{checklistId}")
    public ResponseEntity<AnalyseCausale> getAnalyseCausaleByChecklist(@PathVariable Long checklistId) {
        AnalyseCausale analyseCausale = analyseCausaleService.getAnalyseCausaleByChecklist(checklistId);
        if (analyseCausale == null) {
            logger.error("AnalyseCausale with Checklist ID {} does not exist.", checklistId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analyseCausale);
    }
    @PostMapping("/{id}/addPourquoi")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<String> ajouterPourquoi(@PathVariable Long id, @RequestBody Pourquoi pourquoi) {
        // Récupérez l'analyse causale par ID
        AnalyseCausale analyseCausale = analyseCausaleService.getAnalyseCausaleById(id);

        // Vérifiez que l'analyse causale utilise la méthode Five Whys
        if (analyseCausale == null || analyseCausale.getMethodeAnalyse() != MethodeAnalyse.FIVE_WHYS) {
            return ResponseEntity.badRequest().body("L'analyse causale doit utiliser la méthode Five Whys pour ajouter des Pourquoi.");
        }

        pourquoi.setAnalyseCausale(analyseCausale);
        analyseCausale.getCinqPourquoi().add(pourquoi);
        analyseCausaleService.saveAnalyseCausale(analyseCausale);
        return ResponseEntity.ok("Pourquoi ajouté avec succès.");
    }

    @PostMapping("/{id}/addCauseIshikawa")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<String> ajouterCauseIshikawa(@PathVariable Long id, @RequestBody CauseIshikawa cause) {
        // Récupérez l'analyse causale par ID
        AnalyseCausale analyseCausale = analyseCausaleService.getAnalyseCausaleById(id);

        // Vérifiez que l'analyse causale utilise la méthode Ishikawa
        if (analyseCausale == null || analyseCausale.getMethodeAnalyse() != MethodeAnalyse.ISHIKAWA) {
            return ResponseEntity.badRequest().body("L'analyse causale doit utiliser la méthode Ishikawa pour ajouter des causes.");
        }

        cause.setAnalyseCausale(analyseCausale);
        analyseCausale.getCausesIshikawa().add(cause);
        analyseCausaleService.saveAnalyseCausale(analyseCausale);
        return ResponseEntity.ok("Cause Ishikawa ajoutée avec succès.");
    }
}