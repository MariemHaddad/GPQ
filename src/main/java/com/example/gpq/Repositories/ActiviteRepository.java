package com.example.gpq.Repositories;

import com.example.gpq.Entities.Activite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActiviteRepository  extends JpaRepository<Activite,Long> {
    @Override
    List<Activite> findAll();
}

