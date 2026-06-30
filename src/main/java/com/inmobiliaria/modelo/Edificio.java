package com.inmobiliaria.modelo;

import java.io.Serializable;

/**
 * Representa un edificio. Hereda de Inmueble.
 * Un edificio puede contener pisos y locales, pero estos
 * se registran de forma independiente en el sistema.
 * @author Equipo POO
 */
public class Edificio extends Inmueble implements Serializable {

    private int numeroPisos;
    private String nombreEdificio;

    /**
     * @param id identificador único
     * @param direccion dirección del edificio
     * @param descripcion descripción general
     * @param codigoPostal código postal
     * @param precioAlquiler precio de alquiler mensual
     * @param numeroPisos cantidad total de pisos
     * @param nombreEdificio nombre del edificio
     */
    public Edificio(String id, String direccion,
                    String descripcion, String codigoPostal,
                    double precioAlquiler, int numeroPisos, String nombreEdificio) {
        super(id, direccion, descripcion, codigoPostal, precioAlquiler);
        this.numeroPisos = numeroPisos;
        this.nombreEdificio = nombreEdificio;
    }

    @Override
    public String getTipoInmueble() {
        return "EDIFICIO";
    }

    /** @return cantidad total de pisos */
    public int getNumeroPisos() { return numeroPisos; }
    /** @param numeroPisos nueva cantidad de pisos */
    public void setNumeroPisos(int numeroPisos) { this.numeroPisos = numeroPisos; }

    /** @return nombre del edificio */
    public String getNombreEdificio() { return nombreEdificio; }
    /** @param nombreEdificio nuevo nombre del edificio */
    public void setNombreEdificio(String nombreEdificio) { this.nombreEdificio = nombreEdificio; }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Nombre: %s | Pisos: %d",
                nombreEdificio, numeroPisos);
    }
}
