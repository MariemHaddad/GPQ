package com.example.gpq.Services;

import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.Projet;
import com.example.gpq.Repositories.PhaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PhaseServiceImpl implements IPhaseService {
    private final PhaseRepository phaseRepository;
    private final IChecklistService checklistService;

    @Autowired
    public PhaseServiceImpl(PhaseRepository phaseRepository, @Lazy IChecklistService checklistService) {
        this.phaseRepository = phaseRepository;
        this.checklistService = checklistService; // @Lazy injection
    }

    @Override
    public Phase ajouterPhase(Phase phase, Projet projet) {
        phase.setProjet(projet);
        Phase savedPhase = phaseRepository.save(phase);

        // Initialize and save checklist here
        Checklist checklist = new Checklist();
        checklist.setPhase(savedPhase); // Ensure phase_id is set
        checklistService.saveChecklist(checklist); // Use injected checklistService

        return savedPhase;
    }

    @Override
    public Optional<Phase> findById(Long phaseId) {
        return phaseRepository.findById(phaseId);
    }

    @Override
    public List<Phase> getPhasesByProjet(Projet projet) {
        return phaseRepository.findByProjet(projet);
    }

    @Override
    public Phase save(Phase phase) {
        // Ensure phase and its collections are properly initialized
        if (phase.getChecklist() == null) {
            phase.setChecklist(new Checklist());
        }
        if (phase.getChecklist().getItems() == null) {
            phase.getChecklist().setItems(new ArrayList<>());
        }
        return phaseRepository.save(phase);
    }
}