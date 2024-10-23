package com.example.gpq.Services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.PhaseRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

 class PhaseServiceImplTest {

    @Mock
    private PhaseRepository phaseRepository;

    @Mock
    private IChecklistService checklistService;

    @InjectMocks
    private PhaseServiceImpl phaseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAjouterPhase() {
        Projet projet = new Projet();
        Phase phase = new Phase();
        phase.setDescription("La conception préliminaire");

        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        Phase savedPhase = phaseService.ajouterPhase(phase, projet);

        assertNotNull(savedPhase);
        assertEquals("La conception préliminaire", savedPhase.getDescription());
        verify(phaseRepository, times(1)).save(phase);
    }

    @Test
    public void testDeletePhase() {
        Long phaseId = 1L;
        doNothing().when(phaseRepository).deleteById(phaseId);

        phaseService.deletePhase(phaseId);

        verify(phaseRepository, times(1)).deleteById(phaseId);
    }

    @Test
    public void testUpdatePhaseEtat() {
        Long phaseId = 1L;
        Phase phase = new Phase();
        phase.setIdPh(phaseId);
        phase.setEtat(EtatPhase.EN_COURS);

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));
        when(phaseRepository.save(any(Phase.class))).thenReturn(phase);

        Phase updatedPhase = phaseService.updatePhaseEtat(phaseId, EtatPhase.TERMINE);

        assertEquals(EtatPhase.TERMINE, updatedPhase.getEtat());
        verify(phaseRepository, times(1)).save(phase);
    }

    @Test
    public void testFindById() {
        Long phaseId = 1L;
        Phase phase = new Phase();
        phase.setIdPh(phaseId);

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        Optional<Phase> foundPhase = phaseService.findById(phaseId);

        assertTrue(foundPhase.isPresent());
        assertEquals(phaseId, foundPhase.get().getIdPh());
        verify(phaseRepository, times(1)).findById(phaseId);
    }

    @Test
    public void testGetPhasesByProjet() {
        Projet projet = new Projet();
        Phase phase1 = new Phase();
        Phase phase2 = new Phase();

        List<Phase> phases = Arrays.asList(phase1, phase2);
        when(phaseRepository.findByProjet(projet)).thenReturn(phases);

        List<Phase> foundPhases = phaseService.getPhasesByProjet(projet);

        assertEquals(2, foundPhases.size());
        verify(phaseRepository, times(1)).findByProjet(projet);
    }

    @Test
    public void testSave() {
        Phase phase = new Phase();
        when(phaseRepository.save(phase)).thenReturn(phase);

        Phase savedPhase = phaseService.save(phase);

        assertNotNull(savedPhase);
        verify(phaseRepository, times(1)).save(phase);
    }

    @Test
    public void testValidatePhaseName() {
        String validName = "Conception détaillée";
        String invalidName = "Invalid Phase Name";

        assertDoesNotThrow(() -> phaseService.validatePhaseName(validName));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> phaseService.validatePhaseName(invalidName));
        assertEquals("Nom de phase invalide : " + invalidName, exception.getMessage());
    }

    @Test
    public void testCalculerEffortVariance() {
        Long phaseId = 1L;
        Phase phase = new Phase();
        phase.setEffortActuel(100.0);
        phase.setEffortPlanifie(80.0);

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        double effortVariance = phaseService.calculerEffortVariance(phaseId);

        assertEquals(0.2, effortVariance);
    }

    @Test
    public void testCalculerScheduleVariance() {
        Long phaseId = 1L;
        Phase phase = new Phase();
        phase.setPlannedStartDate(new Date(0));
        phase.setPlannedEndDate(new Date(1000));
        phase.setEffectiveEndDate(new Date(1500));

        when(phaseRepository.findById(phaseId)).thenReturn(Optional.of(phase));

        double scheduleVariance = phaseService.calculerScheduleVariance(phaseId);

        assertEquals(0.5, scheduleVariance);
    }

    @Test
     void testCalculerTauxNCInterne() {
        Projet projet = new Projet();
        Phase phase1 = new Phase();
        phase1.setStatusLivraisonInterne(EtatLivraison.NC);
        Phase phase2 = new Phase();
        phase2.setStatusLivraisonInterne(EtatLivraison.C);

        List<Phase> phases = Arrays.asList(phase1, phase2);
        when(phaseRepository.findByProjet(projet)).thenReturn(phases);

        double tauxNCInterne = phaseService.calculerTauxNCInterne(projet);

        assertEquals(0.5, tauxNCInterne);
    }

    @Test
     void testCalculerTauxNCExterne() {
        Projet projet = new Projet();
        Phase phase1 = new Phase();
        phase1.setStatusLivraisonExterne(EtatLivraison.NC);
        Phase phase2 = new Phase();
        phase2.setStatusLivraisonExterne(EtatLivraison.C);

        List<Phase> phases = Arrays.asList(phase1, phase2);
        when(phaseRepository.findByProjet(projet)).thenReturn(phases);

        double tauxNCExterne = phaseService.calculerTauxNCExterne(projet);

        assertEquals(0.5, tauxNCExterne);
    }
}
