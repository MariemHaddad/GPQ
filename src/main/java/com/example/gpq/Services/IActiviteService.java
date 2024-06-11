package com.example.gpq.Services;

import com.example.gpq.Entities.Activite;

import java.util.List;

public interface IActiviteService {
    void ajouterActivite(Activite activite);
    void modifierActivite(Long id, String nouveauNom);
    void supprimerActivite(Long id);

    List<Activite> getAllActivites();
    Activite findById(Long id);
}
