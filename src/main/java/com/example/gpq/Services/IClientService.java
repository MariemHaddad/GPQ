package com.example.gpq.Services;

import com.example.gpq.Entities.Client;

public interface IClientService {
    Client findByNomC(String nomC);
    Client save(Client client);
}
