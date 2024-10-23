package com.example.gpq.Services;

import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnalyseCausaleServiceImplTest {

    @InjectMocks
    private AnalyseCausaleServiceImpl analyseCausaleService;

    @Mock
    private AnalyseCausaleRepository analyseCausaleRepository;

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private PourquoiRepository pourquoiRepository;

    @Mock
    private CauseIshikawaRepository causeIshikawaRepository;

    @Mock
    private PlanActionRepository planActionRepository;

    @Mock
    private PlanActionServiceImpl planActionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAnalyseCausale() {
        AnalyseCausale analyseCausale = new AnalyseCausale();
        analyseCausale.setMethodeAnalyse(MethodeAnalyse.FIVE_WHYS);

        Pourquoi pourquoi = new Pourquoi();
        pourquoi.setAction("Action description");
        analyseCausale.setCinqPourquoi(Collections.singletonList(pourquoi));

        analyseCausaleService.saveAnalyseCausale(analyseCausale);

        // Verify the repository save method was called
        verify(analyseCausaleRepository, times(1)).save(analyseCausale);
        assertNotNull(analyseCausale.getPlanAction());
        assertEquals(1, analyseCausale.getPlanAction().getActions().size());
        assertEquals("Action description", analyseCausale.getPlanAction().getActions().get(0).getDescription());
    }

    @Test
    void testGetChecklistById_Found() {
        Checklist checklist = new Checklist();
        checklist.setIdCh(1L);

        when(checklistRepository.findById(1L)).thenReturn(Optional.of(checklist));

        Checklist result = analyseCausaleService.getChecklistById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdCh());
    }

    @Test
    void testGetChecklistById_NotFound() {
        when(checklistRepository.findById(1L)).thenReturn(Optional.empty());

        Checklist result = analyseCausaleService.getChecklistById(1L);

        assertNull(result);
    }

    @Test
    void testGetAnalyseCausaleByChecklist_Found() {
        AnalyseCausale analyseCausale = new AnalyseCausale();
        analyseCausale.setIdAN(1L);

        when(analyseCausaleRepository.findByChecklistIdCh(1L)).thenReturn(Optional.of(analyseCausale));

        AnalyseCausale result = analyseCausaleService.getAnalyseCausaleByChecklist(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdAN());
    }

    @Test
    void testGetAnalyseCausaleByChecklist_NotFound() {
        when(analyseCausaleRepository.findByChecklistIdCh(1L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            analyseCausaleService.getAnalyseCausaleByChecklist(1L);
        });

        assertEquals("Analyse causale not found for checklist id 1", exception.getMessage());
    }

    @Test
    void testAddPourquoi() {
        AnalyseCausale analyseCausale = new AnalyseCausale();
        analyseCausale.setIdAN(1L);

        Pourquoi pourquoi = new Pourquoi();
        pourquoi.setQuestion("Pourquoi description");

        when(analyseCausaleRepository.findById(1L)).thenReturn(Optional.of(analyseCausale));
        when(pourquoiRepository.save(pourquoi)).thenReturn(pourquoi);

        Pourquoi result = analyseCausaleService.addPourquoi(1L, pourquoi);

        assertNotNull(result);
        assertEquals(analyseCausale, pourquoi.getAnalyseCausale());
        verify(pourquoiRepository, times(1)).save(pourquoi);
    }

    @Test
    void testDeleteAnalyseCausale() {
        analyseCausaleService.deleteAnalyseCausale(1L);

        // Verify the deleteById method was called
        verify(analyseCausaleRepository, times(1)).deleteById(1L);
    }
    @Test
    void addCauseIshikawa_ShouldReturnSavedCauseIshikawa_WhenValidInput() {
        // Arrange
        Long analyseCausaleId = 1L;
        CauseIshikawa newCauseIshikawa = CauseIshikawa.builder()
                .description("Test Cause")
                .categorie(CategorieIshikawa.MANAGEMENT) // Assuming CATEGORIE_1 is a valid enum value
                .pourcentage(25.0)
                .action("Test Action")
                .build();

        // Mocking a valid AnalyseCausale to return
        AnalyseCausale mockAnalyseCausale = new AnalyseCausale();
        mockAnalyseCausale.setIdAN(analyseCausaleId); // Set the ID to match

        // Mock the repository to return the mock AnalyseCausale when searched by ID
        when(analyseCausaleRepository.findById(analyseCausaleId)).thenReturn(Optional.of(mockAnalyseCausale));

        // Mock the repository to return the saved CauseIshikawa
        when(causeIshikawaRepository.save(any(CauseIshikawa.class))).thenAnswer(invocation -> {
            CauseIshikawa savedCause = invocation.getArgument(0);
            savedCause.setId(1L); // Simulate setting the ID after saving
            return savedCause;
        });

        // Act
        CauseIshikawa result = analyseCausaleService.addCauseIshikawa(analyseCausaleId, newCauseIshikawa);

        // Assert
        assertNotNull(result, "Expected a non-null CauseIshikawa");
        assertEquals(newCauseIshikawa.getDescription(), result.getDescription(), "Expected matching description");
        assertEquals(newCauseIshikawa.getCategorie(), result.getCategorie(), "Expected matching category");
        assertEquals(newCauseIshikawa.getPourcentage(), result.getPourcentage(), "Expected matching percentage");
        assertEquals(newCauseIshikawa.getAction(), result.getAction(), "Expected matching action");
        assertNotNull(result.getId(), "Expected ID to be set after saving");

        // Verify that the methods were called as expected
        verify(analyseCausaleRepository, times(1)).findById(analyseCausaleId);
        verify(causeIshikawaRepository, times(1)).save(any(CauseIshikawa.class));
    }

}
