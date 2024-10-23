package com.example.gpq.Services;

import com.example.gpq.Entities.Action;
import com.example.gpq.Entities.PlanAction;
import com.example.gpq.Repositories.ActionRepository;
import com.example.gpq.Repositories.PlanActionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlanActionServiceImplTest {

    @InjectMocks
    private PlanActionServiceImpl planActionService;

    @Mock
    private PlanActionRepository planActionRepository;

    @Mock
    private ActionRepository actionRepository;

    private PlanAction planAction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        planAction = PlanAction.builder()
                .idPa(1L)
                .leconTirees("Leçon 1")
                .build();
    }

    @Test
    void testSavePlanActions() {
        List<PlanAction> planActions = Arrays.asList(planAction);
        when(planActionRepository.saveAll(planActions)).thenReturn(planActions);

        List<PlanAction> savedActions = planActionService.savePlanActions(planActions);

        assertNotNull(savedActions);
        assertEquals(1, savedActions.size());
        assertEquals("Leçon 1", savedActions.get(0).getLeconTirees());
        verify(planActionRepository, times(1)).saveAll(planActions);
    }

    @Test
    void testGetPlanActionById() {
        when(planActionRepository.findById(1L)).thenReturn(Optional.of(planAction));

        PlanAction foundAction = planActionService.getPlanActionById(1L);

        assertNotNull(foundAction);
        assertEquals("Leçon 1", foundAction.getLeconTirees());
        verify(planActionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllPlansAction() {
        when(planActionRepository.findAll()).thenReturn(Arrays.asList(planAction));

        List<PlanAction> allActions = planActionService.getAllPlansAction();

        assertNotNull(allActions);
        assertEquals(1, allActions.size());
        verify(planActionRepository, times(1)).findAll();
    }

    @Test
    void testUpdatePlanAction() {
        when(planActionRepository.findById(1L)).thenReturn(Optional.of(planAction));
        PlanAction updatedAction = PlanAction.builder()
                .leconTirees("Updated Lesson")
                .build();

        when(planActionRepository.save(any(PlanAction.class))).thenReturn(updatedAction);

        PlanAction result = planActionService.updatePlanAction(1L, updatedAction);

        assertNotNull(result);
        assertEquals("Updated Lesson", result.getLeconTirees());
        verify(planActionRepository, times(1)).findById(1L);
        verify(planActionRepository, times(1)).save(any(PlanAction.class));
    }

    @Test
    void testUpdatePlanActionNotFound() {
        when(planActionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            planActionService.updatePlanAction(1L, planAction);
        });

        verify(planActionRepository, times(1)).findById(1L);
    }

    @Test
    void testDeletePlanAction() {
        doNothing().when(planActionRepository).deleteById(1L);

        planActionService.deletePlanAction(1L);

        verify(planActionRepository, times(1)).deleteById(1L);
    }

    @Test
    void testSaveAction() {
        Action action = new Action();
        when(actionRepository.save(action)).thenReturn(action);

        Action savedAction = planActionService.saveAction(action);

        assertNotNull(savedAction);
        verify(actionRepository, times(1)).save(action);
    }

    @Test
    void testGetActionById() {
        Action action = new Action();
        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));

        Action foundAction = planActionService.getActionById(1L);

        assertNotNull(foundAction);
        verify(actionRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllActions() {
        Action action = new Action();
        when(actionRepository.findAll()).thenReturn(Arrays.asList(action));

        List<Action> actions = planActionService.getAllActions();

        assertNotNull(actions);
        assertEquals(1, actions.size());
        verify(actionRepository, times(1)).findAll();
    }

    @Test
    void testUpdateAction() {
        Action action = new Action();
        action.setId(1L);
        when(actionRepository.findById(1L)).thenReturn(Optional.of(action));

        Action actionDetails = new Action();
        actionDetails.setDescription("Updated Description");

        when(actionRepository.save(any(Action.class))).thenReturn(action);

        Action updatedAction = planActionService.updateAction(1L, actionDetails);

        assertNotNull(updatedAction);
        assertEquals("Updated Description", updatedAction.getDescription());
        verify(actionRepository, times(1)).findById(1L);
        verify(actionRepository, times(1)).save(any(Action.class));
    }

    @Test
    void testDeleteAction() {
        doNothing().when(actionRepository).deleteById(1L);

        planActionService.deleteAction(1L);

        verify(actionRepository, times(1)).deleteById(1L);
    }

    @Test
    void getPlanActionByAnalyseCausaleId_ShouldReturnPlanAction_WhenFound() {
        // Arrange
        Long idAN = 1L;
        PlanAction expectedPlanAction = PlanAction.builder()
                .idPa(1L)
                .leconTirees("Sample Lesson")
                .build();

        // Mock the repository to return a PlanAction
        when(planActionRepository.findByAnalyseCausaleIdAN(idAN)).thenReturn(expectedPlanAction);

        // Act
        PlanAction result = planActionService.getPlanActionByAnalyseCausaleId(idAN);

        // Assert
        assertNotNull(result, "Expected a non-null PlanAction");
        assertEquals(expectedPlanAction.getIdPa(), result.getIdPa(), "Expected matching PlanAction ID");
        assertEquals(expectedPlanAction.getLeconTirees(), result.getLeconTirees(), "Expected matching lesson string");

        // Verify that the repository method was called
        verify(planActionRepository, times(1)).findByAnalyseCausaleIdAN(idAN);
    }

    @Test
    void getPlanActionByAnalyseCausaleId_ShouldReturnNull_WhenNotFound() {
        // Arrange
        Long idAN = 2L;

        // Mock the repository to return null when no PlanAction is found
        when(planActionRepository.findByAnalyseCausaleIdAN(idAN)).thenReturn(null);

        // Act
        PlanAction result = planActionService.getPlanActionByAnalyseCausaleId(idAN);

        // Assert
        assertNull(result, "Expected null when no PlanAction is found");

        // Verify that the repository method was called
        verify(planActionRepository, times(1)).findByAnalyseCausaleIdAN(idAN);
    }
}
