package com.example.gpq.Services;

import com.example.gpq.Entities.Activite;
import com.example.gpq.Repositories.ActiviteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ActiviteServiceImpl  implements IActiviteService {
    @Autowired
private ActiviteRepository activiteRepository;

    @Override
    public void ajouterActivite(Activite activite) {
        activiteRepository.save(activite);
    }
    @Override
    public void modifierActivite(Long id, Activite activite) {
        Optional<Activite> optionalActivite = activiteRepository.findById(id);
        if (optionalActivite.isPresent()) {
            Activite existingActivite = optionalActivite.get();
            existingActivite.setNomA(activite.getNomA());
            // Répétez cela pour tous les champs que vous souhaitez mettre à jour

            activiteRepository.save(existingActivite);
        } else {
            // Gérer le cas où aucune activité avec cet ID n'est trouvée
            // Par exemple, vous pouvez lever une exception ou renvoyer un message d'erreur
        }
    }

    @Override
    public void supprimerActivite(Long id) {
        activiteRepository.deleteById(id);
    }
}
