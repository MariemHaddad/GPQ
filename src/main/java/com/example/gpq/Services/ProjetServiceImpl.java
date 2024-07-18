package com.example.gpq.Services;

import com.example.gpq.Entities.Activite;
import com.example.gpq.Entities.Projet;
import com.example.gpq.Entities.Role;
import com.example.gpq.Entities.User;
import com.example.gpq.Repositories.ProjetRepository;
import com.example.gpq.Repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}