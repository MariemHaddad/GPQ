package com.example.gpq.Repositories;

import com.example.gpq.Entities.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemChecklistRepository extends JpaRepository<ChecklistItem, Long> {
}