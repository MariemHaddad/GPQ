package com.example.gpq.Services;

import com.example.gpq.DTO.DDEDataDTO;
import com.example.gpq.DTO.RunSemestrielDTO;
import com.example.gpq.DTO.SatisfactionDataDTO;
import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.ProjetRepository;
import com.example.gpq.Repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProjetServiceImpl implements IProjetService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ProjetRepository projetRepository;
    @Override
    public List<Projet> findAll() {  // Modifiez ici pour correspondre à la méthode de l'interface
        return projetRepository.findAll();
    }
    public void ajouterProjetAvecAffectation(User utilisateurConnecte, Projet projet,
                                             String chefDeProjetNom, String responsableQualiteNom) {
        // Vérifier le rôle de l'utilisateur connecté
        if (utilisateurConnecte.getRole() == Role.DIRECTEUR) {
            // Si c'est un directeur, il doit choisir à la fois le chef de projet et le responsable qualité
            User chefDeProjet = userService.findByNom(chefDeProjetNom);
            if (chefDeProjet == null || !chefDeProjet.getRole().equals(Role.CHEFDEPROJET)) {
                throw new IllegalArgumentException("Chef de projet non trouvé ou invalide avec le nom spécifié");
            }

            User responsableQualite = userService.findByNom(responsableQualiteNom);
            if (responsableQualite == null || !responsableQualite.getRole().equals(Role.RQUALITE)) {
                throw new IllegalArgumentException("Responsable qualité non trouvé ou invalide avec le nom spécifié");
            }

            // Attribuer les utilisateurs récupérés au projet
            attribuerUtilisateursAuProjet(projet, chefDeProjet, responsableQualite);
        } else if (utilisateurConnecte.getRole() == Role.CHEFDEPROJET) {
            // Si c'est un chef de projet, il doit choisir seulement le responsable qualité
            User responsableQualite = userService.findByNom(responsableQualiteNom);
            if (responsableQualite == null || !responsableQualite.getRole().equals(Role.RQUALITE)) {
                throw new IllegalArgumentException("Responsable qualité non trouvé ou invalide avec le nom spécifié");
            }

            // Attribuer le chef de projet (l'utilisateur connecté) et le responsable qualité au projet
            attribuerUtilisateursAuProjet(projet, utilisateurConnecte, responsableQualite);
        } else {
            // Gérer le cas où l'utilisateur n'a pas le rôle approprié pour ajouter un projet
            throw new IllegalArgumentException("L'utilisateur n'a pas le rôle approprié pour ajouter un projet");
        }
    }
    @Override
    public void save(Projet projet) {
        projetRepository.save(projet); // Save the project using the repository
    }
    @Override
    public double calculerDDEPourProjet(Long projetId) {
        Optional<Projet> projetOpt = projetRepository.findById(projetId);
        if (projetOpt.isEmpty()) {
            throw new RuntimeException("Projet introuvable avec ID: " + projetId);
        }

        Projet projet = projetOpt.get();
        return projet.getDDE(); // Utilisation de la méthode getDDE de l'entité Projet
    }

    @Override
    public List<DDEDataDTO> calculerDDEPourActivite(Long activiteId) {
        List<Projet> projets = projetRepository.findByActiviteIdA(activiteId);
        return projets.stream()
                .map(projet -> new DDEDataDTO(projet.getIdP(), projet.getNomP(), projet.getDDE()))
                .collect(Collectors.toList());
    }

    @Override
    public List<RunSemestrielDTO> getRunsSemestriels(Long activiteId) {
        List<Projet> projets = projetRepository.findByActiviteIdA(activiteId);

        Map<String, Integer> runMap = new HashMap<>();

        for (Projet projet : projets) {
            String semestre = projet.getSemester();
            int currentRuns = runMap.getOrDefault(semestre, 0);

            // Conversion explicite de Long en int
            int nombreRuns = projet.getNombreRuns() != null ? projet.getNombreRuns().intValue() : 0;

            runMap.put(semestre, currentRuns + nombreRuns);
        }

        return runMap.entrySet().stream()
                .map(entry -> new RunSemestrielDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    @Override
    public double getTauxCByProjet(Long idProjet) {
        Projet projet = projetRepository.findById(idProjet).orElse(null);
        return projet != null ? projet.getTauxC() : 0.0;
    }

    @Override
    public Map<String, List<Double>> getTauxCBySemestre(Long activiteId) {
        // Récupérer tous les projets de l'activité
        List<Projet> projets = projetRepository.findByActiviteIdA(activiteId);
        Map<String, List<Double>> tauxSemestriels = new HashMap<>();

        for (Projet projet : projets) {
            String semestre = projet.getSemester();
            double tauxC = projet.getTauxC();

            tauxSemestriels.putIfAbsent(semestre, new ArrayList<>());
            tauxSemestriels.get(semestre).add(tauxC);
        }

        return tauxSemestriels;
    }
    @Override
    public Map<String, Double> getTauxLiberationSemestriel(Long activiteId) {
        Map<String, Double> tauxLiberationSemestriel = new HashMap<>();

        // Récupérer tous les projets liés à l'activité
        List<Projet> projets = projetRepository.findByActiviteIdA(activiteId);

        // Calculer le taux de libération par semestre
        for (Projet projet : projets) {
            String semestre = projet.getSemester(); // Assurez-vous que cette méthode existe et retourne le semestre du projet
            double tauxLiberation = projet.getTauxLiberation();

            tauxLiberationSemestriel.putIfAbsent(semestre, 0.0);
            tauxLiberationSemestriel.put(semestre, tauxLiberationSemestriel.get(semestre) + tauxLiberation);
        }

        // Calculer la moyenne par semestre
        for (String semestre : tauxLiberationSemestriel.keySet()) {
            int countProjets = (int) projets.stream().filter(projet -> projet.getSemester().equals(semestre)).count();
            if (countProjets > 0) {
                tauxLiberationSemestriel.put(semestre, tauxLiberationSemestriel.get(semestre) / countProjets);
            }
        }

        return tauxLiberationSemestriel;
    }
    @Override
    public Map<String, List<Double>> getTauxRealisation8DParSemestre(Long activiteId) {
        // Récupération de tous les projets liés à l'activité
        List<Projet> projets = projetRepository.findByActiviteIdA(activiteId);

        if (projets.isEmpty()) {
            throw new EntityNotFoundException("Aucun projet trouvé pour cette activité.");
        }

        Map<String, List<Double>> tauxParSemestre = new HashMap<>();

        // Itérer à travers chaque projet pour regrouper par semestre et ajouter le taux de réalisation
        for (Projet projet : projets) {
            String semester = projet.getSemester();
            tauxParSemestre.putIfAbsent(semester, new ArrayList<>());
            tauxParSemestre.get(semester).add(projet.getTauxRealisation8D());
        }

        return tauxParSemestre;
    }

    @Override
    public void delete(Projet projet) {
        if (projet != null && projet.getIdP() != null) {
            projetRepository.deleteById(projet.getIdP()); // Delete by project ID
        } else {
            throw new IllegalArgumentException("Cannot delete a project with a null ID.");
        }
    }
    private void attribuerUtilisateursAuProjet(Projet projet, User chefDeProjet, User responsableQualite) {
        // Assurez-vous que les utilisateurs sélectionnés ne sont pas null avant de les attribuer au projet
        if (chefDeProjet != null && responsableQualite != null) {
            projet.setChefDeProjet(chefDeProjet);
            projet.setResponsableQualite(responsableQualite);
            projetRepository.save(projet);
        } else {
            // Gérer le cas où aucun utilisateur approprié n'est disponible
            throw new IllegalStateException("Impossible d'attribuer les utilisateurs au projet : utilisateurs indisponibles");
        }
    }

    public List<Projet> findByActivite(Activite activite) {
        List<Projet> projets = projetRepository.findByActivite(activite);
        for (Projet projet : projets) {
            if (projet.getResponsableQualite() != null) {
                projet.setResponsableQualiteNom(projet.getResponsableQualite().getNom());
            }
            if (projet.getChefDeProjet() != null) {
                projet.setChefDeProjetNom(projet.getChefDeProjet().getNom());
            }
        }
        return projets;
    }
    @Override
    public Optional<Projet> findById(Long id) {
        return projetRepository.findById(id);
    }
    @Override
    public List<SatisfactionDataDTO> getSatisfactionDataForActivity(Long activiteId) {
        List<Projet> projects = projetRepository.findByActiviteIdA(activiteId);

        Map<String, SatisfactionDataDTO> satisfactionDataMap = new TreeMap<>(); // Utiliser une TreeMap pour trier par semestre

        for (Projet project : projects) {
            String semester = project.getSemester(); // Calcul du semestre
            SatisfactionDataDTO data = satisfactionDataMap.getOrDefault(semester, new SatisfactionDataDTO(semester, 0.0, 0.0));

            // Vérifiez le type de satisfaction et ajoutez la valeur appropriée
            if (project.getSatisfactionClient() == TypeSatisfaction.SI1) {
                data.setSi1Value(data.getSi1Value() + project.getValeurSatisfaction());
            } else if (project.getSatisfactionClient() == TypeSatisfaction.SI2) {
                data.setSi2Value(data.getSi2Value() + project.getValeurSatisfaction());
            }

            satisfactionDataMap.put(semester, data);
        }

        return new ArrayList<>(satisfactionDataMap.values());}
    public void addSatisfaction(Long projectId, Double satisfactionValue, TypeSatisfaction type) {
        Projet projet = projetRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));

        if (type == TypeSatisfaction.SI1) {
            projet.setValeurSatisfaction(satisfactionValue); // Assurez-vous que cela fonctionne pour SI1
        } else if (type == TypeSatisfaction.SI2) {
            projet.setValeurSatisfaction(satisfactionValue); // Vous aurez peut-être besoin d'une variable différente pour SI2
        }

        projetRepository.save(projet);
    }
}
