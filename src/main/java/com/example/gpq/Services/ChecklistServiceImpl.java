package com.example.gpq.Services;

import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.ChecklistRepository;
import com.example.gpq.Repositories.ItemChecklistRepository;
import com.example.gpq.Repositories.PhaseRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import org.slf4j.Logger;


@Service
public class ChecklistServiceImpl implements IChecklistService {


    private final ChecklistRepository checklistRepository;
    private final ItemChecklistRepository itemChecklistRepository;
    private final PhaseRepository phaseRepository;

    @Autowired
    public ChecklistServiceImpl(ChecklistRepository checklistRepository, ItemChecklistRepository itemChecklistRepository, PhaseRepository phaseRepository) {
        this.checklistRepository = checklistRepository;
        this.itemChecklistRepository = itemChecklistRepository;
        this.phaseRepository = phaseRepository;
    }

    @Override
    public Checklist initializeChecklist(Long phaseId) {
        Phase phase = phaseRepository.findById(phaseId).orElseThrow(() -> new IllegalArgumentException("Invalid phase ID"));
        return createChecklist(phase);
    }
    @Override
    public Checklist createChecklist(Phase phase) {
        Checklist checklist = new Checklist();
        checklist.setPhase(phase);

        List<ChecklistItem> items = new ArrayList<>();
        String phaseName = phase.getDescription();
        if (phaseName.equalsIgnoreCase("Le Plan de Management (PM)")) {
            items.add(new ChecklistItem("Le Plan de Management (PM) est stocké dans le système documentaire du projet", checklist));
            items.add(new ChecklistItem("L'historique de changement du Plan de Management (PM) entre deux versions est décrit", checklist));
        } else if (phaseName.equalsIgnoreCase("Spécification")) {
            items.add(new ChecklistItem("Toutes les références internes des exigences sont correctes", checklist));
            items.add(new ChecklistItem("Les exigences sont cohérentes entre elles", checklist));
        }

        checklist.setItems(items);
        checklist = checklistRepository.save(checklist);
        itemChecklistRepository.saveAll(items);
        return checklist;
    }

    @Override
    public void saveChecklist(Checklist checklist) {
        checklistRepository.save(checklist);
    }

    @Override
    public Checklist updateChecklistStatus(Long checklistId, StatusChecklist status, String remarque) {
        Checklist checklist = checklistRepository.findById(checklistId).orElseThrow(() -> new IllegalArgumentException("Invalid checklist ID"));
        checklist.setStatus(status);
        checklist.setRemarque(remarque);
        return checklistRepository.save(checklist);
    }

    @Override
    public void updateChecklistItems(Long checklistId, List<ChecklistItem> updatedItems) {
        Checklist checklist = checklistRepository.findById(checklistId).orElseThrow(() -> new IllegalArgumentException("Invalid checklist ID"));
        for (ChecklistItem item : updatedItems) {
            item.setChecklist(checklist);
        }
        itemChecklistRepository.saveAll(updatedItems);
    }
}