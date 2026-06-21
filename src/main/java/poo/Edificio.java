package poo;

import java.util.ArrayList;

public class Edificio extends Inmueble{
    private ArrayList<Piso> Pisos;
    private ArrayList<Local> Locales;
    public Edificio(String direccion, int IDinterna, String descripcion, int codigoPostal, boolean disponibilidad, int precioAlquiler, ArrayList<Piso> pisos, ArrayList<Local> locales) {
        super(direccion, IDinterna, descripcion, codigoPostal, disponibilidad, precioAlquiler);
        Pisos = pisos;
        Locales = locales;
    }
}
