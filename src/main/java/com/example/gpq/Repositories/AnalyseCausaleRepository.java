package com.example.gpq.Repositories;

import com.example.gpq.Entities.AnalyseCausale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalyseCausaleRepository extends JpaRepository<AnalyseCausale, Long> {
    Optional<AnalyseCausale> findByChecklistIdCh(Long idCh);
}