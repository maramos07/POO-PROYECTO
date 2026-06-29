package com.inmobiliaria.modelo;

import java.io.Serializable;

/**
 * Representa un piso (apartamento/vivienda). Hereda de Inmueble.
 * Puede pertenecer opcionalmente a un Edificio registrado en el sistema.
 * @author Equipo POO
 */
public class Piso extends Inmueble implements Serializable {

    private int numeroPiso;
    private String tipoEspacio;       // e.g. "Apartamento", "Ático", "Duplex"
    private String descripcionEspecifica;
    private String edificioId;        // Referencia al edificio (puede ser null)

    /**
     * @param id identificador único
     * @param direccion dirección del piso
     * @param numero código interno del piso
     * @param descripcion descripción general
     * @param codigoPostal código postal
     * @param precioAlquiler precio de alquiler mensual
     * @param numeroPiso número de piso en el edificio
     * @param tipoEspacio tipo de espacio (Apartamento, Ático, etc.)
     * @param descripcionEspecifica descripción adicional del espacio
     * @param edificioId ID del edificio al que pertenece (opcional)
     */
    public Piso(String id, String direccion, String numero,
                String descripcion, String codigoPostal, double precioAlquiler,
                int numeroPiso, String tipoEspacio,
                String descripcionEspecifica, String edificioId) {
        super(id, direccion, numero, descripcion, codigoPostal, precioAlquiler);
        this.numeroPiso = numeroPiso;
        this.tipoEspacio = tipoEspacio;
        this.descripcionEspecifica = descripcionEspecifica;
        this.edificioId = edificioId;
    }

    @Override
    public String getTipoInmueble() {
        return "PISO";
    }

    /** @return número de piso */
    public int getNumeroPiso() { return numeroPiso; }
    /** @param numeroPiso nuevo número de piso */
    public void setNumeroPiso(int numeroPiso) { this.numeroPiso = numeroPiso; }

    /** @return tipo de espacio (Apartamento, Ático, etc.) */
    public String getTipoEspacio() { return tipoEspacio; }
    /** @param tipoEspacio nuevo tipo de espacio */
    public void setTipoEspacio(String tipoEspacio) { this.tipoEspacio = tipoEspacio; }

    /** @return descripción específica del espacio */
    public String getDescripcionEspecifica() { return descripcionEspecifica; }
    /** @param d nueva descripción específica */
    public void setDescripcionEspecifica(String d) { this.descripcionEspecifica = d; }

    /** @return ID del edificio asociado (null si no tiene) */
    public String getEdificioId() { return edificioId; }
    /** @param edificioId nuevo ID de edificio asociado */
    public void setEdificioId(String edificioId) { this.edificioId = edificioId; }

    @Override
    public String toString() {
        String edif = (edificioId != null && !edificioId.isEmpty())
                ? " | Edificio: " + edificioId : "";
        return super.toString() + String.format(" | Piso Nº%d | Tipo: %s%s",
                numeroPiso, tipoEspacio, edif);
    }
}
