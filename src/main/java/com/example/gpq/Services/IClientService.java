package com.example.gpq.Services;

import com.example.gpq.Entities.Client;

import java.util.List;

public interface IClientService {
    Client findByNomC(String nomC);
    List<Client> findAll();
    Client save(Client client);
}
