package com.example.gpq.Services;

import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.Projet;
import com.example.gpq.Repositories.PhaseRepository;
import com.example.gpq.Repositories.ProjetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
//Nouveau service pour gérer les interactions entre les service : phase et checklist
@Service
public class ApplicationService {
    private final PhaseRepository phaseRepository;
    private final ProjetRepository projetRepository;

    @Autowired
    public ApplicationService(PhaseRepository phaseRepository, ProjetRepository projetRepository) {
        this.phaseRepository = phaseRepository;
        this.projetRepository = projetRepository;
    }

    public Phase ajouterPhaseEtChecklist(Phase phase, Projet projet) {
        phase.setProjet(projet);
        return phaseRepository.save(phase);
    }

    public Optional<Phase> findPhaseById(Long id) {
        return phaseRepository.findById(id);
    }

    public void savePhase(Phase phase) {
        phaseRepository.save(phase);
    }

    public List<Phase> getPhasesByProjet(Projet projet) {
        return phaseRepository.findByProjet(projet);
    }
}