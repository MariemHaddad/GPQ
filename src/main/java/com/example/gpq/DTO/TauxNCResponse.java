package com.example.gpq.DTO;

public class TauxNCResponse {
    private Long projetId;
    private String projetNom;
    private double tauxNCInterne;
    private double tauxNCExterne;

    // Constructeur, getters et setters
    public TauxNCResponse(Long projetId, String projetNom, double tauxNCInterne, double tauxNCExterne) {
        this.projetId = projetId;
        this.projetNom = projetNom;
        this.tauxNCInterne = tauxNCInterne;
        this.tauxNCExterne = tauxNCExterne;
    }

    // Getters et Setters
    public Long getProjetId() {
        return projetId;
    }

    public void setProjetId(Long projetId) {
        this.projetId = projetId;
    }

    public String getProjetNom() {
        return projetNom;
    }

    public void setProjetNom(String projetNom) {
        this.projetNom = projetNom;
    }

    public double getTauxNCInterne() {
        return tauxNCInterne;
    }

    public void setTauxNCInterne(double tauxNCInterne) {
        this.tauxNCInterne = tauxNCInterne;
    }

    public double getTauxNCExterne() {
        return tauxNCExterne;
    }

    public void setTauxNCExterne(double tauxNCExterne) {
        this.tauxNCExterne = tauxNCExterne;
    }
}

