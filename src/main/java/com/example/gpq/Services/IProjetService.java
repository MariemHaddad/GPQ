package com.example.gpq.Services;

import com.example.gpq.Entities.Projet;
import com.example.gpq.Entities.User;

public interface IProjetService {
    void ajouterProjetAvecAffectation(User utilisateurConnecte, Projet projet,
                                      Long chefDeProjetId, Long responsableQualiteId);

}
