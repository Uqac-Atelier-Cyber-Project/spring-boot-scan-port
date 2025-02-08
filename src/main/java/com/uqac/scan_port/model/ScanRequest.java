package com.uqac.scan_port.model;

// Modèle représentant la requête POST pour le scan
public class ScanRequest {
    private String targetIp;
    private int startPort = 1; // Valeur par défaut
    private int endPort = 1024; // Valeur par défaut

    // Getters et Setters
    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public int getStartPort() {
        return startPort;
    }

    public void setStartPort(int startPort) {
        this.startPort = startPort;
    }

    public int getEndPort() {
        return endPort;
    }

    public void setEndPort(int endPort) {
        this.endPort = endPort;
    }
}