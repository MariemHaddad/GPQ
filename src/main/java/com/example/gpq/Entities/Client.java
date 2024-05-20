package com.example.gpq.Entities;

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
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long idC;
    private String nomC;
    @OneToMany(mappedBy = "client",cascade = CascadeType.REMOVE)
    private List<Projet> projet;
}
