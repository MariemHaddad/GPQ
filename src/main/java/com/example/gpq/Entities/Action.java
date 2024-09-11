package com.example.gpq.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String description;
    private String type;
    private String responsable;
    private Date datePlanification;
    private Date dateRealisation;
    private String critereEfficacite;
    private Boolean efficace;
    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "plan_action_id")
    @JsonBackReference
    private PlanAction planAction;
}
