package com.example.gpq.Entities;


import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    /* @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    */
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

}


