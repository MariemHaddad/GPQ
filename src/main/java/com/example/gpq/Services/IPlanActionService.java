package com.example.gpq.Services;

import com.example.gpq.Entities.Action;
import com.example.gpq.Entities.PlanAction;

import java.util.List;

public interface IPlanActionService {
    List<PlanAction> savePlanActions(List<PlanAction> planActions);// Ajouter ou mettre à jour un plan d'action
    PlanAction getPlanActionById(Long id); // Récupérer un plan d'action par ID
    List<PlanAction> getAllPlansAction(); // Récupérer tous les plans d'action
    PlanAction updatePlanAction(Long id, PlanAction planActionDetails); // Mettre à jour un plan d'action
    void deletePlanAction(Long id); // Supprimer un plan d'action par ID
    PlanAction getPlanActionByAnalyseCausaleId(Long idAN);
    // Méthodes pour Action
    Action saveAction(Action action); // Ajouter ou mettre à jour une action
    Action getActionById(Long id); // Récupérer une action par ID
    List<Action> getAllActions(); // Récupérer toutes les actions
    Action updateAction(Long id, Action actionDetails);
    void deleteAction(Long id); // Supprimer une action par ID
}