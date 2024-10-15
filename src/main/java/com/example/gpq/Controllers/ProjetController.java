package com.example.gpq.Controllers;

import com.example.gpq.DTO.*;
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
    @GetMapping("/satisfaction/{activityId}")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<List<SatisfactionDataDTO>> getSatisfactionData(@PathVariable Long activityId) {
        List<SatisfactionDataDTO> satisfactionData = projetService.getSatisfactionDataForActivity(activityId);
        return new ResponseEntity<>(satisfactionData, HttpStatus.OK);
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
    @GetMapping("/all/projets")
    public ResponseEntity<List<Projet>> getAllProjets() {
        List<Projet> projets = projetService.findAll();
        return ResponseEntity.ok(projets);
    }
    @GetMapping("/all")
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientService.findAll();
        return ResponseEntity.ok(clients);
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


    @PutMapping("/modifier/{projetId}")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('DIRECTEUR')")
    public ResponseEntity<String> modifierProjet(@PathVariable Long projetId, @RequestBody Projet projetDetails) {
        Optional<Projet> projetOpt = projetService.findById(projetId);
        if (projetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projet non trouvé.");
        }
        Projet projet = projetOpt.get();

        // Mettre à jour les détails du projet
        projet.setNomP(projetDetails.getNomP());
        projet.setDescriptionP(projetDetails.getDescriptionP());
        projet.setDatedebutP(projetDetails.getDatedebutP());
        projet.setDatefinP(projetDetails.getDatefinP());
        projet.setMethodologie(projetDetails.getMethodologie());
        projet.setObjectifs(projetDetails.getObjectifs());
        projet.setSatisfactionClient(projetDetails.getSatisfactionClient());
        projet.setValeurSatisfaction(projetDetails.getValeurSatisfaction());
        projet.setTypeprojet(projetDetails.getTypeprojet());
        projet.setDefautInternes(projetDetails.getDefautInternes());
        projet.setDefautTotaux(projetDetails.getDefautTotaux());
        projet.setNbr8DRealises(projetDetails.getNbr8DRealises());
        projet.setNbrRetoursCritiques(projetDetails.getNbrRetoursCritiques());
        projet.setNombreRuns(projetDetails.getNombreRuns());
        projetService.save(projet); // Sauvegarder les modifications
        return ResponseEntity.ok("Projet modifié avec succès.");
    }
    @GetMapping("/{projetId}/dde")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<Double> getDDEPourProjet(@PathVariable Long projetId) {
        double dde = projetService.calculerDDEPourProjet(projetId);
        return ResponseEntity.ok(dde);
    }

    // Endpoint pour obtenir le DDE de tous les projets d'une activité
    @GetMapping("/activite/{activiteId}/dde")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<List<DDEDataDTO>> getDDEPourActivite(@PathVariable Long activiteId) {
        List<DDEDataDTO> ddeData = projetService.calculerDDEPourActivite(activiteId);
        return ResponseEntity.ok(ddeData);
    }
    @GetMapping("/runs-semestriels/{activiteId}")
    public ResponseEntity<List<RunSemestrielDTO>> getRunsSemestriels(@PathVariable Long activiteId) {
        List<RunSemestrielDTO> runsSemestriels = projetService.getRunsSemestriels(activiteId);
        return ResponseEntity.ok(runsSemestriels);
    }
    @GetMapping("/activites/{activiteId}/taux-liberation-semestriel")
    public ResponseEntity<Map<String, Double>> getTauxLiberationSemestriel(@PathVariable Long activiteId) {
        Map<String, Double> tauxLiberationSemestriel = projetService.getTauxLiberationSemestriel(activiteId);
        return ResponseEntity.ok(tauxLiberationSemestriel);
    }
    @GetMapping("/tauxRealisation8D/{activiteId}")
    public Map<String, List<Double>> getTauxRealisation8DParSemestre(@PathVariable Long activiteId) {
        return projetService.getTauxRealisation8DParSemestre(activiteId);
    }
    @GetMapping("/{id}/tauxC")
    public double getTauxCByProjet(@PathVariable Long id) {
        return projetService.getTauxCByProjet(id);
    }

    @GetMapping("/activite/{activiteId}/tauxC")
    public Map<String, List<Double>> getTauxCBySemestre(@PathVariable Long activiteId) {
        return projetService.getTauxCBySemestre(activiteId);
    }

    @GetMapping("/activite/{activiteId}/ddeSemestriels")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('RQUALITE')")
    public ResponseEntity<Map<String, Double>> getDDESemestriels(@PathVariable Long activiteId) {
        Activite activite = activiteService.findById(activiteId);
        if (activite == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        List<Projet> projets = projetService.findByActivite(activite);
        Map<String, List<Double>> ddeBySemester = new HashMap<>();

        // Grouper les projets par semestre et calculer la moyenne DDE
        for (Projet projet : projets) {
            String semester = projet.getSemester();
            double dde = projet.getDDE();

            ddeBySemester.computeIfAbsent(semester, k -> new ArrayList<>()).add(dde);
        }

        Map<String, Double> ddeAverageBySemester = new HashMap<>();
        for (Map.Entry<String, List<Double>> entry : ddeBySemester.entrySet()) {
            List<Double> ddeList = entry.getValue();
            double averageDDE = ddeList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            ddeAverageBySemester.put(entry.getKey(), averageDDE);
        }

        return ResponseEntity.ok(ddeAverageBySemester);
    }
    @DeleteMapping("/supprimer/{projetId}")
    @PreAuthorize("hasRole('CHEFDEPROJET') or hasRole('DIRECTEUR')") // Assurez-vous que seuls certains rôles peuvent supprimer
    public ResponseEntity<String> supprimerProjet(@PathVariable Long projetId) {
        Optional<Projet> projetOpt = projetService.findById(projetId);
        if (projetOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projet non trouvé.");
        }

        Projet projet = projetOpt.get();

        // Suppression automatique des entités associées grâce à la cascade définie dans la classe Projet
        projetService.delete(projet); // Utilisez une méthode dans le service pour gérer la suppression
        return ResponseEntity.ok("Projet et toutes ses entités associées supprimés avec succès.");
    }
}