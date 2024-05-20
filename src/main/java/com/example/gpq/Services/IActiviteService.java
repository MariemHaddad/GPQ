package com.example.gpq.Services;

import com.example.gpq.Entities.Activite;

public interface IActiviteService {
    void ajouterActivite(Activite activite);
    void modifierActivite(Long id, Activite activite);
    void supprimerActivite(Long id);
}
