package com.inmobiliaria.modelo;

import java.io.Serializable;

/**
 * Representa un edificio. Hereda de Inmueble.
 * Un edificio puede contener pisos y locales, pero estos
 * se registran de forma independiente en el sistema.
 */
public class Edificio extends Inmueble implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numeroPisos;
    private String nombreEdificio;

    public Edificio(String id, String direccion, String numero,
                    String descripcion, String codigoPostal,
                    double precioAlquiler, int numeroPisos, String nombreEdificio) {
        super(id, direccion, numero, descripcion, codigoPostal, precioAlquiler);
        this.numeroPisos = numeroPisos;
        this.nombreEdificio = nombreEdificio;
    }

    @Override
    public String getTipoInmueble() {
        return "EDIFICIO";
    }

    public int getNumeroPisos() { return numeroPisos; }
    public void setNumeroPisos(int numeroPisos) { this.numeroPisos = numeroPisos; }

    public String getNombreEdificio() { return nombreEdificio; }
    public void setNombreEdificio(String nombreEdificio) { this.nombreEdificio = nombreEdificio; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Nombre: %s | Pisos: %d",
                nombreEdificio, numeroPisos);
    }
}
