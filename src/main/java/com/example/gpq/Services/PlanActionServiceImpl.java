package com.example.gpq.Services;

import com.example.gpq.Entities.Action;
import com.example.gpq.Entities.PlanAction;
import com.example.gpq.Repositories.ActionRepository;
import com.example.gpq.Repositories.PlanActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanActionServiceImpl implements IPlanActionService {

    @Autowired
    private PlanActionRepository planActionRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Override
    public PlanAction savePlanAction(PlanAction planAction) {
        return planActionRepository.save(planAction);
    }

    @Override
    public PlanAction getPlanActionById(Long id) {
        return planActionRepository.findById(id).orElse(null);
    }

    @Override
    public List<PlanAction> getAllPlansAction() {
        return planActionRepository.findAll();
    }

    @Override
    public PlanAction updatePlanAction(Long id, PlanAction planActionDetails) {
        Optional<PlanAction> planActionOptional = planActionRepository.findById(id);
        if (planActionOptional.isPresent()) {
            PlanAction planAction = planActionOptional.get();

            // Mise à jour des champs du plan d'action
            planAction.setLeçonTirées(planActionDetails.getLeçonTirées());
            planAction.setActions(planActionDetails.getActions()); // Met à jour les actions associées

            return planActionRepository.save(planAction);
        }
        return null; // Si l'ID est introuvable
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
            if (actionDetails.getDescription() != null) action.setDescription(actionDetails.getDescription());
            if (actionDetails.getType() != null) action.setType(actionDetails.getType());
            if (actionDetails.getResponsable() != null) action.setResponsable(actionDetails.getResponsable());
            if (actionDetails.getDatePlanification() != null) action.setDatePlanification(actionDetails.getDatePlanification());
            if (actionDetails.getDateRealisation() != null) action.setDateRealisation(actionDetails.getDateRealisation());
            if (actionDetails.getCritereEfficacite() != null) action.setCritereEfficacite(actionDetails.getCritereEfficacite());
            if (actionDetails.getEfficace() != null) action.setEfficace(actionDetails.getEfficace());
            if (actionDetails.getCommentaire() != null) action.setCommentaire(actionDetails.getCommentaire());
            return actionRepository.save(action);
        }
        return null;
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

