package com.example.gpq.Repositories;


import com.example.gpq.Entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
        Optional<Client> findByNomC(String nomC);}