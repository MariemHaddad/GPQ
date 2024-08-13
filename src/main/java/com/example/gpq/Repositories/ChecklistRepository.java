package com.example.gpq.Repositories;

import com.example.gpq.Entities.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
    Optional<Checklist> findByPhaseIdPh(Long phaseId);  // Ajustez ici pour correspondre à `idPh`
}