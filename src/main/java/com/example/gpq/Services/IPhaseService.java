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
    /**
     * Adds a new phase to a project.
     *
     * @param phase The phase to be added.
     * @param projet The project to which the phase will be added.
     * @return The added phase.
     */
    Phase ajouterPhase(Phase phase, Projet projet);

    /**
     * Finds a phase by its ID.
     *
     * @param phaseId The ID of the phase.
     * @return An Optional containing the phase if found, otherwise empty.
     */
    Optional<Phase> findById(Long phaseId);

    /**
     * Retrieves all phases associated with a project.
     *
     * @param projet The project whose phases are to be retrieved.
     * @return A list of phases for the given project.
     */
    List<Phase> getPhasesByProjet(Projet projet);

    /**
     * Saves or updates a phase in the repository.
     *
     * @param phase The phase to be saved or updated.
     * @return The saved phase.
     */
    Phase save(Phase phase);

    /**
     * Validates the name of a phase.
     *
     * @param phaseName The name of the phase to be validated.
     * @throws IllegalArgumentException if the phase name is invalid.
     */
    void validatePhaseName(String phaseName);

    /**
     * Updates the state of a phase.
     *
     * @param id The ID of the phase to be updated.
     * @param newEtat The new state of the phase.
     * @return The updated phase.
     */
    Phase updatePhaseEtat(Long id, EtatPhase newEtat);
}
