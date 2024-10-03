package com.example.gpq.DTO;

public class TauxNCSemestrielResponse {
    private String semestre;
    private double tauxNCInterne;
    private double tauxNCExterne;

    // No-argument constructor
    public TauxNCSemestrielResponse() {
    }

    // Constructor with parameters
    public TauxNCSemestrielResponse(String semestre, double tauxNCInterne, double tauxNCExterne) {
        this.semestre = semestre;
        this.tauxNCInterne = tauxNCInterne;
        this.tauxNCExterne = tauxNCExterne;
    }

    // Getters
    public String getSemestre() {
        return semestre;
    }

    public double getTauxNCInterne() {
        return tauxNCInterne;
    }

    public double getTauxNCExterne() {
        return tauxNCExterne;
    }

    // Optional: Setters if you need to modify the fields after object creation
    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public void setTauxNCInterne(double tauxNCInterne) {
        this.tauxNCInterne = tauxNCInterne;
    }

    public void setTauxNCExterne(double tauxNCExterne) {
        this.tauxNCExterne = tauxNCExterne;
    }
}