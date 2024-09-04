package com.example.gpq.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CauseIshikawa {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String description; // Description de la cause

    @Enumerated(EnumType.STRING)
    private CategorieIshikawa categorie; // Catégorie de la cause

    private Double pourcentage; // Le pourcentage associé

    private String action; // L'action à entreprendre

    @ManyToOne
    @JoinColumn(name = "analyseCausale_id")
    @JsonBackReference("analyse-causesIshikawa")
    private AnalyseCausale analyseCausale;
    @Override
    public String toString() {
        return "CauseIshikawa{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", categorie=" + categorie +
                '}';
    }
}