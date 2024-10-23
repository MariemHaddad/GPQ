package com.example.gpq.Services;

import com.example.gpq.DTO.RunSemestrielDTO;
import com.example.gpq.DTO.SatisfactionDataDTO;
import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.ProjetRepository;
import com.example.gpq.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProjetServiceImplTest {

    @InjectMocks
    private ProjetServiceImpl projetService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjetRepository projetRepository;

    @Mock
    private UserServiceImpl userService;

    private Projet projet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        projet = Projet.builder()
                .idP(1L)
                .nomP("Test Project")
                .datedebutP(new Date())
                .datefinP(new Date())
                .defautInternes(5L)
                .defautTotaux(10L)
                .nbr8DRealises(3L)
                .nbrRetoursCritiques(4L)
                .build();
    }

    @Test
    void testFindAll() {
        List<Projet> projets = Collections.singletonList(projet);
        when(projetRepository.findAll()).thenReturn(projets);

        List<Projet> result = projetService.findAll();

        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getNomP());
    }

    @Test
    void testAjouterProjetAvecAffectation_directeurRole() {
        User directeur = new User();
        directeur.setRole(Role.DIRECTEUR);

        User chefDeProjet = new User();
        chefDeProjet.setRole(Role.CHEFDEPROJET);
        User responsableQualite = new User();
        responsableQualite.setRole(Role.RQUALITE);

        when(userService.findByNom("ChefNom")).thenReturn(chefDeProjet);
        when(userService.findByNom("QualiteNom")).thenReturn(responsableQualite);

        projetService.ajouterProjetAvecAffectation(directeur, projet, "ChefNom", "QualiteNom");

        verify(projetRepository, times(1)).save(projet);
    }

    @Test
    void testAjouterProjetAvecAffectation_invalidChefDeProjet() {
        User directeur = new User();
        directeur.setRole(Role.DIRECTEUR);

        when(userService.findByNom("ChefNom")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            projetService.ajouterProjetAvecAffectation(directeur, projet, "ChefNom", "QualiteNom");
        });
    }

    @Test
    void testCalculerDDEPourProjet() {
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));

        double dde = projetService.calculerDDEPourProjet(1L);

        assertEquals(50.0, dde);
    }

    @Test
    void testCalculerDDEPourProjet_notFound() {
        when(projetRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> projetService.calculerDDEPourProjet(1L));
    }



    @Test
    void testGetRunsSemestriels() {
        projet.setNombreRuns(10L);
        List<Projet> projets = Collections.singletonList(projet);
        when(projetRepository.findByActiviteIdA(1L)).thenReturn(projets);

        List<RunSemestrielDTO> result = projetService.getRunsSemestriels(1L);

        assertEquals(1, result.size());
        assertEquals("S2-2024", result.get(0).getSemestre());
        assertEquals(10, result.get(0).getTotalRuns());
    }

    @Test
    void testGetTauxCByProjet() {
        Phase phase1 = new Phase();
        phase1.setStatusLivraisonExterne(EtatLivraison.C);

        Phase phase2 = new Phase();
        phase2.setStatusLivraisonExterne(EtatLivraison.NC);

        Phase phase3 = new Phase();
        phase3.setStatusLivraisonExterne(EtatLivraison.C);

        projet = new Projet();
        projet.setPhases(Arrays.asList(phase1, phase2, phase3));
        when(projetRepository.findById(1L)).thenReturn(Optional.of(projet));

        // Execute the method to test
        double tauxC = projetService.getTauxCByProjet(1L);

        // Verify the result
        assertEquals(66.67, tauxC, 0.01); // The TauxC should be (2/3) * 100 = 66.67%
    }


    @Test
    void getTauxCBySemestre_ShouldReturnEmptyMapWhenNoProjects() {
        // Arrange
        Long activiteId = 1L;

        // Mock the repository to return an empty list
        when(projetRepository.findByActiviteIdA(activiteId)).thenReturn(Arrays.asList());

        // Act
        Map<String, List<Double>> result = projetService.getTauxCBySemestre(activiteId);

        // Assert
        assertEquals(0, result.size());

        // Verify that the repository method was called
        verify(projetRepository, times(1)).findByActiviteIdA(activiteId);
    }

    @Test
    void testGetTauxLiberationSemestriel() {
        // Create mock phases
        Phase phase1 = new Phase();
        phase1.setDescription("Livraison");
        Checklist checklist1 = new Checklist();
        checklist1.setStatus(StatusChecklist.ACCEPTE);
        phase1.setChecklist(checklist1);

        Phase phase2 = new Phase();
        phase2.setDescription("Livraison");
        Checklist checklist2 = new Checklist();
        checklist2.setStatus(StatusChecklist.REFUSE);
        phase2.setChecklist(checklist2);

        Phase phase3 = new Phase();
        phase3.setDescription("Livraison");
        Checklist checklist3 = new Checklist();
        checklist3.setStatus(StatusChecklist.ACCEPTE);
        phase3.setChecklist(checklist3);

        // Set a valid start date for the project
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.FEBRUARY, 1); // Set to a date in the first semester of 2024
        Date startDate = calendar.getTime();

        // Add the phases to the mock projet
        Projet projet = new Projet();
        projet.setDatedebutP(startDate); // Set the start date
        projet.setPhases(Arrays.asList(phase1, phase2, phase3)); // Initialize the phases list

        // Mock the repository behavior
        when(projetRepository.findByActiviteIdA(1L)).thenReturn(Collections.singletonList(projet));

        // Execute the method to test
        Map<String, Double> result = projetService.getTauxLiberationSemestriel(1L);

        // Verify the result
        assertEquals(1, result.size());
        assertEquals(133.33, result.get("S1-2024"), 0.01); // The tauxLiberation should be ((2 / 3) * 2) * 100 = 133.33
    }

    @Test
    void testGetTauxRealisation8DParSemestre() {
        List<Projet> projets = Collections.singletonList(projet);
        when(projetRepository.findByActiviteIdA(1L)).thenReturn(projets);

        Map<String, List<Double>> result = projetService.getTauxRealisation8DParSemestre(1L);

        assertEquals(1, result.size());
    }

    @Test
    void delete_ShouldDeleteProjet_WhenValid() {
        // Arrange
        Long projetId = 1L;
        Projet projet = new Projet();
        projet.setIdP(projetId);

        // Act
        projetService.delete(projet);

        // Assert
        verify(projetRepository, times(1)).deleteById(projetId);
    }

    @Test
    void delete_ShouldThrowException_WhenProjetIsNull() {
        // Arrange
        Projet projet = null;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> projetService.delete(projet));
        assertEquals("Cannot delete a project with a null ID.", exception.getMessage());
    }
    @Test
    void findById_ShouldReturnProjet_WhenExists() {
        // Arrange
        Long projetId = 1L;
        Projet projet = new Projet();
        projet.setIdP(projetId);

        // Mock the repository to return the projet
        when(projetRepository.findById(projetId)).thenReturn(Optional.of(projet));

        // Act
        Optional<Projet> result = projetService.findById(projetId);

        // Assert
        assertTrue(result.isPresent(), "Expected Projet to be present");
        assertEquals(projetId, result.get().getIdP(), "Expected Projet ID to match");

        // Verify that the repository method was called
        verify(projetRepository, times(1)).findById(projetId);
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        Long projetId = 2L;

        // Mock the repository to return an empty Optional
        when(projetRepository.findById(projetId)).thenReturn(Optional.empty());

        // Act
        Optional<Projet> result = projetService.findById(projetId);

        // Assert
        assertFalse(result.isPresent(), "Expected Projet to be absent");

        // Verify that the repository method was called
        verify(projetRepository, times(1)).findById(projetId);
    }

    @Test
    void getSatisfactionDataForActivity_ShouldReturnEmptyList_WhenNoProjects() {
        // Arrange
        Long activiteId = 1L;

        // Mock the repository to return an empty list
        when(projetRepository.findByActiviteIdA(activiteId)).thenReturn(Collections.emptyList());

        // Act
        List<SatisfactionDataDTO> result = projetService.getSatisfactionDataForActivity(activiteId);

        // Assert
        assertTrue(result.isEmpty(), "Expected an empty list when no projects are found");

        // Verify that the repository method was called
        verify(projetRepository, times(1)).findByActiviteIdA(activiteId);
    }

    @Test
    void getSatisfactionDataForActivity_ShouldReturnCorrectData_WhenProjectsExist() {
        // Arrange
        Long activiteId = 2L;

        // Create sample projects
        Projet project1 = new Projet();
        project1.setDatedebutP(new Date(2024 - 1900, 0, 1)); // January 1, 2024
        project1.setSatisfactionClient(TypeSatisfaction.SI1);
        project1.setValeurSatisfaction(80.0);

        Projet project2 = new Projet();
        project2.setDatedebutP(new Date(2024 - 1900, 0, 1)); // January 1, 2024

        project2.setSatisfactionClient(TypeSatisfaction.SI2);
        project2.setValeurSatisfaction(90.0);

        Projet project3 = new Projet();
        project3.setDatedebutP(new Date(2024 - 1900, 6, 1)); // July 1, 2024

        project3.setSatisfactionClient(TypeSatisfaction.SI1);
        project3.setValeurSatisfaction(85.0);

        List<Projet> projects = Arrays.asList(project1, project2, project3);

        // Mock the repository to return the list of projects
        when(projetRepository.findByActiviteIdA(activiteId)).thenReturn(projects);

        // Act
        List<SatisfactionDataDTO> result = projetService.getSatisfactionDataForActivity(activiteId);

        // Assert
        assertEquals(2, result.size(), "Expected two semesters in the result");

        // Check S1-2024 data
        SatisfactionDataDTO s1Data = result.stream().filter(data -> "S1-2024".equals(data.getSemester())).findFirst().orElse(null);
        assertNotNull(s1Data, "Expected to find satisfaction data for S1-2024");
        assertEquals(80.0, s1Data.getSi1Value(), "Expected SI1 value to be 80.0");
        assertEquals(90.0, s1Data.getSi2Value(), "Expected SI2 value to be 90.0");

        // Check S2-2024 data
        SatisfactionDataDTO s2Data = result.stream().filter(data -> "S2-2024".equals(data.getSemester())).findFirst().orElse(null);
        assertNotNull(s2Data, "Expected to find satisfaction data for S2-2024");
        assertEquals(85.0, s2Data.getSi1Value(), "Expected SI1 value to be 85.0");
        assertEquals(0.0, s2Data.getSi2Value(), "Expected SI2 value to be 0.0");

        // Verify that the repository method was called
        verify(projetRepository, times(1)).findByActiviteIdA(activiteId);
    }
    @Test
    void findByActivite_ShouldReturnEmptyList_WhenNoProjects() {
        // Arrange
        Activite activite = new Activite(); // Create an Activite object as needed

        // Mock the repository to return an empty list
        when(projetRepository.findByActivite(activite)).thenReturn(Collections.emptyList());

        // Act
        List<Projet> result = projetService.findByActivite(activite);

        // Assert
        assertTrue(result.isEmpty(), "Expected an empty list when no projects are found");

        // Verify that the repository method was called
        verify(projetRepository, times(1)).findByActivite(activite);
    }

    @Test
    void findByActivite_ShouldReturnProjectsWithResponsableQualiteAndChefDeProjetNames() {
        // Arrange
        Activite activite = new Activite(); // Create an Activite object as needed

        // Create sample projects with their managers represented as strings
        Projet project1 = new Projet();
        project1.setResponsableQualiteNom("Quality Manager");
        project1.setChefDeProjetNom("Project Manager");

        Projet project2 = new Projet();
        project2.setResponsableQualiteNom(null); // No quality manager
        project2.setChefDeProjetNom("Project Manager");

        Projet project3 = new Projet();
        project3.setResponsableQualiteNom("Quality Manager"); // Quality manager exists
        project3.setChefDeProjetNom(null); // No project manager

        List<Projet> projects = Arrays.asList(project1, project2, project3);

        // Mock the repository to return the list of projects
        when(projetRepository.findByActivite(activite)).thenReturn(projects);

        // Act
        List<Projet> result = projetService.findByActivite(activite);

        // Assert
        assertEquals(3, result.size(), "Expected to find three projects");

        // Verify the names are set correctly
        assertEquals("Quality Manager", result.get(0).getResponsableQualiteNom(), "Expected name of quality manager to be set");
        assertEquals("Project Manager", result.get(0).getChefDeProjetNom(), "Expected name of project manager to be set");

        assertNull(result.get(1).getResponsableQualiteNom(), "Expected no name for quality manager");
        assertEquals("Project Manager", result.get(1).getChefDeProjetNom(), "Expected name of project manager to be set");

        assertEquals("Quality Manager", result.get(2).getResponsableQualiteNom(), "Expected name of quality manager to be set");
        assertNull(result.get(2).getChefDeProjetNom(), "Expected no name for project manager");

        // Verify that the repository method was called
        verify(projetRepository, times(1)).findByActivite(activite);
    }
}
