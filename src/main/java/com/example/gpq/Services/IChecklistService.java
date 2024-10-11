package com.example.gpq.Services;

import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.ChecklistItem;
import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.StatusChecklist;

import java.util.List;

public interface IChecklistService {
    Checklist getChecklistData(Long checklistId);
    Checklist createChecklist(Phase phase);
    void deleteChecklistByPhase(Phase phase);
    void saveChecklist(Checklist checklist);

    Checklist updateChecklistStatus(Long checklistId, StatusChecklist status, String remarque);

    void updateChecklistItems(Long checklistId, List<ChecklistItem> updatedItems);

    Checklist initializeChecklist(Long phaseId);


    Checklist findByPhaseId(Long phaseId);

    // Ajoutez cette méthode pour trouver une checklist par son ID
}