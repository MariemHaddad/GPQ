package com.example.gpq.Entities;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "phase")
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "idCh")
public class Checklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long idCh;

    @Enumerated(EnumType.STRING)
    private StatusChecklist status = StatusChecklist.EN_ATTENTE;

    private String remarque;

    @OneToOne
    @JoinColumn(name = "phase_id")
    @JsonBackReference
    private Phase phase;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ChecklistItem> items;
}