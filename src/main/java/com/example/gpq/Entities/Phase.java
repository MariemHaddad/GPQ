package com.example.gpq.Entities;


import com.example.gpq.Configuration.PhaseDeserializer;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"projet", "checklist"})
@Builder
@JsonDeserialize(using = PhaseDeserializer.class)
public class Phase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long idPh;

    private String description;
    private String objectifs;
    private Date plannedStartDate;
    private Date plannedEndDate;
    private Date effectiveStartDate;
    private Date effectiveEndDate;

    @Enumerated(EnumType.STRING)
    private EtatPhase etat = EtatPhase.EN_ATTENTE;

    @ManyToOne
    @JoinColumn(name = "projet_id")
    @JsonBackReference
    private Projet projet;

    @OneToOne(mappedBy = "phase", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Checklist checklist;
}