package com.inmobiliaria.modelo;

import java.io.Serializable;

/**
 * Clase abstracta que representa la entidad base de cualquier inmueble.
 * Aplica herencia y encapsulamiento como principios de POO.
 * @author Equipo POO
 */
public abstract class Inmueble implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String direccion;
    private String numero; // Identificador interno del inmueble (ej. número de matrícula o código de oficina). No confundir con el número de piso en subclases.
    private String descripcion;
    private String codigoPostal;
    private double precioAlquiler;
    private boolean disponible;
    private String inquilinoId; // null si está disponible

    public Inmueble(String id, String direccion, String numero,
                    String descripcion, String codigoPostal, double precioAlquiler) {
        this.id = id;
        this.direccion = direccion;
        this.numero = numero;
        this.descripcion = descripcion;
        this.codigoPostal = codigoPostal;
        this.precioAlquiler = precioAlquiler;
        this.disponible = true;
        this.inquilinoId = null;
    }

    /**
     * Retorna el tipo de inmueble (EDIFICIO, PISO o LOCAL).
     * @return identificador del tipo de inmueble
     */
    public abstract String getTipoInmueble();

    // Getters y Setters (encapsulamiento)

    /** @return identificador único del inmueble */
    public String getId() { return id; }
    /** @param id nuevo identificador */
    public void setId(String id) { this.id = id; }

    /** @return dirección del inmueble */
    public String getDireccion() { return direccion; }
    /** @param direccion nueva dirección */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /** @return código interno del inmueble */
    public String getNumero() { return numero; }
    /** @param numero nuevo código interno */
    public void setNumero(String numero) { this.numero = numero; }

    /** @return descripción del inmueble */
    public String getDescripcion() { return descripcion; }
    /** @param descripcion nueva descripción */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /** @return código postal del inmueble */
    public String getCodigoPostal() { return codigoPostal; }
    /** @param codigoPostal nuevo código postal */
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    /** @return precio de alquiler mensual */
    public double getPrecioAlquiler() { return precioAlquiler; }
    /** @param precioAlquiler nuevo precio de alquiler */
    public void setPrecioAlquiler(double precioAlquiler) { this.precioAlquiler = precioAlquiler; }

    /** @return true si el inmueble está disponible para alquilar */
    public boolean isDisponible() { return disponible; }
    /** @param disponible nuevo estado de disponibilidad */
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    /** @return ID del inquilino actual (null si está disponible) */
    public String getInquilinoId() { return inquilinoId; }
    /** @param inquilinoId ID del inquilino a asignar */
    public void setInquilinoId(String inquilinoId) { this.inquilinoId = inquilinoId; }

    @Override
    public String toString() {
        return String.format("[%s] %s (Cód: %s) | %s | %s | $%.2f | %s",
                getTipoInmueble(), direccion, numero, descripcion,
                codigoPostal, precioAlquiler,
                disponible ? "DISPONIBLE" : "OCUPADO");
    }
}
