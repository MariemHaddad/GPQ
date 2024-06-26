package com.example.gpq.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

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
    private List<Projet> projet;

}

