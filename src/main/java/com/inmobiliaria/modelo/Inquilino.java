package com.inmobiliaria.modelo;

import java.io.Serializable;

/**
 * Representa un inquilino que puede alquilar inmuebles.
 * Encapsula sus datos personales y tipo de respaldo económico.
 * @author Equipo POO
 */
public class Inquilino implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Sexos válidos para un inquilino. */
    public enum Sexo {
        MASCULINO("Masculino"),
        FEMENINO("Femenino"),
        OTRO("Otro");

        private final String descripcion;
        Sexo(String descripcion) { this.descripcion = descripcion; }
        public String getDescripcion() { return descripcion; }

        @Override
        public String toString() { return descripcion; }
    }

    /** Tipos de respaldo económico válidos. */
    public enum TipoRespaldo {
        NOMINA("Nómina"),
        AVAL_BANCARIO("Aval Bancario"),
        CONTRATO_TRABAJO("Contrato de Trabajo"),
        AVAL_PERSONA("Aval por Persona");

        private final String descripcion;
        TipoRespaldo(String descripcion) { this.descripcion = descripcion; }
        public String getDescripcion() { return descripcion; }

        @Override
        public String toString() { return descripcion; }
    }

    private String id;
    private String nombre;
    private String cedula;
    private int edad;
    private String sexo;
    private String medioContacto;
    private TipoRespaldo tipoRespaldo;

    /**
     * @param id identificador único del inquilino
     * @param nombre nombre completo
     * @param cedula número de cédula
     * @param edad edad en años
     * @param sexo sexo (debe coincidir con Sexo.values())
     * @param medioContacto teléfono o email
     * @param tipoRespaldo tipo de respaldo económico
     */
    public Inquilino(String id, String nombre, String cedula, int edad,
                     String sexo,
                     String medioContacto, TipoRespaldo tipoRespaldo) {
        this.id = id;
        this.nombre = nombre;
        this.cedula = cedula;
        this.edad = edad;
        this.sexo = sexo;
        this.medioContacto = medioContacto;
        this.tipoRespaldo = tipoRespaldo;
    }

    // Getters y Setters

    /** @return identificador único */
    public String getId() { return id; }
    /** @param id nuevo identificador */
    public void setId(String id) { this.id = id; }

    /** @return nombre completo */
    public String getNombre() { return nombre; }
    /** @param nombre nuevo nombre */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return número de cédula */
    public String getCedula() { return cedula; }
    /** @param cedula nueva cédula */
    public void setCedula(String cedula) { this.cedula = cedula; }

    /** @return edad en años */
    public int getEdad() { return edad; }
    /** @param edad nueva edad */
    public void setEdad(int edad) { this.edad = edad; }

    /** @return sexo del inquilino */
    public String getSexo() { return sexo; }
    /**
     * @param sexo sexo del inquilino (debe coincidir con Sexo.values())
     * @throws IllegalArgumentException si el sexo no es válido
     */
    public void setSexo(String sexo) {
        boolean valido = false;
        for (Sexo s : Sexo.values()) {
            if (s.getDescripcion().equals(sexo) || s.name().equals(sexo)) {
                valido = true;
                break;
            }
        }
        if (!valido) throw new IllegalArgumentException("Sexo no válido.");
        this.sexo = sexo;
    }

    /** @return teléfono o email de contacto */
    public String getMedioContacto() { return medioContacto; }
    /** @param medioContacto nuevo contacto */
    public void setMedioContacto(String medioContacto) { this.medioContacto = medioContacto; }

    /** @return tipo de respaldo económico */
    public TipoRespaldo getTipoRespaldo() { return tipoRespaldo; }
    /** @param tipoRespaldo nuevo tipo de respaldo */
    public void setTipoRespaldo(TipoRespaldo tipoRespaldo) { this.tipoRespaldo = tipoRespaldo; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Cédula: %s | Edad: %d | %s | Contacto: %s | Respaldo: %s",
                id, nombre, cedula, edad, sexo, medioContacto,
                tipoRespaldo.getDescripcion());
    }
}
