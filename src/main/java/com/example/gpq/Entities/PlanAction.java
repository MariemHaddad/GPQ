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

    @Column(name = "lecon_tirees") // S'assurer que le nom correspond à la colonne de la base de données
    private String leconTirees;

    @OneToOne
    @JoinColumn(name = "analyse_causale_id", referencedColumnName = "id_an")
    @JsonBackReference("analyse-planActions")
    private AnalyseCausale analyseCausale;
    @OneToMany(mappedBy = "planAction", cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Action> actions = new ArrayList<>();
    @Override
    public String toString() {
        return "PlanAction{" +
                "idPa=" + idPa +
                ", leconTirees='" + leconTirees + '\'' +
                ", actionsCount=" + (actions != null ? actions.size() : 0) +
                '}';
    }
}