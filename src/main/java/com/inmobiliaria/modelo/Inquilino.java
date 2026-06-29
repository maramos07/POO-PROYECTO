package com.inmobiliaria.modelo;

import java.io.Serializable;

/**
 * Representa un inquilino que puede alquilar inmuebles.
 * Encapsula sus datos personales y tipo de respaldo económico.
 */
public class Inquilino implements Serializable {
    private static final long serialVersionUID = 1L;

    // Sexos válidos
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

    // Tipos de respaldo válidos
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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getSexo() { return sexo; }
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

    public String getMedioContacto() { return medioContacto; }
    public void setMedioContacto(String medioContacto) { this.medioContacto = medioContacto; }

    public TipoRespaldo getTipoRespaldo() { return tipoRespaldo; }
    public void setTipoRespaldo(TipoRespaldo tipoRespaldo) { this.tipoRespaldo = tipoRespaldo; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Cédula: %s | Edad: %d | %s | Contacto: %s | Respaldo: %s",
                id, nombre, cedula, edad, sexo, medioContacto,
                tipoRespaldo.getDescripcion());
    }
}
