package com.example.gpq.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Projet {
    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id", nullable = false)
private Long idP;


    private String nomP;
    private String descriptionP;
    private Date datedebutP;
    private Date datefinP;
    private String methodologie;

    private String objectifs;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeProjet typeprojet;
}
