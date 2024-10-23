package com.example.gpq.Services;


import com.example.gpq.Entities.Activite;
import com.example.gpq.Repositories.ActiviteRepository;
import jakarta.persistence.EntityNotFoundException;
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

class ActiviteServiceImplTest {

    @InjectMocks
    private ActiviteServiceImpl activiteService;

    @Mock
    private ActiviteRepository activiteRepository;

    private Activite activite;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        activite = Activite.builder()
                .idA(1L)
                .nomA("Activite Test")
                .build();
    }

    @Test
    void testGetAllActivites() {
        List<Activite> activites = Arrays.asList(activite);

        when(activiteRepository.findAll()).thenReturn(activites);

        List<Activite> result = activiteService.getAllActivites();

        assertEquals(1, result.size());
        assertEquals("Activite Test", result.get(0).getNomA());
        verify(activiteRepository, times(1)).findAll();
    }

    @Test
    void testAjouterActivite() {
        activiteService.ajouterActivite(activite);

        verify(activiteRepository, times(1)).save(activite);
    }

    @Test
    void testModifierActivite_Success() {
        String nouveauNom = "New Name";

        when(activiteRepository.findById(1L)).thenReturn(Optional.of(activite));

        activiteService.modifierActivite(1L, nouveauNom);

        assertEquals(nouveauNom, activite.getNomA());
        verify(activiteRepository, times(1)).save(activite);
    }

    @Test
    void testModifierActivite_NotFound() {
        when(activiteRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () ->
                activiteService.modifierActivite(1L, "New Name")
        );

        assertEquals("Activité non trouvée avec l'ID : 1", thrown.getMessage());
    }

    @Test
    void testSupprimerActivite() {
        doNothing().when(activiteRepository).deleteById(1L);

        activiteService.supprimerActivite(1L);

        verify(activiteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testFindById_Found() {
        when(activiteRepository.findById(1L)).thenReturn(Optional.of(activite));

        Activite result = activiteService.findById(1L);

        assertNotNull(result);
        assertEquals("Activite Test", result.getNomA());
    }

    @Test
    void testFindById_NotFound() {
        when(activiteRepository.findById(1L)).thenReturn(Optional.empty());

        Activite result = activiteService.findById(1L);

        assertNull(result);
    }
}
