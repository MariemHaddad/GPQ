package com.example.gpq.Services;

import com.example.gpq.Entities.EtatPhase;
import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.Projet;

import java.util.List;
import java.util.Optional;

/**
 * Interface for Phase service operations.
 */
public interface IPhaseService {
    Phase ajouterPhase(Phase phase, Projet projet);
    Optional<Phase> findById(Long phaseId);
    List<Phase> getPhasesByProjet(Projet projet);
    Phase save(Phase phase);
    void validatePhaseName(String phaseName);
    Phase updatePhaseEtat(Long id, EtatPhase newEtat);
    double calculerEffortVariance(Long phaseId);
    double calculerScheduleVariance(Long phaseId);
    void deletePhase(Long id);
    double calculerTauxNCInterne(Projet projet);
    double calculerTauxNCExterne(Projet projet);

}