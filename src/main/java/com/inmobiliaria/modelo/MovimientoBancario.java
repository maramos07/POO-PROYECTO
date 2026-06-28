package com.inmobiliaria.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa un movimiento bancario asociado a un inmueble.
 * Puede ser ingreso (alquiler) o gasto (servicio, reforma, reparación).
 */
public class MovimientoBancario implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoMovimiento {
        INGRESO_ALQUILER("Ingreso por Alquiler"),
        GASTO_SERVICIO("Gasto por Servicio"),
        GASTO_REFORMA("Gasto por Reforma"),
        GASTO_REPARACION("Gasto por Reparación"),
        OTRO_INGRESO("Otro Ingreso"),
        OTRO_GASTO("Otro Gasto");

        private final String descripcion;
        TipoMovimiento(String descripcion) { this.descripcion = descripcion; }
        public String getDescripcion() { return descripcion; }

        @Override
        public String toString() { return descripcion; }
    }

    private String id;
    private TipoMovimiento tipoMovimiento;
    private String inmuebleId;
    private LocalDate fecha;
    private double importe;
    private String personaEntidad;   // Acreedor o deudor

    public MovimientoBancario(String id, TipoMovimiento tipoMovimiento,
                              String inmuebleId, LocalDate fecha,
                              double importe, String personaEntidad) {
        this.id = id;
        this.tipoMovimiento = tipoMovimiento;
        this.inmuebleId = inmuebleId;
        this.fecha = fecha;
        this.importe = importe;
        this.personaEntidad = personaEntidad;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public TipoMovimiento getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(TipoMovimiento tipo) { this.tipoMovimiento = tipo; }

    public String getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(String inmuebleId) { this.inmuebleId = inmuebleId; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }

    public String getPersonaEntidad() { return personaEntidad; }
    public void setPersonaEntidad(String personaEntidad) { this.personaEntidad = personaEntidad; }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | Inmueble: %s | $%.2f | %s",
                id, fecha, tipoMovimiento.getDescripcion(),
                inmuebleId, importe, personaEntidad);
    }
}
