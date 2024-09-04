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
public class Pourquoi {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String question; // La question du Pourquoi

    private Double pourcentage; // Le pourcentage associé

    private String action; // L'action à entreprendre

    @ManyToOne
    @JoinColumn(name = "analyseCausale_id")
    @JsonBackReference("analyse-cinqPourquoi")
    private AnalyseCausale analyseCausale;
}