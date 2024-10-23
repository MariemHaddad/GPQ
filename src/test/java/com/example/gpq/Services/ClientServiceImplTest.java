package com.example.gpq.Services;

import com.example.gpq.Entities.Client;
import com.example.gpq.Repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private ClientRepository clientRepository;

    private Client client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        client = Client.builder()
                .idC(1L)
                .nomC("Client Test")
                .build();
    }

    @Test
    void testFindAll() {
        List<Client> clients = Arrays.asList(client);

        when(clientRepository.findAll()).thenReturn(clients);

        List<Client> result = clientService.findAll();

        assertEquals(1, result.size());
        assertEquals("Client Test", result.get(0).getNomC());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void testFindByNomC_Found() {
        when(clientRepository.findByNomC("Client Test")).thenReturn(Optional.of(client));

        Client result = clientService.findByNomC("Client Test");

        assertNotNull(result);
        assertEquals("Client Test", result.getNomC());
        verify(clientRepository, times(1)).findByNomC("Client Test");
    }

    @Test
    void testFindByNomC_NotFound() {
        when(clientRepository.findByNomC("Unknown Client")).thenReturn(Optional.empty());

        Client result = clientService.findByNomC("Unknown Client");

        assertNull(result);
        verify(clientRepository, times(1)).findByNomC("Unknown Client");
    }

    @Test
    void testSave() {
        when(clientRepository.save(client)).thenReturn(client);

        Client result = clientService.save(client);

        assertNotNull(result);
        assertEquals("Client Test", result.getNomC());
        verify(clientRepository, times(1)).save(client);
    }
}
