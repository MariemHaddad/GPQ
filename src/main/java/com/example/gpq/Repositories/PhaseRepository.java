package com.example.gpq.Repositories;

import com.example.gpq.Entities.Phase;
import com.example.gpq.Entities.Projet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhaseRepository extends JpaRepository<Phase, Long> {
    List<Phase> findByProjet(Projet projet);
}