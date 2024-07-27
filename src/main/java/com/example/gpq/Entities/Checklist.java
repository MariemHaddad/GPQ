package com.example.gpq.Entities;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Checklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long idCh;

    @OneToOne
    @JoinColumn(name = "phase_id")
    private Phase phase;

    @Enumerated(EnumType.STRING)
    private StatusChecklist status = StatusChecklist.EN_ATTENTE;

    private String remarque;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItem> items = new ArrayList<>();

    // Ajoutez un constructeur si nécessaire
}