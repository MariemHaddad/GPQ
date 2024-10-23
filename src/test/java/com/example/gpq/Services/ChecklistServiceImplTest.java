package com.example.gpq.Services;

import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.ChecklistRepository;
import com.example.gpq.Repositories.ItemChecklistRepository;
import com.example.gpq.Repositories.PhaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChecklistServiceImplTest {

    @InjectMocks
    private ChecklistServiceImpl checklistService;

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private ItemChecklistRepository itemChecklistRepository;

    @Mock
    private PhaseRepository phaseRepository;

    private Phase phase;
    private Checklist checklist;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        phase = new Phase();
        phase.setIdPh(1L);
        phase.setDescription("Test Phase");

        checklist = new Checklist();
        checklist.setIdCh(1L);
        checklist.setPhase(phase);
        checklist.setItems(Arrays.asList(new ChecklistItem("Item 1", checklist), new ChecklistItem("Item 2", checklist)));
    }

    @Test
    void initializeChecklist_ShouldReturnChecklist() {
        when(phaseRepository.findById(1L)).thenReturn(Optional.of(phase));
        when(checklistRepository.save(any(Checklist.class))).thenReturn(checklist);
        when(itemChecklistRepository.saveAll(any())).thenReturn(Arrays.asList(new ChecklistItem("Item 1", checklist), new ChecklistItem("Item 2", checklist)));

        Checklist result = checklistService.initializeChecklist(1L);

        assertNotNull(result);
        assertEquals(phase, result.getPhase());
        assertEquals(2, result.getItems().size());
    }

    @Test
    void initializeChecklist_ShouldThrowIllegalArgumentException_WhenPhaseNotFound() {
        when(phaseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            checklistService.initializeChecklist(1L);
        });

        assertEquals("Invalid phase ID", exception.getMessage());
    }

    @Test
    void deleteChecklistByPhase_ShouldDeleteChecklist_WhenChecklistExists() {
        when(phaseRepository.findById(1L)).thenReturn(Optional.of(phase));
        checklist.setIdCh(1L);
        phase.setChecklist(checklist);
        when(checklistRepository.findById(1L)).thenReturn(Optional.of(checklist));

        checklistService.deleteChecklistByPhase(phase);

        verify(checklistRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteChecklistByPhase_ShouldNotDelete_WhenChecklistDoesNotExist() {
        phase.setChecklist(null);
        checklistService.deleteChecklistByPhase(phase);

        verify(checklistRepository, never()).deleteById(any());
    }

    @Test
    void createChecklist_ShouldReturnCreatedChecklist() {
        when(phaseRepository.findById(1L)).thenReturn(Optional.of(phase));
        when(checklistRepository.save(any(Checklist.class))).thenReturn(checklist);
        when(itemChecklistRepository.saveAll(any())).thenReturn(checklist.getItems());

        Checklist result = checklistService.createChecklist(phase);

        assertNotNull(result);
        assertEquals(phase, result.getPhase());
        assertEquals(2, result.getItems().size());
    }

    @Test
    void saveChecklist_ShouldCallChecklistRepositorySave() {
        checklistService.saveChecklist(checklist);
        verify(checklistRepository, times(1)).save(checklist);
    }

    @Test
    void getChecklistData_ShouldReturnChecklist() {
        when(checklistRepository.findById(1L)).thenReturn(Optional.of(checklist));

        Checklist result = checklistService.getChecklistData(1L);

        assertNotNull(result);
        assertEquals(checklist.getIdCh(), result.getIdCh());
    }

    @Test
    void getChecklistData_ShouldThrowEntityNotFoundException_WhenChecklistNotFound() {
        when(checklistRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            checklistService.getChecklistData(1L);
        });

        assertEquals("Checklist not found with id 1", exception.getMessage());
    }

    @Test
    void updateChecklistStatus_ShouldReturnUpdatedChecklist() {
        checklist = new Checklist();
        checklist.setIdCh(1L);
        checklist.setStatus(StatusChecklist.REFUSE);
        checklist.setRemarque("Initial remark");
        // Mock repository behavior
        when(checklistRepository.findById(1L)).thenReturn(Optional.of(checklist));
        when(checklistRepository.save(any(Checklist.class))).thenReturn(checklist);

        // Call the service method to update the checklist
        Checklist result = checklistService.updateChecklistStatus(1L, StatusChecklist.EN_ATTENTE, "Updated remark");

        // Assertions to check that the status and remark have been updated
        assertEquals(StatusChecklist.EN_ATTENTE, result.getStatus());
        assertEquals("Updated remark", result.getRemarque());

        // Verify that the save method was called
        verify(checklistRepository, times(1)).save(checklist);
    }

    @Test
    void updateChecklistStatus_ShouldThrowIllegalArgumentException_WhenChecklistNotFound() {
        when(checklistRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            checklistService.updateChecklistStatus(1L, StatusChecklist.EN_ATTENTE, "Updated remark");
        });

        assertEquals("Invalid checklist ID", exception.getMessage());
    }

    @Test
    void updateChecklistItems_ShouldUpdateItems() {
        ChecklistItem item = new ChecklistItem();
        item.setIdCi(1L);
        item.setResultat("Old Result");
        item.setCommentaire("Old Comment");

        checklist = new Checklist();
        checklist.setIdCh(1L);
        checklist.setItems(Arrays.asList(item));
        // Mock repository behavior
        when(checklistRepository.findById(1L)).thenReturn(Optional.of(checklist));

        // Create an updated item with the same ID as the existing item
        ChecklistItem updatedItem = new ChecklistItem();
        updatedItem.setIdCi(1L);
        updatedItem.setResultat("Result");
        updatedItem.setCommentaire("Comment");

        // Call the service method
        checklistService.updateChecklistItems(1L, Arrays.asList(updatedItem));

        // Verify that the existing item was updated
        assertEquals("Result", checklist.getItems().get(0).getResultat());
        assertEquals("Comment", checklist.getItems().get(0).getCommentaire());

        // Verify that the repository saveAll method was called
        verify(itemChecklistRepository, times(1)).saveAll(checklist.getItems());
    }

    @Test
    void updateChecklistItems_ShouldThrowIllegalArgumentException_WhenChecklistNotFound() {
        when(checklistRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            checklistService.updateChecklistItems(1L, Arrays.asList(new ChecklistItem()));
        });

        assertEquals("Invalid checklist ID", exception.getMessage());
    }

    @Test
    void findByPhaseId_ShouldReturnChecklist() {
        when(checklistRepository.findByPhaseIdPh(1L)).thenReturn(Optional.of(checklist));

        Checklist result = checklistService.findByPhaseId(1L);

        assertNotNull(result);
        assertEquals(checklist.getIdCh(), result.getIdCh());
    }

    @Test
    void findByPhaseId_ShouldReturnNull_WhenChecklistNotFound() {
        when(checklistRepository.findByPhaseIdPh(1L)).thenReturn(Optional.empty());

        Checklist result = checklistService.findByPhaseId(1L);

        assertNull(result);
    }
}
