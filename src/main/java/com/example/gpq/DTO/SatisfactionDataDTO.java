package com.example.gpq.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SatisfactionDataDTO {
    private String semester;
    private double si1Value;
    private double si2Value;


    // Vous pouvez ajouter d'autres attributs si nécessaire
}