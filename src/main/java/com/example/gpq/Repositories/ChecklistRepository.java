package com.example.gpq.Repositories;

import com.example.gpq.Entities.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
}