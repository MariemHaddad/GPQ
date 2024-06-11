package com.example.gpq.Services;

import com.example.gpq.Entities.Activite;
import com.example.gpq.Repositories.ActiviteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor

public class ActiviteServiceImpl  implements IActiviteService {
    @Autowired
private ActiviteRepository activiteRepository;

    @Override
    public List<Activite> getAllActivites() {
        return activiteRepository.findAll();
    }
    @PreAuthorize("hasRole('ADMIN')")

    @Override
    public void ajouterActivite(Activite activite) {
        activiteRepository.save(activite);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void modifierActivite(Long id, String nouveauNom) {
        Activite activite = activiteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Activité non trouvée avec l'ID : " + id));
        activite.setNomA(nouveauNom);
        activiteRepository.save(activite);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void supprimerActivite(Long id) {
        activiteRepository.deleteById(id);
    }
    @Override
    public Activite findById(Long id) {
        return activiteRepository.findById(id).orElse(null);
    }
}

