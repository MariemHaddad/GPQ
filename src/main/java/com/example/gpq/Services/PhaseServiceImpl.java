package com.example.gpq.Services;

import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.EtatPhase;
import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.Projet;
import com.example.gpq.Repositories.PhaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PhaseServiceImpl implements IPhaseService {
    private static final Logger logger = LoggerFactory.getLogger(PhaseServiceImpl.class);
    private final PhaseRepository phaseRepository;
    private final IChecklistService checklistService;
    private static final List<String> VALID_PHASE_NAMES = Arrays.asList(
            "La conception préliminaire",
            "Manuel d'utilisation",
            "Tests unitaires",
            "Le Plan d'Integration",
            "Le Plan de Validation",
            "Le Plan de Management (PM)",
            "Code",
            "Spécification",
            "Conception détaillée"
    );

    @Autowired
    public PhaseServiceImpl(PhaseRepository phaseRepository, @Lazy IChecklistService checklistService) {
        this.phaseRepository = phaseRepository;
        this.checklistService = checklistService; // @Lazy injection
    }

    @Override
    public void validatePhaseName(String phaseName) {
        if (!VALID_PHASE_NAMES.contains(phaseName)) {
            throw new IllegalArgumentException("Nom de phase invalide : " + phaseName);
        }
    }

    @Override
    public Phase ajouterPhase(Phase phase, Projet projet) {
        validatePhaseName(phase.getDescription());
        phase.setProjet(projet);
        Phase savedPhase = phaseRepository.save(phase);

        // Initialize and save checklist only if necessary
        if (savedPhase.getEtat() == EtatPhase.TERMINE) {
            Checklist checklist = checklistService.createChecklist(savedPhase);
            checklistService.saveChecklist(checklist);
            savedPhase.setChecklist(checklist);
            phaseRepository.save(savedPhase); // Ensure the phase is updated with the checklist
        }

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