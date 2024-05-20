package com.example.gpq.Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Date;
import java.util.List;

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
    @ManyToMany(mappedBy = "projets")
    private List<User> users;
    @ManyToOne
    private User chefDeProjet;

    @ManyToOne
    private User responsableQualite;

    public void setChefDeProjet(User chefDeProjet) {
        this.chefDeProjet = chefDeProjet;
    }

    public void setResponsableQualite(User responsableQualite) {
        this.responsableQualite = responsableQualite;
    }
    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Activite activite;
    @ManyToOne
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Client client;
}
