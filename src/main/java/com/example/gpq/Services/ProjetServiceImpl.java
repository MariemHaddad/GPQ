package com.example.gpq.Services;

import com.example.gpq.DTO.SatisfactionDataDTO;
import com.example.gpq.Entities.*;
import com.example.gpq.Repositories.ProjetRepository;
import com.example.gpq.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class ProjetServiceImpl implements IProjetService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ProjetRepository projetRepository;

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
