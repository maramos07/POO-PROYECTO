package com.inmobiliaria.repositorio;

import com.inmobiliaria.modelo.*;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Repositorio central de datos con persistencia en archivos serializados.
 * Implementa el patrón Singleton para una única instancia en la aplicación.
 * @author Equipo POO
 */
public class RepositorioDatos {

    private static final Logger LOG = Logger.getLogger(RepositorioDatos.class.getName());

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

    /**
     * Obtiene la instancia única del repositorio (Singleton).
     * @return instancia única de RepositorioDatos
     */
    public static RepositorioDatos getInstance() {
        if (instancia == null) {
            instancia = new RepositorioDatos();
        }
        return instancia;
    }

    // ── Generadores de ID ──────────────────────────────────────────────────────

    /**
     * Genera un nuevo ID autoincremental para un inmueble y persiste el contador.
     * @return ID con formato INM-XXXX
     */
    public String generarIdInmueble() {
        String id = String.format("INM-%04d", contadorInmuebles++);
        guardarContadores();
        return id;
    }

    /**
     * Genera un nuevo ID autoincremental para un inquilino y persiste el contador.
     * @return ID con formato INQ-XXXX
     */
    public String generarIdInquilino() {
        String id = String.format("INQ-%04d", contadorInquilinos++);
        guardarContadores();
        return id;
    }

    /**
     * Genera un nuevo ID autoincremental para una factura y persiste el contador.
     * @return ID con formato FAC-XXXX
     */
    public String generarIdFactura() {
        String id = String.format("FAC-%04d", contadorFacturas++);
        guardarContadores();
        return id;
    }

    /**
     * Genera un nuevo ID autoincremental para un movimiento y persiste el contador.
     * @return ID con formato MOV-XXXX
     */
    public String generarIdMovimiento() {
        String id = String.format("MOV-%04d", contadorMovimientos++);
        guardarContadores();
        return id;
    }

    /**
     * Genera un nuevo ID autoincremental para un alquiler y persiste el contador.
     * @return ID con formato ALQ-XXXX
     */
    public String generarIdAlquiler() {
        String id = String.format("ALQ-%04d", contadorAlquileres++);
        guardarContadores();
        return id;
    }

    // ── INMUEBLES ──────────────────────────────────────────────────────────────

    /**
     * Agrega un inmueble al repositorio y persiste los cambios.
     * @param inmueble inmueble a agregar
     */
    public void agregarInmueble(Inmueble inmueble) {
        inmuebles.put(inmueble.getId(), inmueble);
        guardarInmuebles();
    }

    /**
     * Actualiza un inmueble existente en el repositorio y persiste los cambios.
     * @param inmueble inmueble con los datos actualizados
     */
    public void actualizarInmueble(Inmueble inmueble) {
        inmuebles.put(inmueble.getId(), inmueble);
        guardarInmuebles();
    }

    /**
     * Elimina un inmueble por su ID y persiste los cambios.
     * @param id identificador del inmueble
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminarInmueble(String id) {
        boolean eliminado = inmuebles.remove(id) != null;
        if (eliminado) guardarInmuebles();
        return eliminado;
    }

    /**
     * Busca un inmueble por su ID.
     * @param id identificador del inmueble
     * @return el inmueble encontrado o null si no existe
     */
    public Inmueble buscarInmueblePorId(String id) {
        return inmuebles.get(id);
    }

    /**
     * Busca inmuebles cuya dirección contenga el texto dado (búsqueda case-insensitive).
     * @param direccion texto a buscar en la dirección
     * @return lista de inmuebles que coinciden con la búsqueda
     */
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

    /**
     * Obtiene una copia de todos los inmuebles registrados.
     * @return lista de todos los inmuebles
     */
    public List<Inmueble> getTodosInmuebles() {
        return new ArrayList<>(inmuebles.values());
    }

    /**
     * Obtiene los inmuebles marcados como disponibles.
     * @return lista de inmuebles disponibles
     */
    public List<Inmueble> getInmueblesDisponibles() {
        List<Inmueble> result = new ArrayList<>();
        for (Inmueble inm : inmuebles.values()) {
            if (inm.isDisponible()) result.add(inm);
        }
        return result;
    }

    // ── INQUILINOS ─────────────────────────────────────────────────────────────

    /**
     * Agrega un inquilino al repositorio y persiste los cambios.
     * @param inquilino inquilino a agregar
     */
    public void agregarInquilino(Inquilino inquilino) {
        inquilinos.put(inquilino.getId(), inquilino);
        guardarInquilinos();
    }

    /**
     * Actualiza un inquilino existente en el repositorio y persiste los cambios.
     * @param inquilino inquilino con los datos actualizados
     */
    public void actualizarInquilino(Inquilino inquilino) {
        inquilinos.put(inquilino.getId(), inquilino);
        guardarInquilinos();
    }

    /**
     * Elimina un inquilino por su ID y persiste los cambios.
     * @param id identificador del inquilino
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminarInquilino(String id) {
        boolean eliminado = inquilinos.remove(id) != null;
        if (eliminado) guardarInquilinos();
        return eliminado;
    }

    /**
     * Busca un inquilino por su ID.
     * @param id identificador del inquilino
     * @return el inquilino encontrado o null si no existe
     */
    public Inquilino buscarInquilinoPorId(String id) {
        return inquilinos.get(id);
    }

    /**
     * Obtiene una copia de todos los inquilinos registrados.
     * @return lista de todos los inquilinos
     */
    public List<Inquilino> getTodosInquilinos() {
        return new ArrayList<>(inquilinos.values());
    }

    /**
     * Verifica si ya existe un inquilino con la cédula dada.
     * @param cedula cédula a verificar
     * @return true si la cédula ya está registrada
     */
    public boolean existeCedula(String cedula) {
        for (Inquilino inq : inquilinos.values()) {
            if (inq.getCedula().equals(cedula)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si un inquilino tiene alquileres activos.
     * @param inquilinoId identificador del inquilino
     * @return true si existe al menos un alquiler activo para ese inquilino
     */
    public boolean tieneAlquileresActivos(String inquilinoId) {
        for (Alquiler a : alquileres) {
            if (a.getInquilinoId().equals(inquilinoId) && a.isActivo()) {
                return true;
            }
        }
        return false;
    }

    // ── FACTURAS ───────────────────────────────────────────────────────────────

    /**
     * Agrega una factura al repositorio y persiste los cambios.
     * @param factura factura a agregar
     */
    public void agregarFactura(Factura factura) {
        facturas.add(factura);
        guardarFacturas();
    }

    /**
     * Elimina una factura por su ID y persiste los cambios.
     * @param id identificador de la factura
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminarFactura(String id) {
        boolean eliminado = facturas.removeIf(f -> f.getId().equals(id));
        if (eliminado) guardarFacturas();
        return eliminado;
    }

    /**
     * Obtiene las facturas asociadas a un inmueble.
     * @param inmuebleId identificador del inmueble
     * @return lista de facturas del inmueble
     */
    public List<Factura> getFacturasPorInmueble(String inmuebleId) {
        List<Factura> resultado = new ArrayList<>();
        for (Factura f : facturas) {
            if (f.getInmuebleId().toLowerCase().contains(inmuebleId.toLowerCase())) resultado.add(f);
        }
        return resultado;
    }

    /**
     * Obtiene las facturas de un inmueble dentro de un rango de fechas.
     * @param inmuebleId identificador del inmueble
     * @param desde fecha de inicio del período
     * @param hasta fecha de fin del período
     * @return lista de facturas del inmueble en el período
     */
    public List<Factura> getFacturasPorInmuebleYPeriodo(
            String inmuebleId,
            java.time.LocalDate desde,
            java.time.LocalDate hasta) {
        List<Factura> resultado = new ArrayList<>();
        for (Factura f : facturas) {
            if (f.getInmuebleId().toLowerCase().contains(inmuebleId.toLowerCase())
                    && !f.getFechaEmision().isBefore(desde)
                    && !f.getFechaEmision().isAfter(hasta)) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    /**
     * Obtiene una copia de todas las facturas registradas.
     * @return lista de todas las facturas
     */
    public List<Factura> getTodasFacturas() { return new ArrayList<>(facturas); }

    // ── MOVIMIENTOS BANCARIOS ──────────────────────────────────────────────────

    /**
     * Agrega un movimiento bancario al repositorio y persiste los cambios.
     * @param movimiento movimiento bancario a agregar
     */
    public void agregarMovimiento(MovimientoBancario movimiento) {
        movimientos.add(movimiento);
        guardarMovimientos();
    }

    /**
     * Elimina un movimiento bancario por su ID y persiste los cambios.
     * @param id identificador del movimiento
     * @return true si se eliminó correctamente, false si no existía
     */
    public boolean eliminarMovimiento(String id) {
        boolean eliminado = movimientos.removeIf(m -> m.getId().equals(id));
        if (eliminado) guardarMovimientos();
        return eliminado;
    }

    /**
     * Obtiene los movimientos bancarios de un inmueble dentro de un rango de fechas, ordenados por fecha.
     * @param inmuebleId identificador del inmueble
     * @param desde fecha de inicio del período
     * @param hasta fecha de fin del período
     * @return lista de movimientos del inmueble en el período ordenados por fecha
     */
    public List<MovimientoBancario> getMovimientosPorInmuebleYPeriodo(
            String inmuebleId,
            java.time.LocalDate desde,
            java.time.LocalDate hasta) {
        List<MovimientoBancario> resultado = new ArrayList<>();
        for (MovimientoBancario m : movimientos) {
            if (m.getInmuebleId().toLowerCase().contains(inmuebleId.toLowerCase())
                    && !m.getFecha().isBefore(desde)
                    && !m.getFecha().isAfter(hasta)) {
                resultado.add(m);
            }
        }
        resultado.sort(Comparator.comparing(MovimientoBancario::getFecha));
        return resultado;
    }

    /**
     * Obtiene una copia de todos los movimientos bancarios registrados.
     * @return lista de todos los movimientos bancarios
     */
    public List<MovimientoBancario> getTodosMovimientos() {
        return new ArrayList<>(movimientos);
    }

    // ── ALQUILERES ─────────────────────────────────────────────────────────────

    /**
     * Agrega un alquiler al repositorio y persiste los cambios.
     * @param alquiler alquiler a agregar
     */
    public void agregarAlquiler(Alquiler alquiler) {
        alquileres.add(alquiler);
        guardarAlquileres();
    }

    /**
     * Actualiza un alquiler existente o lo agrega si no existe, y persiste los cambios.
     * @param alquiler alquiler con los datos actualizados
     */
    public void actualizarAlquiler(Alquiler alquiler) {
        for (int i = 0; i < alquileres.size(); i++) {
            if (alquileres.get(i).getId().equals(alquiler.getId())) {
                alquileres.set(i, alquiler);
                guardarAlquileres();
                return;
            }
        }
        alquileres.add(alquiler);
        guardarAlquileres();
    }

    /**
     * Obtiene los alquileres activos de un inmueble.
     * @param inmuebleId identificador del inmueble
     * @return lista de alquileres activos del inmueble
     */
    public List<Alquiler> getAlquileresActivosPorInmueble(String inmuebleId) {
        List<Alquiler> resultado = new ArrayList<>();
        for (Alquiler a : alquileres) {
            if (a.getInmuebleId().equals(inmuebleId) && a.isActivo()) resultado.add(a);
        }
        return resultado;
    }

    /**
     * Obtiene una copia de todos los alquileres registrados.
     * @return lista de todos los alquileres
     */
    public List<Alquiler> getTodosAlquileres() { return new ArrayList<>(alquileres); }

    // ── LIMPIEZA DE DATOS HUÉRFANOS ─────────────────────────────────────────────

    /**
     * Elimina todas las facturas asociadas a un inmueble y persiste los cambios.
     * @param inmuebleId identificador del inmueble
     */
    public void eliminarFacturasDeInmueble(String inmuebleId) {
        facturas.removeIf(f -> f.getInmuebleId().equals(inmuebleId));
        guardarFacturas();
    }

    /**
     * Elimina todos los movimientos bancarios asociados a un inmueble y persiste los cambios.
     * @param inmuebleId identificador del inmueble
     */
    public void eliminarMovimientosDeInmueble(String inmuebleId) {
        movimientos.removeIf(m -> m.getInmuebleId().equals(inmuebleId));
        guardarMovimientos();
    }

    /**
     * Elimina todos los alquileres asociados a un inmueble y persiste los cambios.
     * @param inmuebleId identificador del inmueble
     */
    public void eliminarAlquileresDeInmueble(String inmuebleId) {
        alquileres.removeIf(a -> a.getInmuebleId().equals(inmuebleId));
        guardarAlquileres();
    }

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
        } catch (Exception e) {
            LOG.warning("No se pudieron cargar los contadores: " + e.getMessage());
        }
    }

    private void guardarContadores(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CONTADORES))){
            oos.writeObject(new int[]{contadorInmuebles, contadorInquilinos,
                    contadorFacturas, contadorMovimientos, contadorAlquileres});
        } catch (IOException e) {
            LOG.warning("No se pudieron guardar los contadores: " + e.getMessage());
        }
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
        } catch (IOException | ClassNotFoundException e) {
            LOG.warning("No se pudo cargar " + ruta + ": " + e.getMessage());
            return porDefecto;
        }
    }

    private void guardar(String ruta, Object datos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(datos);
        } catch (IOException e) {
            LOG.warning("Error guardando " + ruta + ": " + e.getMessage());
        }
    }

    private void guardarInmuebles()   { guardar(ARCHIVO_INMUEBLES,   inmuebles);   }
    private void guardarInquilinos()  { guardar(ARCHIVO_INQUILINOS,  inquilinos);  }
    private void guardarFacturas()    { guardar(ARCHIVO_FACTURAS,    facturas);    }
    private void guardarMovimientos() { guardar(ARCHIVO_MOVIMIENTOS, movimientos); }
    private void guardarAlquileres()  { guardar(ARCHIVO_ALQUILERES,  alquileres);  }
}
