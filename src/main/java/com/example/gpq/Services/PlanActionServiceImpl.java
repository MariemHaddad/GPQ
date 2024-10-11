package com.example.gpq.Services;

import com.example.gpq.Entities.Action;
import com.example.gpq.Entities.PlanAction;
import com.example.gpq.Repositories.ActionRepository;
import com.example.gpq.Repositories.PlanActionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlanActionServiceImpl implements IPlanActionService {

    @Autowired
    private PlanActionRepository planActionRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Override
    public List<PlanAction> savePlanActions(List<PlanAction> planActions) {
        return planActionRepository.saveAll(planActions); // Utilisez saveAll() pour sauvegarder une liste
    }
    @Override
    public PlanAction getPlanActionById(Long id) {
        return planActionRepository.findById(id).orElse(null);
    }

    @Override
    public List<PlanAction> getAllPlansAction() {
        return planActionRepository.findAll();
    }
    @Transactional
    @Override
    public PlanAction updatePlanAction(Long id, PlanAction planActionDetails) {
        // Récupérer le PlanAction existant
        PlanAction existingPlanAction = planActionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PlanAction non trouvé"));

        // Log du PlanAction existant avant mise à jour
        System.out.println("Existing PlanAction before update: " + existingPlanAction);

        // Mettre à jour les champs du PlanAction existant
        existingPlanAction.setLeconTirees(planActionDetails.getLeconTirees());

        // Mettre à jour ou ajouter les actions
        List<Action> updatedActions = planActionDetails.getActions();

        if (updatedActions != null) {
            // Parcourir les actions mises à jour
            for (Action updatedAction : updatedActions) {
                if (updatedAction.getId() != null) {
                    // Trouver l'action existante
                    Action existingAction = actionRepository.findById(updatedAction.getId()).orElse(null);
                    if (existingAction != null) {
                        // Mettre à jour les champs de l'action existante
                        existingAction.setDescription(updatedAction.getDescription());
                        existingAction.setType(updatedAction.getType());
                        existingAction.setResponsable(updatedAction.getResponsable());
                        existingAction.setDatePlanification(updatedAction.getDatePlanification());
                        existingAction.setDateRealisation(updatedAction.getDateRealisation());
                        existingAction.setCritereEfficacite(updatedAction.getCritereEfficacite());
                        existingAction.setEfficace(updatedAction.getEfficace());
                        existingAction.setCommentaire(updatedAction.getCommentaire());
                        // Enregistrer les modifications de l'action
                        actionRepository.save(existingAction);
                    } else {
                        // Si l'action n'existe pas, l'ajouter à la liste des actions
                        updatedAction.setPlanAction(existingPlanAction);
                        existingPlanAction.getActions().add(updatedAction);
                    }
                } else {
                    // Si l'ID de l'action est nul, ajouter l'action comme nouvelle
                    updatedAction.setPlanAction(existingPlanAction);
                    existingPlanAction.getActions().add(updatedAction);
                }
            }
        }

        // Enregistrer le PlanAction mis à jour
        PlanAction savedPlanAction = planActionRepository.save(existingPlanAction);
        System.out.println("Updated PlanAction saved: " + savedPlanAction);

        return savedPlanAction;
    }
    @Override
    public void deletePlanAction(Long id) {
        planActionRepository.deleteById(id);
    }

    @Override
    public Action saveAction(Action action) {
        return actionRepository.save(action);
    }

    @Override
    public Action getActionById(Long id) {
        return actionRepository.findById(id).orElse(null);
    }

    @Override
    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    @Override
    public Action updateAction(Long id, Action actionDetails) {
        Optional<Action> actionOptional = actionRepository.findById(id);
        if (actionOptional.isPresent()) {
            Action action = actionOptional.get();
            // Update the fields of the action based on actionDetails
            if (actionDetails.getDescription() != null) {
                action.setDescription(actionDetails.getDescription());
            }
            if (actionDetails.getType() != null) {
                action.setType(actionDetails.getType());
            }
            if (actionDetails.getResponsable() != null) {
                action.setResponsable(actionDetails.getResponsable());
            }
            if (actionDetails.getDatePlanification() != null) {
                action.setDatePlanification(actionDetails.getDatePlanification());
            }
            if (actionDetails.getDateRealisation() != null) {
                action.setDateRealisation(actionDetails.getDateRealisation());
            }
            if (actionDetails.getCritereEfficacite() != null) {
                action.setCritereEfficacite(actionDetails.getCritereEfficacite());
            }
            if (actionDetails.getEfficace() != null) {
                action.setEfficace(actionDetails.getEfficace());
            }
            if (actionDetails.getCommentaire() != null) {
                action.setCommentaire(actionDetails.getCommentaire());
            }
            return actionRepository.save(action);
        }
        return null; // If the action ID is not found
    }
    @Override
    public void deleteAction(Long id) {
        actionRepository.deleteById(id);
    }
    @Override
    public PlanAction getPlanActionByAnalyseCausaleId(Long idAN) {
        // Supposons que vous avez une méthode dans le repository pour trouver par analyse causale
        return planActionRepository.findByAnalyseCausaleIdAN(idAN);
    }
}

