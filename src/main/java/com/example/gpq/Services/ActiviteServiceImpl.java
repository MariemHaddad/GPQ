package com.example.gpq.Services;

import com.example.gpq.Entities.Activite;
import com.example.gpq.Repositories.ActiviteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ActiviteServiceImpl  implements IActiviteService {
    @Autowired
private ActiviteRepository activiteRepository;

    @Override
    public void ajouterActivite(Activite activite) {
        activiteRepository.save(activite);
    }
}
