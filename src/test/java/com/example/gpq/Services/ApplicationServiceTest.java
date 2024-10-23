package com.example.gpq.Services;

import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.Projet;
import com.example.gpq.Repositories.PhaseRepository;
import com.example.gpq.Repositories.ProjetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationServiceTest {

    @InjectMocks
    private ApplicationService applicationService;

    @Mock
    private PhaseRepository phaseRepository;

    @Mock
    private ProjetRepository projetRepository;

    private Phase phase;
    private Projet projet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        projet = Projet.builder()
                .idP(1L)
                .nomP("Projet Test")
                .build();

        phase = Phase.builder()
                .idPh(1L)
                .description("Phase Test")
                .projet(projet)
                .build();
    }

    @Test
    void testAjouterPhaseEtChecklist() {
        when(phaseRepository.save(phase)).thenReturn(phase);

        Phase result = applicationService.ajouterPhaseEtChecklist(phase, projet);

        assertNotNull(result);
        assertEquals("Phase Test", result.getDescription());
        assertEquals(projet, result.getProjet());
        verify(phaseRepository, times(1)).save(phase);
    }

    @Test
    void testFindPhaseById_Found() {
        when(phaseRepository.findById(1L)).thenReturn(Optional.of(phase));

        Optional<Phase> result = applicationService.findPhaseById(1L);

        assertTrue(result.isPresent());
        assertEquals("Phase Test", result.get().getDescription());
        verify(phaseRepository, times(1)).findById(1L);
    }

    @Test
    void testFindPhaseById_NotFound() {
        when(phaseRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Phase> result = applicationService.findPhaseById(2L);

        assertFalse(result.isPresent());
        verify(phaseRepository, times(1)).findById(2L);
    }

    @Test
    void testSavePhase() {
        when(phaseRepository.save(phase)).thenReturn(phase);

        applicationService.savePhase(phase);

        verify(phaseRepository, times(1)).save(phase);
    }

    @Test
    void testGetPhasesByProjet() {
        List<Phase> phases = Arrays.asList(phase);
        when(phaseRepository.findByProjet(projet)).thenReturn(phases);

        List<Phase> result = applicationService.getPhasesByProjet(projet);

        assertEquals(1, result.size());
        assertEquals("Phase Test", result.get(0).getDescription());
        verify(phaseRepository, times(1)).findByProjet(projet);
    }
}
