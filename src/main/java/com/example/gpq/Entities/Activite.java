package com.example.gpq.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Activite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long idA;
    private String nomA;
    @OneToMany(mappedBy = "activite",cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Projet> projet;

}

