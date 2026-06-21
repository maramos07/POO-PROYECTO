package poo;

public class Inquilino {
    private boolean condicionRespaldo;
    private String nombre;
    private long cedula;
    private int edad;
    private String sexo;
    private String medioContacto;
    private String tipoRespaldo;
    private int ID;

    public Inquilino(boolean condicionRespaldo, String nombre, long cedula, int edad, String medioContacto, String sexo, String tipoRespaldo, int ID) {
        this.condicionRespaldo = condicionRespaldo;
        this.nombre = nombre;
        this.cedula = cedula;
        this.edad = edad;
        this.medioContacto = medioContacto;
        this.sexo = sexo;
        this.tipoRespaldo = tipoRespaldo;
        this.ID = ID;
    }

    public boolean isCondicionRespaldo() {
        return condicionRespaldo;
    }

    public void setCondicionRespaldo(boolean condicionRespaldo) {
        this.condicionRespaldo = condicionRespaldo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public long getCedula() {
        return cedula;
    }

    public void setCedula(long cedula) {
        this.cedula = cedula;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getMedioContacto() {
        return medioContacto;
    }

    public void setMedioContacto(String medioContacto) {
        this.medioContacto = medioContacto;
    }

    public String getTipoRespaldo() {
        return tipoRespaldo;
    }

    public void setTipoRespaldo(String tipoRespaldo) {
        this.tipoRespaldo = tipoRespaldo;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

}
