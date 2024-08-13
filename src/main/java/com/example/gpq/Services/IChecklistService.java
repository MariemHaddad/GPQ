package com.example.gpq.Services;

import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.ChecklistItem;
import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.StatusChecklist;

import java.util.List;

public interface IChecklistService {
    Checklist createChecklist(Phase phase); // Remplacez saveChecklist par createChecklist
    void saveChecklist(Checklist checklist); // Ajoutez cette méthode
    Checklist updateChecklistStatus(Long checklistId, StatusChecklist status, String remarque);
    void updateChecklistItems(Long checklistId, List<ChecklistItem> updatedItems);
    Checklist initializeChecklist(Long phaseId);

    Checklist findByPhaseId(Long phaseId); // Nouvelle méthode ajoutée
}