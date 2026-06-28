package com.inmobiliaria.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa una factura de gasto asociada a un inmueble.
 * Incluye servicios públicos, reformas y reparaciones.
 */
public class Factura implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum ConceptoFactura {
        TELEFONO("Telefono"),
        AGUA("Agua"),
        GAS("Gas"),
        ELECTRICIDAD("Electricidad"),
        REFORMA("Reforma"),
        REPARACION("Reparación"),
        OTRO("Otro");

        private final String descripcion;
        ConceptoFactura(String descripcion) { this.descripcion = descripcion; }
        public String getDescripcion() { return descripcion; }

        @Override
        public String toString() { return descripcion; }
    }

    private String id;
    private LocalDate fechaEmision;
    private String inmuebleId;
    private ConceptoFactura concepto;
    private String proveedor;
    private double costo;

    public Factura(String id, LocalDate fechaEmision, String inmuebleId,
                   ConceptoFactura concepto, String proveedor, double costo) {
        this.id = id;
        this.fechaEmision = fechaEmision;
        this.inmuebleId = inmuebleId;
        this.concepto = concepto;
        this.proveedor = proveedor;
        this.costo = costo;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getInmuebleId() { return inmuebleId; }
    public void setInmuebleId(String inmuebleId) { this.inmuebleId = inmuebleId; }

    public ConceptoFactura getConcepto() { return concepto; }
    public void setConcepto(ConceptoFactura concepto) { this.concepto = concepto; }

    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Inmueble: %s | %s | Proveedor: %s | $%.2f",
                id, fechaEmision, inmuebleId,
                concepto.getDescripcion(), proveedor, costo);
    }
}
