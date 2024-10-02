package com.example.gpq.Services;

import com.example.gpq.Entities.Checklist;
import com.example.gpq.Entities.EtatPhase;
import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.Projet;
import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.PhaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
@Service
public class PhaseServiceImpl implements IPhaseService {
    private static final Logger logger = LoggerFactory.getLogger(PhaseServiceImpl.class);

    private final PhaseRepository phaseRepository;
    private final IChecklistService checklistService;

    @Autowired
    public PhaseServiceImpl(PhaseRepository phaseRepository, @Lazy IChecklistService checklistService) {
        this.phaseRepository = phaseRepository;
        this.checklistService = checklistService; // @Lazy injection
    }
    private static final List<String> VALID_PHASE_NAMES = Arrays.asList(
            "La conception préliminaire",
            "La conception détaillée",
            "La mise en œuvre",
            "La vérification",
            "La validation",
            "Code",
            "Spécification"
            // Add other valid phase names here
    );

    @Override
    public Phase ajouterPhase(Phase phase, Projet projet) {
        validatePhaseName(phase.getDescription());
        phase.setProjet(projet);
        phase.setEtat(EtatPhase.EN_COURS); // Set default state to EN_COURS
        Phase savedPhase = phaseRepository.save(phase);

        // Automatically create and associate a checklist if phase state is TERMINE
        if (savedPhase.getEtat() == EtatPhase.TERMINE && savedPhase.getChecklist() == null) {
            Checklist checklist = checklistService.createChecklist(savedPhase);
            savedPhase.setChecklist(checklist);
            phaseRepository.save(savedPhase); // Save phase again to update checklist
        }

        return savedPhase;
    }
    @Override
    public void deletePhase(Long id) {
        phaseRepository.deleteById(id);
    }
    @Override
    public Phase updatePhaseEtat(Long id, EtatPhase newEtat) {
        Optional<Phase> phaseOpt = phaseRepository.findById(id);
        if (phaseOpt.isEmpty()) {
            throw new IllegalArgumentException("Phase non trouvée.");
        }

        Phase phase = phaseOpt.get();
        phase.setEtat(newEtat);

        if (newEtat == EtatPhase.TERMINE && phase.getChecklist() == null) {
            Checklist checklist = checklistService.createChecklist(phase);
            phase.setChecklist(checklist);
        }

        return phaseRepository.save(phase);
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
        return phaseRepository.save(phase);
    }

    @Override
    public void validatePhaseName(String phaseName) {
        if (!VALID_PHASE_NAMES.contains(phaseName)) {
            throw new IllegalArgumentException("Nom de phase invalide : " + phaseName);
        }
    }
    @Override
    public double calculerEffortVariance(Long phaseId) {
        Optional<Phase> optionalPhase = phaseRepository.findById(phaseId);
        if (optionalPhase.isPresent()) {
            Phase phase = optionalPhase.get();
            if (phase.getEffortActuel() != null && phase.getEffortPlanifie() != null && phase.getEffortActuel() != 0) {
                return (phase.getEffortActuel() - phase.getEffortPlanifie()) / phase.getEffortActuel();
            }
        }
        return 0; // Gérer les cas où les valeurs sont nulles ou la phase introuvable
    }

    // Implémentation de la méthode pour calculer le schedule variance
    @Override
    public double calculerScheduleVariance(Long phaseId) {
        Optional<Phase> optionalPhase = phaseRepository.findById(phaseId);
        if (optionalPhase.isPresent()) {
            Phase phase = optionalPhase.get();
            if (phase.getPlannedStartDate() != null && phase.getPlannedEndDate() != null && phase.getEffectiveEndDate() != null) {
                long plannedDuration = phase.getPlannedEndDate().getTime() - phase.getPlannedStartDate().getTime();
                long effectiveDuration = phase.getEffectiveEndDate().getTime() - phase.getPlannedEndDate().getTime();
                return (double) effectiveDuration / plannedDuration;
            }
        }
        return 0; // Gérer les cas où les dates sont nulles ou la phase introuvable
    }
    @Override
    public double calculerTauxNCInterne(Projet projet) {
        List<Phase> phases = phaseRepository.findByProjet(projet);
        if (phases.isEmpty()) {
            return 0;
        }

        long nombreNCInterne = phases.stream()
                .filter(phase -> phase.getStatusLivraisonInterne() == EtatLivraison.NC)
                .count();

        return (double) nombreNCInterne / phases.size();
    }

    @Override
    public double calculerTauxNCExterne(Projet projet) {
        List<Phase> phases = phaseRepository.findByProjet(projet);
        if (phases.isEmpty()) {
            return 0;
        }

        long nombreNCExterne = phases.stream()
                .filter(phase -> phase.getStatusLivraisonExterne() == EtatLivraison.NC)
                .count();

        return (double) nombreNCExterne / phases.size();
    }

}
