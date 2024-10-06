package com.example.gpq.Services;

import com.example.gpq.DTO.DDEDataDTO;
import com.example.gpq.DTO.RunSemestrielDTO;
import com.example.gpq.DTO.SatisfactionDataDTO;
import com.example.gpq.Entities.Activite;
import com.example.gpq.Entities.Projet;
import com.example.gpq.Entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IProjetService {
    double calculerDDEPourProjet(Long projetId);
    List<RunSemestrielDTO> getRunsSemestriels(Long activiteId);
    List<DDEDataDTO> calculerDDEPourActivite(Long activiteId);
    void ajouterProjetAvecAffectation(User utilisateurConnecte, Projet projet,
                                      String chefDeProjetNom, String responsableQualiteNom);

    List<Projet> findByActivite(Activite activite);
    List<SatisfactionDataDTO> getSatisfactionDataForActivity(Long activiteId);
    Optional<Projet> findById(Long id);
    void save(Projet projet);
    void delete(Projet projet);


}