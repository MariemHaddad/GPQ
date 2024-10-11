package com.example.gpq.Entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idAN")
public class AnalyseCausale {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_an", nullable = false)
    private Long idAN;

    @Enumerated(EnumType.STRING)
    private TypeProbleme typeProbleme;

    private String identificationProbleme;

    @Enumerated(EnumType.STRING)
    private MethodeAnalyse methodeAnalyse;

    @OneToOne
    @JoinColumn(name = "checklist_id")
    @JsonManagedReference("checklist-analyse")
    private Checklist checklist;
    @OneToMany(mappedBy = "analyseCausale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("analyse-cinqPourquoi")

    private List<Pourquoi> cinqPourquoi = new ArrayList<>();

    @OneToMany(mappedBy = "analyseCausale", cascade = CascadeType.ALL)
    @JsonManagedReference("analyse-causesIshikawa")

    private List<CauseIshikawa> causesIshikawa = new ArrayList<>();
    @OneToOne(mappedBy = "analyseCausale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("analyse-planActions")
    private PlanAction planAction;

}