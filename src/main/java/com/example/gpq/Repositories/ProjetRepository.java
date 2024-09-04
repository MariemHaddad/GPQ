package com.example.gpq.Repositories;

import com.example.gpq.Entities.Activite;
import com.example.gpq.Entities.Projet;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface ProjetRepository extends JpaRepository<Projet, Long> {
    List<Projet> findByActivite(Activite activite);
}

