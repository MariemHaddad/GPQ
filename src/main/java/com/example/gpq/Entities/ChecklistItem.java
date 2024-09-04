package com.example.gpq.Entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChecklistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long idCi;

    private String description;

    @ManyToOne
    @JoinColumn(name = "checklist_id")
    @JsonBackReference("checklist-items")
    private Checklist checklist;

    private String resultat;
    private String commentaire;

    // Custom constructor
    public ChecklistItem(String description, Checklist checklist) {
        this.description = description;
        this.checklist = checklist;
    }
}
