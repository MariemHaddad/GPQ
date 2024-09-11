package com.example.gpq.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PlanAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long idPa;

    private String LeçonTirées;

    @OneToOne
    @JoinColumn(name = "analyse_causale_id", referencedColumnName = "id_an")
    @JsonBackReference("analyse-planActions")
    private AnalyseCausale analyseCausale;
    @OneToMany(mappedBy = "planAction", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Action> actions = new ArrayList<>();

}