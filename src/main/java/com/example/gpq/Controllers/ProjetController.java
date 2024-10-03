package com.example.gpq.Controllers;

import com.example.gpq.DTO.TauxNCAggregator;
import com.example.gpq.DTO.TauxNCResponse;
import com.example.gpq.DTO.TauxNCSemestrielResponse;
import com.example.gpq.Entities.*;
import com.example.gpq.Services.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/projet")
@CrossOrigin(origins = "http://localhost:4200")
public class ProjetController {

    private static final Logger logger = LoggerFactory.getLogger(PhaseController.class); // Updated to PhaseController
    private final ApplicationService applicationService;
    @Autowired
    public ProjetController(ApplicationService applicationService, IProjetService projetService,
                           IChecklistService checklistService, IPhaseService phaseService) {
        this.applicationService = applicationService;}
    @Autowired
    private IProjetService projetService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IActiviteService activiteService;

    @Autowired
    private IClientService clientService;
    @GetMapping("/chefsdeprojet")
    public ResponseEntity<List<User>> getChefsDeProjet() {
        List<User> chefsDeProjet = userService.findByRole(Role.CHEFDEPROJET);
        return ResponseEntity.ok(chefsDeProjet);
    }

    @GetMapping("/responsablesqualite")
    public ResponseEntity<List<User>> getResponsablesQualite() {
        List<User> responsablesQualite = userService.findByRole(Role.RQUALITE);
        return ResponseEntity.ok(responsablesQualite);
    }

    @PostMapping("/ajouter")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('DIRECTEUR')")
    public ResponseEntity<String> ajouterProjet(
            @RequestBody Projet projet,
            @RequestParam(value = "activiteId") Long activiteId,
            @RequestParam(value = "chefDeProjetNom", required = false) String chefDeProjetNom,
            @RequestParam("responsableQualiteNom") String responsableQualiteNom,
            @RequestParam("nomC") String nomC) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User utilisateurConnecte = userService.findByEmail(username);

            Activite activite = activiteService.findById(activiteId);
            if (activite == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Activité non trouvée.");
            }

            User responsableQualite = userService.findByNom(responsableQualiteNom);
            if (responsableQualite == null || !responsableQualite.getRole().equals(Role.RQUALITE)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Responsable qualité non trouvé ou invalide.");
            }

            Client client = clientService.findByNomC(nomC);
            if (client == null) {
                client = new Client();
                client.setNomC(nomC);
                client = clientService.save(client);
            }

            projet.setActivite(activite);
            projet.setResponsableQualite(responsableQualite);
            projet.setClient(client);

            if (utilisateurConnecte.getRole().equals(Role.CHEFDEPROJET)) {
                projet.setChefDeProjet(utilisateurConnecte);
                projetService.ajouterProjetAvecAffectation(utilisateurConnecte, projet, null, responsableQualiteNom);
            } else if (utilisateurConnecte.getRole().equals(Role.DIRECTEUR)) {
                User chefDeProjet = userService.findByNom(chefDeProjetNom);
                if (chefDeProjet == null || !chefDeProjet.getRole().equals(Role.CHEFDEPROJET)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chef de projet non trouvé ou invalide.");
                }
                projet.setChefDeProjet(chefDeProjet);
                projetService.ajouterProjetAvecAffectation(utilisateurConnecte, projet, chefDeProjetNom, responsableQualiteNom);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Utilisateur non autorisé.");
            }

            return ResponseEntity.ok("Projet ajouté avec succès.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié.");
        }
    }

    @GetMapping("/activites/{activiteId}/projets")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<List<Projet>> getProjetsByActivite(@PathVariable Long activiteId) {
        Activite activite = activiteService.findById(activiteId);
        if (activite == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        List<Projet> projets = projetService.findByActivite(activite);
        return ResponseEntity.ok(projets);
    }

    @GetMapping("/activite/{activiteId}/tauxNC")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<List<TauxNCResponse>> getTauxNCTousProjets(@PathVariable Long activiteId) {
        Activite activite = activiteService.findById(activiteId);
        if (activite == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<Projet> projets = projetService.findByActivite(activite);
        List<TauxNCResponse> tauxNCList = new ArrayList<>();

        for (Projet projet : projets) {
            double tauxNCInterne = getTauxNCInterne(projet.getIdP()).getBody(); // Update if getId() is renamed
            double tauxNCExterne = getTauxNCExterne(projet.getIdP()).getBody(); // Update if getId() is renamed

            tauxNCList.add(new TauxNCResponse(projet.getIdP(), projet.getNomP(), tauxNCInterne, tauxNCExterne));
        }

        return ResponseEntity.ok(tauxNCList);
    }  @GetMapping("/projet/{projetId}/tauxNCExterne")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<Double> getTauxNCExterne(@PathVariable Long projetId) {
        Optional<Projet> projetOpt = projetService.findById(projetId);
        if (projetOpt.isEmpty()) {
            logger.error("Projet not found with ID: " + projetId);
            return ResponseEntity.notFound().build();
        }

        Projet projet = projetOpt.get();
        List<Phase> phases = applicationService.getPhasesByProjet(projet);

        long totalStatuts = phases.size();
        long nombreNCExterne = phases.stream()
                .filter(phase -> phase.getStatusLivraisonExterne() == EtatLivraison.NC)
                .count();

        double tauxNCExterne = (totalStatuts > 0) ? ((double) nombreNCExterne / totalStatuts) * 100 : 0.0;

        return ResponseEntity.ok(tauxNCExterne);
    }

    // Endpoint pour calculer le taux de non-conformité interne
    @GetMapping("/projet/{projetId}/tauxNCInterne")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<Double> getTauxNCInterne(@PathVariable Long projetId) {
        Optional<Projet> projetOpt = projetService.findById(projetId);
        if (projetOpt.isEmpty()) {
            logger.error("Projet not found with ID: " + projetId);
            return ResponseEntity.notFound().build();
        }

        Projet projet = projetOpt.get();
        List<Phase> phases = applicationService.getPhasesByProjet(projet);

        long totalStatuts = phases.size();
        long nombreNCInterne = phases.stream()
                .filter(phase -> phase.getStatusLivraisonInterne() == EtatLivraison.NC)
                .count();

        double tauxNCInterne = (totalStatuts > 0) ? ((double) nombreNCInterne / totalStatuts) * 100 : 0.0;

        return ResponseEntity.ok(tauxNCInterne);
    }
    @GetMapping("/activite/{activiteId}/tauxNCSemestriels")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<List<TauxNCSemestrielResponse>> getTauxNCSemestriels(@PathVariable Long activiteId) {
        Activite activite = activiteService.findById(activiteId);
        if (activite == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<Projet> projets = projetService.findByActivite(activite);
        Map<String, TauxNCAggregator> tauxMap = new HashMap<>();

        for (Projet projet : projets) {
            List<Phase> phases = applicationService.getPhasesByProjet(projet);
            for (Phase phase : phases) {
                String semestreKey = getSemestreKey(phase.getPlannedStartDate());

                TauxNCAggregator tauxAggregator = tauxMap.getOrDefault(semestreKey, new TauxNCAggregator());
                tauxAggregator.setTotalStatuts(tauxAggregator.getTotalStatuts() + 1);

                if (phase.getStatusLivraisonInterne() == EtatLivraison.NC) {
                    tauxAggregator.setNombreNCInterne(tauxAggregator.getNombreNCInterne() + 1);
                }
                if (phase.getStatusLivraisonExterne() == EtatLivraison.NC) {
                    tauxAggregator.setNombreNCExterne(tauxAggregator.getNombreNCExterne() + 1);
                }

                tauxMap.put(semestreKey, tauxAggregator);
            }
        }

        List<TauxNCSemestrielResponse> responseList = new ArrayList<>();
        for (Map.Entry<String, TauxNCAggregator> entry : tauxMap.entrySet()) {
            String semestre = entry.getKey();
            TauxNCAggregator aggregator = entry.getValue();

            double tauxNCInterne = (aggregator.getTotalStatuts() > 0) ? ((double) aggregator.getNombreNCInterne() / aggregator.getTotalStatuts()) * 100 : 0.0;
            double tauxNCExterne = (aggregator.getTotalStatuts() > 0) ? ((double) aggregator.getNombreNCExterne() / aggregator.getTotalStatuts()) * 100 : 0.0;
            responseList.add(new TauxNCSemestrielResponse(semestre, tauxNCInterne, tauxNCExterne));
        }

        return ResponseEntity.ok(responseList);
    }

    private String getSemestreKey(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // Determine which semester the month belongs to
        String semestre = (month < 6) ? "S1" : "S2";

        return year + "-" + semestre;
    }
}