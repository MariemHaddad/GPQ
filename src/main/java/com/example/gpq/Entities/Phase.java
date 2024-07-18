package com.example.gpq.Entities;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Projet projet;

    @OneToOne(mappedBy = "phase", cascade = CascadeType.ALL)
    private Checklist checklist;
}