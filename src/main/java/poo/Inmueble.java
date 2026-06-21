package poo;

public abstract class Inmueble {
    private String direccion;
    private int IDinterna;
    private String descripcion;
    private int codigoPostal;
    private int precioAlquiler;
    private boolean disponibilidad;

    public Inmueble(String direccion, int IDinterna, String descripcion, int codigoPostal, boolean disponibilidad, int precioAlquiler) {
        this.direccion = direccion;
        this.IDinterna = IDinterna;
        this.descripcion = descripcion;
        this.codigoPostal = codigoPostal;
        this.disponibilidad = disponibilidad;
        this.precioAlquiler = precioAlquiler;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getIDinterna() {
        return IDinterna;
    }

    public void setIDinterna(int IDinterna) {
        this.IDinterna = IDinterna;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(int codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public int getPrecioAlquiler() {
        return precioAlquiler;
    }

    public void setPrecioAlquiler(int precioAlquiler) {
        this.precioAlquiler = precioAlquiler;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}
