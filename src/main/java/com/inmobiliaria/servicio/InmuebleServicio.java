package com.inmobiliaria.servicio;

import com.inmobiliaria.modelo.*;
import com.inmobiliaria.repositorio.RepositorioDatos;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de negocio para gestión de inmuebles, alquileres,
 * facturas y movimientos bancarios.
 */
public class InmuebleServicio {

    private final RepositorioDatos repo = RepositorioDatos.getInstance();

    // ── INMUEBLES ──────────────────────────────────────────────────────────────

    public String registrarEdificio(String direccion, String numero,
                                    String descripcion, String codigoPostal, double precio,
                                    int numPisos, String nombre) {
        String id = repo.generarIdInmueble();
        Edificio e = new Edificio(id, direccion, numero,
                descripcion, codigoPostal, precio, numPisos, nombre);
        repo.agregarInmueble(e);
        return id;
    }

    public String registrarPiso(String direccion, String numero,
                                String descripcion, String codigoPostal, double precio,
                                int numPiso, String tipoEspacio, String descEsp, String edificioId) {
        String id = repo.generarIdInmueble();
        Piso p = new Piso(id, direccion, numero,
                descripcion, codigoPostal, precio,
                numPiso, tipoEspacio, descEsp, edificioId);
        repo.agregarInmueble(p);
        return id;
    }

    public String registrarLocal(String direccion, String numero,
                                 String descripcion, String codigoPostal, double precio,
                                 int numPiso, String tipoLocal, String descEsp, String edificioId) {
        String id = repo.generarIdInmueble();
        Local l = new Local(id, direccion, numero,
                descripcion, codigoPostal, precio,
                numPiso, tipoLocal, descEsp, edificioId);
        repo.agregarInmueble(l);
        return id;
    }

    public boolean modificarInmueble(String id, String descripcion,
                                     String codigoPostal, double precio) {
        Inmueble inm = repo.buscarInmueblePorId(id);
        if (inm == null) return false;
        inm.setDescripcion(descripcion);
        inm.setCodigoPostal(codigoPostal);
        inm.setPrecioAlquiler(precio);
        repo.actualizarInmueble(inm);
        return true;
    }

    public boolean eliminarInmueble(String id) {
        return repo.eliminarInmueble(id);
    }

    public List<Inmueble> consultarPorDireccion(String direccion) {
        return repo.buscarInmueblesPorDireccion(direccion);
    }

    public List<Inmueble> getTodosInmuebles() {
        return repo.getTodosInmuebles();
    }

    public Inmueble buscarPorId(String id) {
        return repo.buscarInmueblePorId(id);
    }

    // ── INQUILINOS ─────────────────────────────────────────────────────────────

    public String registrarInquilino(String nombre, String cedula, int edad,
                                     String sexo, String fotografia, String contacto,
                                     Inquilino.TipoRespaldo respaldo) {
        String id = repo.generarIdInquilino();
        Inquilino inq = new Inquilino(id, nombre, cedula, edad,
                sexo, fotografia, contacto, respaldo);
        repo.agregarInquilino(inq);
        return id;
    }

    public List<Inquilino> getTodosInquilinos() {
        return repo.getTodosInquilinos();
    }

    public Inquilino buscarInquilinoPorId(String id) {
        return repo.buscarInquilinoPorId(id);
    }

    // ── ALQUILER / DESALQUILER ─────────────────────────────────────────────────

    /**
     * Registra el alquiler de un inmueble disponible a un inquilino registrado.
     * @return ID del alquiler o null si no es posible.
     */
    public String alquilarInmueble(String inmuebleId, String inquilinoId) {
        Inmueble inm = repo.buscarInmueblePorId(inmuebleId);
        Inquilino inq = repo.buscarInquilinoPorId(inquilinoId);
        if (inm == null || inq == null || !inm.isDisponible()) return null;

        String idAlq = repo.generarIdAlquiler();
        Alquiler alq = new Alquiler(idAlq, inquilinoId, inmuebleId, LocalDate.now());
        inm.setDisponible(false);
        inm.setInquilinoId(inquilinoId);
        repo.actualizarInmueble(inm);
        repo.agregarAlquiler(alq);

        // Registrar movimiento bancario automático
        MovimientoBancario mov = new MovimientoBancario(
                repo.generarIdMovimiento(),
                MovimientoBancario.TipoMovimiento.INGRESO_ALQUILER,
                inmuebleId, LocalDate.now(),
                inm.getPrecioAlquiler(), inq.getNombre());
        repo.agregarMovimiento(mov);

        return idAlq;
    }

    /**
     * Registra el desalquiler: libera el inmueble y cierra el contrato activo.
     */
    public boolean desalquilarInmueble(String inmuebleId) {
        Inmueble inm = repo.buscarInmueblePorId(inmuebleId);
        if (inm == null || inm.isDisponible()) return false;

        List<Alquiler> activos = repo.getAlquileresActivosPorInmueble(inmuebleId);
        for (Alquiler alq : activos) {
            alq.finalizar(LocalDate.now());
            repo.actualizarAlquiler(alq);
        }

        inm.setDisponible(true);
        inm.setInquilinoId(null);
        repo.actualizarInmueble(inm);
        return true;
    }

    public List<Alquiler> getTodosAlquileres() {
        return repo.getTodosAlquileres();
    }

    // ── FACTURAS ───────────────────────────────────────────────────────────────

    public String registrarFactura(String inmuebleId, LocalDate fecha,
                                   Factura.ConceptoFactura concepto, String proveedor, double costo) {
        String id = repo.generarIdFactura();
        Factura f = new Factura(id, fecha, inmuebleId, concepto, proveedor, costo);
        repo.agregarFactura(f);
        return id;
    }

    public List<Factura> consultarFacturasPorInmueble(String inmuebleId) {
        return repo.getFacturasPorInmueble(inmuebleId);
    }

    public List<Factura> consultarFacturasPorPeriodo(
            String inmuebleId, LocalDate desde, LocalDate hasta) {
        return repo.getFacturasPorInmuebleYPeriodo(inmuebleId, desde, hasta);
    }

    public List<Factura> getTodasFacturas() {
        return repo.getTodasFacturas();
    }

    // ── MOVIMIENTOS BANCARIOS ──────────────────────────────────────────────────

    public String registrarMovimiento(String inmuebleId,
                                      MovimientoBancario.TipoMovimiento tipo,
                                      LocalDate fecha, double importe, String personaEntidad) {
        String id = repo.generarIdMovimiento();
        MovimientoBancario m = new MovimientoBancario(
                id, tipo, inmuebleId, fecha, importe, personaEntidad);
        repo.agregarMovimiento(m);
        return id;
    }

    public List<MovimientoBancario> consultarMovimientosPorPeriodo(
            String inmuebleId, LocalDate desde, LocalDate hasta) {
        return repo.getMovimientosPorInmuebleYPeriodo(inmuebleId, desde, hasta);
    }

    public List<MovimientoBancario> getTodosMovimientos() {
        return repo.getTodosMovimientos();
    }
}
