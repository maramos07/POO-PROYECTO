package com.inmobiliaria.repositorio;

import com.inmobiliaria.modelo.*;

import java.io.*;
import java.util.*;

/**
 * Repositorio central de datos con persistencia en archivos serializados.
 * Implementa el patrón Singleton para una única instancia en la aplicación.
 */
public class RepositorioDatos {

    private static final String DIR_DATOS = "datos/";
    private static final String ARCHIVO_INMUEBLES     = DIR_DATOS + "inmuebles.dat";
    private static final String ARCHIVO_INQUILINOS    = DIR_DATOS + "inquilinos.dat";
    private static final String ARCHIVO_FACTURAS      = DIR_DATOS + "facturas.dat";
    private static final String ARCHIVO_MOVIMIENTOS   = DIR_DATOS + "movimientos.dat";
    private static final String ARCHIVO_ALQUILERES    = DIR_DATOS + "alquileres.dat";
    private static final String ARCHIVO_CONTADORES = "contadores.dat";

    private static RepositorioDatos instancia;

    private Map<String, Inmueble>          inmuebles   = new HashMap<>();
    private Map<String, Inquilino>         inquilinos  = new HashMap<>();
    private List<Factura>                  facturas    = new ArrayList<>();
    private List<MovimientoBancario>       movimientos = new ArrayList<>();
    private List<Alquiler>                 alquileres  = new ArrayList<>();

    // Contadores para generación de IDs
    private int contadorInmuebles   = 1;
    private int contadorInquilinos  = 1;
    private int contadorFacturas    = 1;
    private int contadorMovimientos = 1;
    private int contadorAlquileres  = 1;

    private RepositorioDatos() {
        new File(DIR_DATOS).mkdirs();
        cargarTodo();
    }

    public static RepositorioDatos getInstance() {
        if (instancia == null) {
            instancia = new RepositorioDatos();
        }
        return instancia;
    }

    // ── Generadores de ID ──────────────────────────────────────────────────────

    public String generarIdInmueble() {
        String id = String.format("INM-%04d", contadorInmuebles++);
        guardarContadores();
        return id;
    }

    public String generarIdInquilino() {
        String id = String.format("INQ-%04d", contadorInquilinos++);
        guardarContadores();
        return id;
    }

    public String generarIdFactura() {
        String id = String.format("FAC-%04d", contadorFacturas++);
        guardarContadores();
        return id;
    }

    public String generarIdMovimiento() {
        String id = String.format("MOV-%04d", contadorMovimientos++);
        guardarContadores();
        return id;
    }

    public String generarIdAlquiler() {
        String id = String.format("ALQ-%04d", contadorAlquileres++);
        guardarContadores();
        return id;
    }

    // ── INMUEBLES ──────────────────────────────────────────────────────────────

    public void agregarInmueble(Inmueble inmueble) {
        inmuebles.put(inmueble.getId(), inmueble);
        guardarInmuebles();
    }

    public void actualizarInmueble(Inmueble inmueble) {
        inmuebles.put(inmueble.getId(), inmueble);
        guardarInmuebles();
    }

    public boolean eliminarInmueble(String id) {
        boolean eliminado = inmuebles.remove(id) != null;
        if (eliminado) guardarInmuebles();
        return eliminado;
    }

    public Inmueble buscarInmueblePorId(String id) {
        return inmuebles.get(id);
    }

    public List<Inmueble> buscarInmueblesPorDireccion(String direccion) {
        List<Inmueble> resultado = new ArrayList<>();
        String busq = direccion.toLowerCase().trim();
        for (Inmueble inm : inmuebles.values()) {
            if (inm.getDireccion().toLowerCase().contains(busq)) {
                resultado.add(inm);
            }
        }
        return resultado;
    }

    public List<Inmueble> getTodosInmuebles() {
        return new ArrayList<>(inmuebles.values());
    }

    public List<Inmueble> getInmueblesDisponibles() {
        List<Inmueble> result = new ArrayList<>();
        for (Inmueble inm : inmuebles.values()) {
            if (inm.isDisponible()) result.add(inm);
        }
        return result;
    }

    // ── INQUILINOS ─────────────────────────────────────────────────────────────

    public void agregarInquilino(Inquilino inquilino) {
        inquilinos.put(inquilino.getId(), inquilino);
        guardarInquilinos();
    }

    public void actualizarInquilino(Inquilino inquilino) {
        inquilinos.put(inquilino.getId(), inquilino);
        guardarInquilinos();
    }

    public boolean eliminarInquilino(String id) {
        boolean eliminado = inquilinos.remove(id) != null;
        if (eliminado) guardarInquilinos();
        return eliminado;
    }

    public Inquilino buscarInquilinoPorId(String id) {
        return inquilinos.get(id);
    }

    public List<Inquilino> getTodosInquilinos() {
        return new ArrayList<>(inquilinos.values());
    }

    // ── FACTURAS ───────────────────────────────────────────────────────────────

    public void agregarFactura(Factura factura) {
        facturas.add(factura);
        guardarFacturas();
    }

    public boolean eliminarFactura(String id) {
        boolean eliminado = facturas.removeIf(f -> f.getId().equals(id));
        if (eliminado) guardarFacturas();
        return eliminado;
    }

    public List<Factura> getFacturasPorInmueble(String inmuebleId) {
        List<Factura> resultado = new ArrayList<>();
        for (Factura f : facturas) {
            if (f.getInmuebleId().equals(inmuebleId)) resultado.add(f);
        }
        return resultado;
    }

    public List<Factura> getFacturasPorInmuebleYPeriodo(
            String inmuebleId,
            java.time.LocalDate desde,
            java.time.LocalDate hasta) {
        List<Factura> resultado = new ArrayList<>();
        for (Factura f : facturas) {
            if (f.getInmuebleId().equals(inmuebleId)
                    && !f.getFechaEmision().isBefore(desde)
                    && !f.getFechaEmision().isAfter(hasta)) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    public List<Factura> getTodasFacturas() { return new ArrayList<>(facturas); }

    // ── MOVIMIENTOS BANCARIOS ──────────────────────────────────────────────────

    public void agregarMovimiento(MovimientoBancario movimiento) {
        movimientos.add(movimiento);
        guardarMovimientos();
    }

    public boolean eliminarMovimiento(String id) {
        boolean eliminado = movimientos.removeIf(m -> m.getId().equals(id));
        if (eliminado) guardarMovimientos();
        return eliminado;
    }

    public List<MovimientoBancario> getMovimientosPorInmuebleYPeriodo(
            String inmuebleId,
            java.time.LocalDate desde,
            java.time.LocalDate hasta) {
        List<MovimientoBancario> resultado = new ArrayList<>();
        for (MovimientoBancario m : movimientos) {
            if (m.getInmuebleId().equals(inmuebleId)
                    && !m.getFecha().isBefore(desde)
                    && !m.getFecha().isAfter(hasta)) {
                resultado.add(m);
            }
        }
        resultado.sort(Comparator.comparing(MovimientoBancario::getFecha));
        return resultado;
    }

    public List<MovimientoBancario> getTodosMovimientos() {
        return new ArrayList<>(movimientos);
    }

    // ── ALQUILERES ─────────────────────────────────────────────────────────────

    public void agregarAlquiler(Alquiler alquiler) {
        alquileres.add(alquiler);
        guardarAlquileres();
    }

    public void actualizarAlquiler(Alquiler alquiler) {
        guardarAlquileres();
    }

    public List<Alquiler> getAlquileresActivosPorInmueble(String inmuebleId) {
        List<Alquiler> resultado = new ArrayList<>();
        for (Alquiler a : alquileres) {
            if (a.getInmuebleId().equals(inmuebleId) && a.isActivo()) resultado.add(a);
        }
        return resultado;
    }

    public List<Alquiler> getTodosAlquileres() { return new ArrayList<>(alquileres); }

    // ── PERSISTENCIA ───────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private void cargarContadores(){
        try(ObjectInputStream ios = new ObjectInputStream(new FileInputStream(ARCHIVO_CONTADORES))){
            int[] c = (int[]) ios.readObject();
            contadorInmuebles   = c[0];
            contadorInquilinos  = c[1];
            contadorFacturas    = c[2];
            contadorMovimientos = c[3];
            contadorAlquileres  = c[4];
        } catch (Exception ignored) {}
    }

    private void guardarContadores(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CONTADORES))){
            oos.writeObject(new int[]{contadorInmuebles, contadorInquilinos,
                    contadorFacturas, contadorMovimientos, contadorAlquileres});
        }catch(Exception ignored) {}
    }

    private void cargarTodo() {
        inmuebles   = cargarArchivo(ARCHIVO_INMUEBLES,   new HashMap<>());
        inquilinos  = cargarArchivo(ARCHIVO_INQUILINOS,  new HashMap<>());
        facturas    = cargarArchivo(ARCHIVO_FACTURAS,    new ArrayList<>());
        movimientos = cargarArchivo(ARCHIVO_MOVIMIENTOS, new ArrayList<>());
        alquileres  = cargarArchivo(ARCHIVO_ALQUILERES,  new ArrayList<>());

        // Carga de contadores
        cargarContadores();
    }

    @SuppressWarnings("unchecked")
    private <T> T cargarArchivo(String ruta, T porDefecto) {
        File f = new File(ruta);
        if (!f.exists()) return porDefecto;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (T) ois.readObject();
        } catch (Exception e) {
            return porDefecto;
        }
    }

    private void guardar(String ruta, Object datos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(datos);
        } catch (IOException e) {
            System.err.println("Error guardando " + ruta + ": " + e.getMessage());
        }
    }

    private void guardarInmuebles()   { guardar(ARCHIVO_INMUEBLES,   inmuebles);   }
    private void guardarInquilinos()  { guardar(ARCHIVO_INQUILINOS,  inquilinos);  }
    private void guardarFacturas()    { guardar(ARCHIVO_FACTURAS,    facturas);    }
    private void guardarMovimientos() { guardar(ARCHIVO_MOVIMIENTOS, movimientos); }
    private void guardarAlquileres()  { guardar(ARCHIVO_ALQUILERES,  alquileres);  }
}
