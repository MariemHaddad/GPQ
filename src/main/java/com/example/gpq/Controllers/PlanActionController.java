package com.example.gpq.Controllers;

import com.example.gpq.Entities.Action;
import com.example.gpq.Entities.PlanAction;
import com.example.gpq.Repositories.PlanActionRepository;
import com.example.gpq.Services.IPlanActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/planAction")
@CrossOrigin(origins = "http://localhost:4200")
public class PlanActionController {

    @Autowired
    private IPlanActionService planActionService;
    @Autowired
    private PlanActionRepository planActionRepository;

    // Créer ou mettre à jour un plan d'action (automatique ou initial)
    @PostMapping("/add")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<List<PlanAction>> ajouterPlanActions(@RequestBody List<PlanAction> planActions) {
        List<PlanAction> savedPlanActions = planActionService.savePlanActions(planActions);
        return ResponseEntity.ok(savedPlanActions);
    }
    // Récupérer un plan d'action par ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<PlanAction> getPlanActionById(@PathVariable Long id) {
        PlanAction planAction = planActionService.getPlanActionById(id);
        if (planAction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(planAction);
    }

    // Mettre à jour un plan d'action (RQUALITE remplit le plan)
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<PlanAction> updatePlanAction(
            @PathVariable Long id,
            @RequestBody PlanAction planActionDetails) {
        // Assurez-vous que les données sont correctes avant de mettre à jour
        PlanAction updatedPlanAction = planActionService.updatePlanAction(id, planActionDetails);
        if (updatedPlanAction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedPlanAction);
    }
    @GetMapping("/analyseCausale/{idAN}/planAction")
    public ResponseEntity<PlanAction> getPlanActionByAnalyseCausaleId(@PathVariable Long idAN) {
        PlanAction planAction = planActionService.getPlanActionByAnalyseCausaleId(idAN);
        if (planAction == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(planAction);
    }


    // Récupérer tous les plans d'action
    @GetMapping("/all")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<List<PlanAction>> getAllPlansAction() {
        List<PlanAction> plansAction = planActionService.getAllPlansAction();
        return ResponseEntity.ok(plansAction);
    }

    // Supprimer un plan d'action par ID
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<Void> deletePlanAction(@PathVariable Long id) {
        planActionService.deletePlanAction(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/actions/add")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<Action> addAction(@RequestBody Action action) {
        Action savedAction = planActionService.saveAction(action);
        return ResponseEntity.ok(savedAction);
    }

    // Récupérer une action par ID
    @GetMapping("/actions/{id}")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<Action> getActionById(@PathVariable Long id) {
        Action action = planActionService.getActionById(id);
        if (action == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(action);
    }

    // Mettre à jour une action
    @PutMapping("/actions/update/{idPa}")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<Void> updateActions(
            @PathVariable Long idPa,
            @RequestBody List<Action> actions) {

        // Check if the list of actions is empty or null
        if (actions == null || actions.isEmpty()) {
            return ResponseEntity.badRequest().build(); // Return 400 Bad Request if no action is provided
        }

        // Loop through the actions and update each one
        for (Action action : actions) {
            Action updatedAction = planActionService.updateAction(action.getId(), action); // Assuming each action has an ID
            if (updatedAction == null) {
                return ResponseEntity.notFound().build(); // Return 404 Not Found if no action was found
            }
        }

        // Return 204 No Content if the update is successful
        return ResponseEntity.noContent().build();
    }
    //pprimer une action par ID
    @DeleteMapping("/actions/delete/{id}")
    @PreAuthorize("hasRole('RQUALITE')")
    public ResponseEntity<Void> deleteAction(@PathVariable Long id) {
        planActionService.deleteAction(id);
        return ResponseEntity.noContent().build();
    }

}