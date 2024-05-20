package com.example.gpq.Repositories;

import com.example.gpq.Entities.Projet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface ProjetRepository extends JpaRepository<Projet, Long> {
    // Vous pouvez ajouter des méthodes supplémentaires si nécessaire
}

