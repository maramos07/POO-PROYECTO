package com.inmobiliaria.servicio;

import com.inmobiliaria.modelo.*;
import com.inmobiliaria.repositorio.RepositorioDatos;
import com.inmobiliaria.util.Validador;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de negocio para gestión de inmuebles, alquileres,
 * facturas y movimientos bancarios.
 * @author Equipo POO
 */
public class InmuebleServicio {

    private final RepositorioDatos repo = RepositorioDatos.getInstance();

    // ── INMUEBLES ──────────────────────────────────────────────────────────────

    /**
     * Registra un nuevo edificio.
     * @param direccion  dirección del edificio
     * @param numero     número de la calle
     * @param descripcion descripción del inmueble
     * @param codigoPostal código postal del inmueble
     * @param precio     precio de alquiler
     * @param numPisos   número de pisos del edificio
     * @param nombre     nombre del edificio
     * @return ID del edificio registrado
     * @throws IllegalArgumentException si el código postal no es válido
     */
    public String registrarEdificio(String direccion, String numero,
                                    String descripcion, String codigoPostal, double precio,
                                    int numPisos, String nombre) {
        Validador.validarCodigoPostal(codigoPostal);
        String id = repo.generarIdInmueble();
        Edificio e = new Edificio(id, direccion, numero,
                descripcion, codigoPostal, precio, numPisos, nombre);
        repo.agregarInmueble(e);
        return id;
    }

    /**
     * Registra un nuevo piso dentro de un edificio.
     * @param direccion   dirección del piso
     * @param numero      número de la calle
     * @param descripcion descripción del inmueble
     * @param codigoPostal código postal
     * @param precio      precio de alquiler
     * @param numPiso     número de piso
     * @param tipoEspacio tipo de espacio (vivienda, oficina, etc.)
     * @param descEsp     descripción específica del espacio
     * @param edificioId  ID del edificio al que pertenece (puede ser null)
     * @return ID del piso registrado
     * @throws IllegalArgumentException si el código postal no es válido,
     *                                  o el ID de edificio no existe o no es un Edificio
     */
    public String registrarPiso(String direccion, String numero,
                                String descripcion, String codigoPostal, double precio,
                                int numPiso, String tipoEspacio, String descEsp, String edificioId) {
        Validador.validarCodigoPostal(codigoPostal);
        if (edificioId != null && !edificioId.isEmpty()) {
            Inmueble edificioRef = repo.buscarInmueblePorId(edificioId);
            if (!(edificioRef instanceof Edificio)) {
                throw new IllegalArgumentException("El ID de edificio \"" + edificioId + "\" no existe o no es un edificio.");
            }
        }
        String id = repo.generarIdInmueble();
        Piso p = new Piso(id, direccion, numero,
                descripcion, codigoPostal, precio,
                numPiso, tipoEspacio, descEsp, edificioId);
        repo.agregarInmueble(p);
        return id;
    }

    /**
     * Registra un nuevo local dentro de un edificio.
     * @param direccion   dirección del local
     * @param numero      número de la calle
     * @param descripcion descripción del inmueble
     * @param codigoPostal código postal
     * @param precio      precio de alquiler
     * @param numPiso     número de piso donde se ubica
     * @param tipoLocal   tipo de local (comercial, oficina, etc.)
     * @param descEsp     descripción específica del local
     * @param edificioId  ID del edificio al que pertenece (puede ser null)
     * @return ID del local registrado
     * @throws IllegalArgumentException si el código postal no es válido,
     *                                  o el ID de edificio no existe o no es un Edificio
     */
    public String registrarLocal(String direccion, String numero,
                                 String descripcion, String codigoPostal, double precio,
                                 int numPiso, String tipoLocal, String descEsp, String edificioId) {
        Validador.validarCodigoPostal(codigoPostal);
        if (edificioId != null && !edificioId.isEmpty()) {
            Inmueble edificioRef = repo.buscarInmueblePorId(edificioId);
            if (!(edificioRef instanceof Edificio)) {
                throw new IllegalArgumentException("El ID de edificio \"" + edificioId + "\" no existe o no es un edificio.");
            }
        }
        String id = repo.generarIdInmueble();
        Local l = new Local(id, direccion, numero,
                descripcion, codigoPostal, precio,
                numPiso, tipoLocal, descEsp, edificioId);
        repo.agregarInmueble(l);
        return id;
    }

    /**
     * Modifica los datos generales de cualquier inmueble.
     * @param id          ID del inmueble a modificar
     * @param direccion   nueva dirección
     * @param numero      nuevo número de calle
     * @param descripcion nueva descripción
     * @param codigoPostal nuevo código postal
     * @param precio      nuevo precio de alquiler
     * @return true si se modificó correctamente, false si el ID no existe
     * @throws IllegalArgumentException si el código postal no es válido
     */
    public boolean modificarInmueble(String id, String direccion, String numero,
                                     String descripcion, String codigoPostal, double precio) {
        Validador.validarCodigoPostal(codigoPostal);
        Inmueble inm = repo.buscarInmueblePorId(id);
        if (inm == null) return false;
        inm.setDireccion(direccion);
        inm.setNumero(numero);
        inm.setDescripcion(descripcion);
        inm.setCodigoPostal(codigoPostal);
        inm.setPrecioAlquiler(precio);
        repo.actualizarInmueble(inm);
        return true;
    }

    /**
     * Modifica los atributos específicos de un edificio.
     * @param id            ID del edificio a modificar
     * @param numPisos      nuevo número de pisos
     * @param nombreEdificio nuevo nombre del edificio
     * @return true si se modificó correctamente, false si el ID no existe o no es un Edificio
     */
    public boolean modificarEdificio(String id, int numPisos, String nombreEdificio) {
        Inmueble inm = repo.buscarInmueblePorId(id);
        if (!(inm instanceof Edificio e)) return false;
        e.setNumeroPisos(numPisos);
        e.setNombreEdificio(nombreEdificio);
        repo.actualizarInmueble(e);
        return true;
    }

    /**
     * Modifica los atributos específicos de un piso.
     * @param id         ID del piso a modificar
     * @param numPiso    nuevo número de piso
     * @param tipoEspacio nuevo tipo de espacio
     * @param descEsp    nueva descripción específica
     * @param edificioId nuevo ID de edificio (puede ser vacío para desvincular)
     * @return true si se modificó correctamente, false si el ID no existe o no es un Piso
     * @throws IllegalArgumentException si el ID de edificio no existe o no es un Edificio
     */
    public boolean modificarPiso(String id, int numPiso, String tipoEspacio,
                                 String descEsp, String edificioId) {
        Inmueble inm = repo.buscarInmueblePorId(id);
        if (!(inm instanceof Piso p)) return false;
        if (edificioId != null && !edificioId.isEmpty()) {
            Inmueble edificioRef = repo.buscarInmueblePorId(edificioId);
            if (!(edificioRef instanceof Edificio)) {
                throw new IllegalArgumentException("El ID de edificio \"" + edificioId + "\" no existe o no es un edificio.");
            }
        }
        p.setNumeroPiso(numPiso);
        p.setTipoEspacio(tipoEspacio);
        p.setDescripcionEspecifica(descEsp);
        p.setEdificioId(edificioId.isEmpty() ? null : edificioId);
        repo.actualizarInmueble(p);
        return true;
    }

    /**
     * Modifica los atributos específicos de un local.
     * @param id         ID del local a modificar
     * @param numPiso    nuevo número de piso
     * @param tipoLocal  nuevo tipo de local
     * @param descEsp    nueva descripción específica
     * @param edificioId nuevo ID de edificio (puede ser vacío para desvincular)
     * @return true si se modificó correctamente, false si el ID no existe o no es un Local
     * @throws IllegalArgumentException si el ID de edificio no existe o no es un Edificio
     */
    public boolean modificarLocal(String id, int numPiso, String tipoLocal,
                                  String descEsp, String edificioId) {
        Inmueble inm = repo.buscarInmueblePorId(id);
        if (!(inm instanceof Local l)) return false;
        if (edificioId != null && !edificioId.isEmpty()) {
            Inmueble edificioRef = repo.buscarInmueblePorId(edificioId);
            if (!(edificioRef instanceof Edificio)) {
                throw new IllegalArgumentException("El ID de edificio \"" + edificioId + "\" no existe o no es un edificio.");
            }
        }
        l.setNumeroPiso(numPiso);
        l.setTipoLocal(tipoLocal);
        l.setDescripcionEspecifica(descEsp);
        l.setEdificioId(edificioId.isEmpty() ? null : edificioId);
        repo.actualizarInmueble(l);
        return true;
    }

    /**
     * Elimina un inmueble si está disponible (no alquilado).
     * @param id ID del inmueble a eliminar
     * @return true si se eliminó correctamente, false si no existe o no está disponible
     */
    public boolean eliminarInmueble(String id) {
        Inmueble inm = repo.buscarInmueblePorId(id);
        if (inm == null || !inm.isDisponible()) {
            return false;
        }
        repo.eliminarAlquileresDeInmueble(id);
        repo.eliminarFacturasDeInmueble(id);
        repo.eliminarMovimientosDeInmueble(id);
        return repo.eliminarInmueble(id);
    }

    /**
     * Busca inmuebles por dirección.
     * @param direccion dirección a consultar
     * @return lista de inmuebles que coinciden con la dirección
     */
    public List<Inmueble> consultarPorDireccion(String direccion) {
        return repo.buscarInmueblesPorDireccion(direccion);
    }

    /**
     * Obtiene todos los inmuebles registrados.
     * @return lista completa de inmuebles
     */
    public List<Inmueble> getTodosInmuebles() {
        return repo.getTodosInmuebles();
    }

    /**
     * Busca un inmueble por su ID.
     * @param id ID del inmueble
     * @return el inmueble encontrado, o null si no existe
     */
    public Inmueble buscarPorId(String id) {
        return repo.buscarInmueblePorId(id);
    }

    // ── INQUILINOS ─────────────────────────────────────────────────────────────

    /**
     * Registra un nuevo inquilino.
     * @param nombre   nombre completo del inquilino
     * @param cedula   cédula de identidad
     * @param edad     edad del inquilino
     * @param sexo     sexo del inquilino
     * @param contacto información de contacto
     * @param respaldo tipo de respaldo financiero
     * @return ID del inquilino registrado
     * @throws IllegalArgumentException si la cédula no es válida o ya está registrada
     */
    public String registrarInquilino(String nombre, String cedula, int edad,
                                     String sexo, String contacto,
                                     Inquilino.TipoRespaldo respaldo) {
        Validador.validarCedula(cedula);
        if (repo.existeCedula(cedula)) {
            throw new IllegalArgumentException("Ya existe un inquilino con esa cédula.");
        }
        String id = repo.generarIdInquilino();
        Inquilino inq = new Inquilino(id, nombre, cedula, edad,
                sexo, contacto, respaldo);
        repo.agregarInquilino(inq);
        return id;
    }

    /**
     * Obtiene todos los inquilinos registrados.
     * @return lista completa de inquilinos
     */
    public List<Inquilino> getTodosInquilinos() {
        return repo.getTodosInquilinos();
    }

    /**
     * Busca un inquilino por su ID.
     * @param id ID del inquilino
     * @return el inquilino encontrado, o null si no existe
     */
    public Inquilino buscarInquilinoPorId(String id) {
        return repo.buscarInquilinoPorId(id);
    }

    /**
     * Elimina un inquilino si no tiene alquileres activos.
     * @param id ID del inquilino a eliminar
     * @return true si se eliminó correctamente, false si tiene alquileres activos o no existe
     */
    public boolean eliminarInquilino(String id) {
        if (repo.tieneAlquileresActivos(id)) {
            return false;
        }
        return repo.eliminarInquilino(id);
    }

    // ── ALQUILER / DESALQUILER ─────────────────────────────────────────────────

    /**
     * Registra el alquiler de un inmueble disponible a un inquilino registrado.
     * @param inmuebleId  ID del inmueble a alquilar
     * @param inquilinoId ID del inquilino
     * @return ID del alquiler o null si no es posible (inmueble no disponible, o IDs inválidos)
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
     * @param inmuebleId ID del inmueble a desalquilar
     * @return true si se desalquiló correctamente, false si no existe o ya está disponible
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

    /**
     * Obtiene todos los alquileres registrados.
     * @return lista completa de alquileres
     */
    public List<Alquiler> getTodosAlquileres() {
        return repo.getTodosAlquileres();
    }

    // ── FACTURAS ───────────────────────────────────────────────────────────────

    /**
     * Registra una nueva factura para un inmueble.
     * @param inmuebleId ID del inmueble asociado
     * @param fecha      fecha de la factura
     * @param concepto   concepto de la factura
     * @param proveedor  nombre del proveedor
     * @param costo      monto de la factura
     * @return ID de la factura registrada
     */
    public String registrarFactura(String inmuebleId, LocalDate fecha,
                                   Factura.ConceptoFactura concepto, String proveedor, double costo) {
        String id = repo.generarIdFactura();
        Factura f = new Factura(id, fecha, inmuebleId, concepto, proveedor, costo);
        repo.agregarFactura(f);
        return id;
    }

    /**
     * Consulta todas las facturas de un inmueble.
     * @param inmuebleId ID del inmueble
     * @return lista de facturas del inmueble
     */
    public List<Factura> consultarFacturasPorInmueble(String inmuebleId) {
        return repo.getFacturasPorInmueble(inmuebleId);
    }

    /**
     * Consulta facturas de un inmueble en un período determinado.
     * @param inmuebleId ID del inmueble
     * @param desde      fecha de inicio del período
     * @param hasta      fecha de fin del período
     * @return lista de facturas del inmueble en el período
     */
    public List<Factura> consultarFacturasPorPeriodo(
            String inmuebleId, LocalDate desde, LocalDate hasta) {
        return repo.getFacturasPorInmuebleYPeriodo(inmuebleId, desde, hasta);
    }

    /**
     * Obtiene todas las facturas registradas.
     * @return lista completa de facturas
     */
    public List<Factura> getTodasFacturas() {
        return repo.getTodasFacturas();
    }

    // ── MOVIMIENTOS BANCARIOS ──────────────────────────────────────────────────

    /**
     * Registra un nuevo movimiento bancario.
     * @param inmuebleId     ID del inmueble asociado
     * @param tipo           tipo de movimiento (ingreso/gasto)
     * @param fecha          fecha del movimiento
     * @param importe        monto del movimiento
     * @param personaEntidad persona o entidad relacionada
     * @return ID del movimiento registrado
     */
    public String registrarMovimiento(String inmuebleId,
                                      MovimientoBancario.TipoMovimiento tipo,
                                      LocalDate fecha, double importe, String personaEntidad) {
        String id = repo.generarIdMovimiento();
        MovimientoBancario m = new MovimientoBancario(
                id, tipo, inmuebleId, fecha, importe, personaEntidad);
        repo.agregarMovimiento(m);
        return id;
    }

    /**
     * Consulta movimientos bancarios de un inmueble en un período.
     * @param inmuebleId ID del inmueble
     * @param desde      fecha de inicio del período
     * @param hasta      fecha de fin del período
     * @return lista de movimientos del inmueble en el período
     */
    public List<MovimientoBancario> consultarMovimientosPorPeriodo(
            String inmuebleId, LocalDate desde, LocalDate hasta) {
        return repo.getMovimientosPorInmuebleYPeriodo(inmuebleId, desde, hasta);
    }

    /**
     * Obtiene todos los movimientos bancarios registrados.
     * @return lista completa de movimientos
     */
    public List<MovimientoBancario> getTodosMovimientos() {
        return repo.getTodosMovimientos();
    }
}
