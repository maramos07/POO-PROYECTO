package poo;

public class MovimientosBancarios {
    private int idDelMovimiento;
    private String tipoMovimiento;
    private Inmueble inmuebleRelacionado;
    private String fecha;
    private double importe;
    private String persona;

    public MovimientosBancarios(int idDelMovimiento, String tipoMovimiento, Inmueble inmuebleRelacionado, String fecha, double importe, String persona) {
        this.idDelMovimiento = idDelMovimiento;
        this.tipoMovimiento = tipoMovimiento;
        this.inmuebleRelacionado = inmuebleRelacionado;
        this.fecha = fecha;
        this.importe = importe;
        this.persona = persona;
    }

    public void mostrarInformacion(){

    }
}
