package com.inmobiliaria.modelo;

import java.io.Serializable;

/**
 * Representa un piso (apartamento/vivienda). Hereda de Inmueble.
 * Puede pertenecer opcionalmente a un Edificio registrado en el sistema.
 */
public class Piso extends Inmueble implements Serializable {
    private static final long serialVersionUID = 1L;

    private int numeroPiso;
    private String tipoEspacio;       // e.g. "Apartamento", "Ático", "Duplex"
    private String descripcionEspecifica;
    private String edificioId;        // Referencia al edificio (puede ser null)

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

    public int getNumeroPiso() { return numeroPiso; }
    public void setNumeroPiso(int numeroPiso) { this.numeroPiso = numeroPiso; }

    public String getTipoEspacio() { return tipoEspacio; }
    public void setTipoEspacio(String tipoEspacio) { this.tipoEspacio = tipoEspacio; }

    public String getDescripcionEspecifica() { return descripcionEspecifica; }
    public void setDescripcionEspecifica(String d) { this.descripcionEspecifica = d; }

    public String getEdificioId() { return edificioId; }
    public void setEdificioId(String edificioId) { this.edificioId = edificioId; }

    @Override
    public String toString() {
        String edif = (edificioId != null && !edificioId.isEmpty())
                ? " | Edificio: " + edificioId : "";
        return super.toString() + String.format(" | Piso Nº%d | Tipo: %s%s",
                numeroPiso, tipoEspacio, edif);
    }
}
