package com.inmobiliaria.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa el contrato de alquiler entre un inquilino y un inmueble.
 */
public class Alquiler implements Serializable {

    private String id;
    private String inquilinoId;
    private String inmuebleId;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;     // null si está activo
    private boolean activo;

    public Alquiler(String id, String inquilinoId, String inmuebleId, LocalDate fechaInicio) {
        this.id = id;
        this.inquilinoId = inquilinoId;
        this.inmuebleId = inmuebleId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = null;
        this.activo = true;
    }

    public void finalizar(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
        this.activo = false;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getInquilinoId() { return inquilinoId; }
    public void setInquilinoId(String inquilinoId) { this.inquilinoId = inquilinoId; }

    public String getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(String inmuebleId) { this.inmuebleId = inmuebleId; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return String.format("[%s] Inquilino: %s | Inmueble: %s | Inicio: %s | Fin: %s | %s",
                id, inquilinoId, inmuebleId, fechaInicio,
                fechaFin != null ? fechaFin.toString() : "En curso",
                activo ? "ACTIVO" : "FINALIZADO");
    }
}
