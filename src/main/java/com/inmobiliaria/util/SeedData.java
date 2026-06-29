package com.inmobiliaria.util;

import com.inmobiliaria.modelo.Factura;
import com.inmobiliaria.modelo.Inquilino;
import com.inmobiliaria.modelo.MovimientoBancario;
import com.inmobiliaria.servicio.InmuebleServicio;

import java.time.LocalDate;

/**
 * Carga datos de prueba en el sistema si el repositorio está vacío.
 * Útil para demostraciones y pruebas manuales.
 *
 * @author Equipo POO
 */
public class SeedData {

    /**
     * Inserta datos de ejemplo si no hay inmuebles registrados.
     * @param servicio instancia del servicio de negocio
     */
    public static void cargarSiVacio(InmuebleServicio servicio) {
        if (!servicio.getTodosInmuebles().isEmpty()) return;

        // Edificios
        String edif1 = servicio.registrarEdificio(
                "Calle Mayor", "1", "Edificio residencial céntrico",
                "28001", 0, 10, "Torre Azul");
        String edif2 = servicio.registrarEdificio(
                "Avenida Comercio", "45", "Centro comercial con locales",
                "28015", 0, 3, "Centro Comercial Norte");

        // Pisos
        servicio.registrarPiso(
                "Calle Mayor", "1", "Apartamento luminoso 2 habitaciones",
                "28001", 850, 3, "Apartamento", "Dos habitaciones, salón, cocina equipada", edif1);
        servicio.registrarPiso(
                "Calle Mayor", "1", "Ático con terraza y vistas",
                "28001", 1200, 5, "Ático", "Terraza de 30m2, dos habitaciones, aire acondicionado", edif1);
        servicio.registrarPiso(
                "Calle Luna", "22", "Dúplex reformado",
                "28004", 950, 1, "Duplex", "Planta baja y sótano, jardín privado", null);
        servicio.registrarPiso(
                "Calle del Sol", "10", "Estudio económico",
                "28012", 550, 2, "Estudio", "Un ambiente, baño completo, amueblado", null);

        // Locales
        servicio.registrarLocal(
                "Avenida Comercio", "45", "Local comercial en planta baja",
                "28015", 2000, 0, "Local Comercial", "Escaparate de 8m, baño, almacén", edif2);
        servicio.registrarLocal(
                "Calle Serrano", "88", "Oficina diáfana",
                "28006", 1500, 3, "Oficina", "Planta completa, 4 despachos, sala de reuniones", null);

        // Inquilinos
        servicio.registrarInquilino(
                "Juan", "12345678", 32, "Masculino", "612345678",
                Inquilino.TipoRespaldo.NOMINA);
        servicio.registrarInquilino(
                "María", "87654321", 28, "Femenino", "maria.garcia@email.com",
                Inquilino.TipoRespaldo.CONTRATO_TRABAJO);
        servicio.registrarInquilino(
                "Carlos", "11223344", 45, "Masculino", "699887766",
                Inquilino.TipoRespaldo.AVAL_BANCARIO);
        servicio.registrarInquilino(
                "Laura", "55667788", 24, "Femenino", "laura.m@email.com",
                Inquilino.TipoRespaldo.AVAL_PERSONA);

        // Alquilar algunos inmuebles (los IDs dependen del orden de registro)
        // INM-00003: Piso en Calle Mayor 1, Apartamento  -> Juan
        // INM-00005: Piso en Calle Luna 22, Duplex        -> María
        // La numeración depende de cuántos inmuebles se crearon (3 edificios + 4 pisos + 2 locales = 9 inmuebles en total)
        // Pero cada registro genera un solo ID, y los IDs son globales.
        // Busquemos por dirección en vez de asumir IDs.
        var inmuebles = servicio.getTodosInmuebles();
        var inquilinos = servicio.getTodosInquilinos();

        // Juan alquila el apartamento en Calle Mayor 1 (primer piso registrado)
        String idPiso1 = inmuebles.stream()
                .filter(i -> i.getDireccion().equals("Calle Mayor")
                        && i.getNumero().equals("1")
                        && i.getTipoInmueble().equals("PISO"))
                .findFirst().orElseThrow().getId();
        String idJuan = inquilinos.stream()
                .filter(i -> i.getCedula().equals("12345678"))
                .findFirst().orElseThrow().getId();
        servicio.alquilarInmueble(idPiso1, idJuan);

        // María alquila el dúplex en Calle Luna 22
        String idDuplex = inmuebles.stream()
                .filter(i -> i.getDireccion().equals("Calle Luna")
                        && i.getNumero().equals("22"))
                .findFirst().orElseThrow().getId();
        String idMaria = inquilinos.stream()
                .filter(i -> i.getCedula().equals("87654321"))
                .findFirst().orElseThrow().getId();
        servicio.alquilarInmueble(idDuplex, idMaria);

        // Facturas
        servicio.registrarFactura(idPiso1, LocalDate.of(2024, 1, 10),
                Factura.ConceptoFactura.AGUA, "Canal de Isabel II", 45.50);
        servicio.registrarFactura(idPiso1, LocalDate.of(2024, 1, 15),
                Factura.ConceptoFactura.ELECTRICIDAD, "Iberdrola", 78.30);
        servicio.registrarFactura(idPiso1, LocalDate.of(2024, 2, 5),
                Factura.ConceptoFactura.GAS, "Naturgy", 62.00);
        servicio.registrarFactura(idPiso1, LocalDate.of(2024, 3, 20),
                Factura.ConceptoFactura.REPARACION, "Fontanería Pérez", 120.00);
        servicio.registrarFactura(idDuplex, LocalDate.of(2024, 2, 12),
                Factura.ConceptoFactura.REFORMA, "Construcciones Luna", 3500.00);

        // Movimiento adicional
        var inmuebleParaMov = inmuebles.stream()
                .filter(i -> i.getDireccion().equals("Calle del Sol")
                        && i.getNumero().equals("10"))
                .findFirst().orElseThrow().getId();
        servicio.registrarMovimiento(inmuebleParaMov,
                MovimientoBancario.TipoMovimiento.GASTO_SERVICIO,
                LocalDate.of(2024, 3, 1), 95.00, "Comunidad de vecinos");

        System.out.println("Datos de prueba cargados: "
                + servicio.getTodosInmuebles().size() + " inmuebles, "
                + servicio.getTodosInquilinos().size() + " inquilinos, "
                + servicio.getTodosAlquileres().size() + " alquileres, "
                + servicio.getTodasFacturas().size() + " facturas, "
                + servicio.getTodosMovimientos().size() + " movimientos.");
    }
}
