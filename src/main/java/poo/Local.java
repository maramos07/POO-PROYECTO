package poo;

public class Local extends Inmueble {
    private int nDePiso;
    private String tipoEspacio;
    private String descripcionEsp;
    private Edificio edificio;

    public Local(String direccion, int IDinterna, String descripcion, int codigoPostal, boolean disponibilidad, int precioAlquiler, int nDePiso, String tipoEspacio, String descripcionEsp, Edificio edificio) {
        super(direccion, IDinterna, descripcion, codigoPostal, disponibilidad, precioAlquiler);
        this.nDePiso = nDePiso;
        this.tipoEspacio = tipoEspacio;
        this.descripcionEsp = descripcionEsp;
        this.edificio = edificio;
    }
}
