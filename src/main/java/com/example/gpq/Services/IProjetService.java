package com.example.gpq.Services;

import com.example.gpq.DTO.SatisfactionDataDTO;
import com.example.gpq.Entities.Activite;
import com.example.gpq.Entities.Projet;
import com.example.gpq.Entities.User;

import java.util.List;
import java.util.Optional;

public interface IProjetService {
    void ajouterProjetAvecAffectation(User utilisateurConnecte, Projet projet,
                                      String chefDeProjetNom, String responsableQualiteNom);
    List<Projet> findByActivite(Activite activite);
    List<SatisfactionDataDTO> getSatisfactionDataForActivity(Long activiteId);
    Optional<Projet> findById(Long id);
    void save(Projet projet);
    void delete(Projet projet);
}