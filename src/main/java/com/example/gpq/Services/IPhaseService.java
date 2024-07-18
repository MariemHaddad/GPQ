package com.example.gpq.Services;

import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.Projet;

import java.util.List;
import java.util.Optional;

public interface IPhaseService {
    Phase ajouterPhase(Phase phase, Projet projet);
    Optional<Phase> findById(Long phaseId);
    List<Phase> getPhasesByProjet(Projet projet);
    Phase save(Phase phase); // Méthode pour sauvegarder une phase
}