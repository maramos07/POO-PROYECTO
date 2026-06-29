package com.inmobiliaria.modelo;

import java.io.Serializable;

/**
 * Clase abstracta que representa la entidad base de cualquier inmueble.
 * Aplica herencia y encapsulamiento como principios de POO.
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

    // Método abstracto que cada subclase implementa para indicar su tipo
    public abstract String getTipoInmueble();

    // Getters y Setters (encapsulamiento)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }

    public double getPrecioAlquiler() { return precioAlquiler; }
    public void setPrecioAlquiler(double precioAlquiler) { this.precioAlquiler = precioAlquiler; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    public String getInquilinoId() { return inquilinoId; }
    public void setInquilinoId(String inquilinoId) { this.inquilinoId = inquilinoId; }

    @Override
    public String toString() {
        return String.format("[%s] %s (Cód: %s) | %s | %s | $%.2f | %s",
                getTipoInmueble(), direccion, numero, descripcion,
                codigoPostal, precioAlquiler,
                disponible ? "DISPONIBLE" : "OCUPADO");
    }
}
