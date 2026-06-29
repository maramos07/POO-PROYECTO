package com.inmobiliaria.modelo;

import java.io.Serializable;

/**
 * Representa un local (comercial u oficina). Hereda de Inmueble.
 * Puede pertenecer opcionalmente a un Edificio registrado en el sistema.
 * @author Equipo POO
 */
public class Local extends Inmueble implements Serializable {

    private int numeroPiso;
    private String tipoLocal;         // "Local Comercial", "Oficina"
    private String descripcionEspecifica;
    private String edificioId;        // Referencia al edificio (puede ser null)

    /**
     * @param id identificador único
     * @param direccion dirección del local
     * @param numero código interno del local
     * @param descripcion descripción general
     * @param codigoPostal código postal
     * @param precioAlquiler precio de alquiler mensual
     * @param numeroPiso número de piso donde se ubica
     * @param tipoLocal tipo de local (Local Comercial, Oficina, etc.)
     * @param descripcionEspecifica descripción adicional del espacio
     * @param edificioId ID del edificio al que pertenece (opcional)
     */
    public Local(String id, String direccion, String numero,
                 String descripcion, String codigoPostal, double precioAlquiler,
                 int numeroPiso, String tipoLocal,
                 String descripcionEspecifica, String edificioId) {
        super(id, direccion, numero, descripcion, codigoPostal, precioAlquiler);
        this.numeroPiso = numeroPiso;
        this.tipoLocal = tipoLocal;
        this.descripcionEspecifica = descripcionEspecifica;
        this.edificioId = edificioId;
    }

    @Override
    public String getTipoInmueble() {
        return "LOCAL";
    }

    /** @return número de piso */
    public int getNumeroPiso() { return numeroPiso; }
    /** @param numeroPiso nuevo número de piso */
    public void setNumeroPiso(int numeroPiso) { this.numeroPiso = numeroPiso; }

    /** @return tipo de local (Local Comercial, Oficina, etc.) */
    public String getTipoLocal() { return tipoLocal; }
    /** @param tipoLocal nuevo tipo de local */
    public void setTipoLocal(String tipoLocal) { this.tipoLocal = tipoLocal; }

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
                numeroPiso, tipoLocal, edif);
    }
}
