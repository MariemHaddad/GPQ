package com.example.gpq.Services;

import com.example.gpq.Entities.Client;
import com.example.gpq.Repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService{
    @Autowired
    private ClientRepository clientRepository;
    @Override
    public List<Client> findAll() {
        return clientRepository.findAll(); // Utilisez le repository pour récupérer tous les clients
    }
    @Override
    public Client findByNomC(String nomC) {
        return clientRepository.findByNomC(nomC).orElse(null);
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }
}

