package com.example.gpq.DTO;
//Cette classe permettra de cumuler les informations pour chaque semestre.
public class TauxNCAggregator {
    private long totalStatuts = 0;
    private long nombreNCInterne = 0;
    private long nombreNCExterne = 0;

    public long getTotalStatuts() {
        return totalStatuts;
    }

    public void setTotalStatuts(long totalStatuts) {
        this.totalStatuts = totalStatuts;
    }

    public long getNombreNCInterne() {
        return nombreNCInterne;
    }

    public void setNombreNCInterne(long nombreNCInterne) {
        this.nombreNCInterne = nombreNCInterne;
    }

    public long getNombreNCExterne() {
        return nombreNCExterne;
    }

    public void setNombreNCExterne(long nombreNCExterne) {
        this.nombreNCExterne = nombreNCExterne;
    }
}
