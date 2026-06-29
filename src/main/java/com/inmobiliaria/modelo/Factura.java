package com.inmobiliaria.modelo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Representa una factura de gasto asociada a un inmueble.
 * Incluye servicios públicos, reformas y reparaciones.
 * @author Equipo POO
 */
public class Factura implements Serializable {

    /** Conceptos posibles de una factura de gasto. */
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

    /**
     * @param id identificador único de la factura
     * @param fechaEmision fecha de emisión
     * @param inmuebleId ID del inmueble asociado
     * @param concepto tipo de gasto
     * @param proveedor nombre del proveedor o compañía
     * @param costo monto del gasto
     */
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

    /** @return identificador único */
    public String getId() { return id; }
    /** @param id nuevo identificador */
    public void setId(String id) { this.id = id; }

    /** @return fecha de emisión */
    public LocalDate getFechaEmision() { return fechaEmision; }
    /** @param fechaEmision nueva fecha de emisión */
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    /** @return ID del inmueble asociado */
    public String getInmuebleId() { return inmuebleId; }
    /** @param inmuebleId nuevo ID de inmueble */
    public void setInmuebleId(String inmuebleId) { this.inmuebleId = inmuebleId; }

    /** @return concepto del gasto */
    public ConceptoFactura getConcepto() { return concepto; }
    /** @param concepto nuevo concepto */
    public void setConcepto(ConceptoFactura concepto) { this.concepto = concepto; }

    /** @return nombre del proveedor */
    public String getProveedor() { return proveedor; }
    /** @param proveedor nuevo proveedor */
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }

    /** @return monto del gasto */
    public double getCosto() { return costo; }
    /** @param costo nuevo monto */
    public void setCosto(double costo) { this.costo = costo; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Inmueble: %s | %s | Proveedor: %s | $%.2f",
                id, fechaEmision, inmuebleId,
                concepto.getDescripcion(), proveedor, costo);
    }
}
