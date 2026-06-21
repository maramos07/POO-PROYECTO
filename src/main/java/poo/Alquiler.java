package poo;

public class Alquiler {
    private int idAlquier;
    private Inquilino inquilino;
    private Inmueble inmueble;
    private String fechaInicio;
    private String fechaFin;
    private double costoPactado;
    private String estado;


    public Alquiler(int idAlquier, Inquilino inquilino, Inmueble inmueble, String fechaInicio, String fechaFin, double costoPactado, String estado) {
        this.idAlquier = idAlquier;
        this.inquilino = inquilino;
        this.inmueble = inmueble;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costoPactado = costoPactado;
        this.estado = estado;
    }

    public void generarContrato(){
        System.out.println("ola");
    };
}
