package poo;

public class Factura {
    private String fecha;
    private Inmueble inmuebleAsociado;
    private String concepto;
    private String proveedor;
    private int costos;

    public Factura(String fecha, Inmueble inmuebleAsociado, String concepto, String proveedor, int costos) {
        this.fecha = fecha;
        this.inmuebleAsociado = inmuebleAsociado;
        this.concepto = concepto;
        this.proveedor = proveedor;
        this.costos = costos;
    }

    public void mostrarinformacion(){
        
    }
}
