package com.example.gpq.Entities;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;


import java.util.Date;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"phases", "activite", "client", "users"})
@Builder

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property ="idP")
public class Projet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long idP;

    private String nomP;
    private String descriptionP;
    private Date datedebutP;
    private Date datefinP;
    private String methodologie;
    private String objectifs;
    @Enumerated(EnumType.STRING)
    private TypeSatisfaction satisfactionClient;

    private Long nombreRuns;
    private Long defautInternes; // DI
    private Long defautTotaux; // DT

    // Méthode pour calculer l'efficacité de détection de défauts (DDE)
    public double getDDE() {
        if (defautTotaux == null || defautTotaux == 0) {
            return 0.0; // Éviter la division par zéro
        }
        return (double) defautInternes / defautTotaux * 100; // Retourne le DDE en pourcentage
    }
    private Double valeurSatisfaction;
    private Long nbr8DRealises;
    private Long nbrRetoursCritiques;

    // Méthode pour calculer le taux de réalisation des 8D
    public double getTauxRealisation8D() {
        if (nbrRetoursCritiques == null || nbrRetoursCritiques == 0) {
            return 0.0; // Éviter la division par zéro
        }
        return (double) nbr8DRealises / nbrRetoursCritiques * 100; // Retourne le taux en pourcentage
    }
    @Enumerated(EnumType.STRING)
    private TypeProjet typeprojet;

    private String responsableQualiteNom;
    private String chefDeProjetNom;

    @ManyToMany(mappedBy = "projets")
    private List<User> users;

    @ManyToOne
    private User chefDeProjet;

    @ManyToOne
    private User responsableQualite;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Activite activite;

    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Client client;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Phase> phases;

    // Custom setters for names
    public void setChefDeProjet(User chefDeProjet) {
        this.chefDeProjet = chefDeProjet;
        if (chefDeProjet != null) {
            this.chefDeProjetNom = chefDeProjet.getNom();
        }
    }

    public void setResponsableQualite(User responsableQualite) {
        this.responsableQualite = responsableQualite;
        if (responsableQualite != null) {
            this.responsableQualiteNom = responsableQualite.getNom();
        }
    }
    public double getSi1() {
        if (this.valeurSatisfaction == null) {
            // Ajouter un log pour comprendre pourquoi valeurSatisfaction est null
            System.out.println("valeurSatisfaction is null for Projet ID: " + this.idP);
            return 0.0; // ou une valeur par défaut que vous jugez appropriée
        }
        return this.valeurSatisfaction;
    }

    public double getSi2() {
        // Ajouter une logique pour SI2 si nécessaire, ou une nouvelle variable pour SI2
        return 0.0; // À remplacer par l'attribut approprié
    }
    public String getSemester() {
        int year = this.datedebutP.getYear() + 1900; // Conversion de l'année
        int month = this.datedebutP.getMonth() + 1; // Les mois commencent à 0
        String semester = (month <= 6) ? "S1-" + year : "S2-" + year;
        return semester;


    }public double getTauxC() {
        long countC = phases.stream()
                .filter(phase -> phase.getStatusLivraisonExterne() == EtatLivraison.C)
                .count();
        long totalLivraisonExterne = phases.stream()
                .filter(phase -> phase.getStatusLivraisonExterne() != null)
                .count();

        return totalLivraisonExterne == 0 ? 0.0 : (double) countC / totalLivraisonExterne * 100; // Taux en pourcentage
    }
    public double getTauxLiberation() {
        long totalLivraison = phases.stream()
                .filter(phase -> "Livraison".equalsIgnoreCase(phase.getDescription()))
                .count();

        long livraisonAcceptee = phases.stream()
                .filter(phase -> "Livraison".equalsIgnoreCase(phase.getDescription()) &&
                        phase.getChecklist() != null &&
                        phase.getChecklist().getStatus() == StatusChecklist.ACCEPTE)
                .peek(phase -> {
                    System.out.println("Phase: " + phase.getDescription() + ", Checklist Status: " + phase.getChecklist().getStatus());
                })
                .count();

        System.out.println("Total Livraison: " + totalLivraison);
        System.out.println("Livraison Acceptée: " + livraisonAcceptee);

        return totalLivraison == 0 ? 0.0 : (double) livraisonAcceptee / totalLivraison *2 * 100; // Retourner le taux en pourcentage
    }}