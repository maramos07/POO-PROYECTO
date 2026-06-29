package com.inmobiliaria.vista;

import com.inmobiliaria.modelo.Factura;
import com.inmobiliaria.modelo.Inmueble;
import com.inmobiliaria.servicio.InmuebleServicio;
import com.inmobiliaria.util.Validador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para registrar y consultar facturas de gastos asociadas a inmuebles.
 */
public class PanelFacturas extends JPanel {

    private final InmuebleServicio servicio;
    private DefaultTableModel modelo;
    private JTable tabla;
    private JTextField txtFiltroInm, txtDesde, txtHasta;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_VISTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] COLS = {
            "ID", "Fecha", "Inmueble", "Concepto", "Proveedor", "Costo ($)"
    };

    public PanelFacturas(InmuebleServicio servicio) {
        this.servicio = servicio;
        setLayout(new BorderLayout(8, 8));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(titulo(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearControles(), BorderLayout.SOUTH);

        cargar(servicio.getTodasFacturas());
    }

    private JLabel titulo() {
        JLabel l = new JLabel("Facturas y Gastos de Inmuebles");
        l.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        l.setForeground(VentanaPrincipal.COLOR_PRIMARIO);
        l.setBorder(new EmptyBorder(0, 0, 8, 0));
        return l;
    }

    private JScrollPane crearTabla() {
        modelo = new DefaultTableModel(COLS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setFont(VentanaPrincipal.FUENTE_NORMAL);
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        tabla.getTableHeader().setBackground(VentanaPrincipal.COLOR_PRIMARIO);
        tabla.getTableHeader().setForeground(VentanaPrincipal.COLOR_ACENTO);
        tabla.setSelectionBackground(new Color(201, 169, 110, 80));
        tabla.setGridColor(new Color(220, 215, 205));
        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(new Color(201, 169, 110)));
        return sp;
    }

    private JPanel crearControles() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtros.setBackground(VentanaPrincipal.COLOR_FONDO);

        txtFiltroInm = SwingUtil.crearTextField(12); txtDesde = SwingUtil.crearTextField(10); txtHasta = SwingUtil.crearTextField(10);

        JButton btnFiltrar = SwingUtil.crearBoton(" Filtrar", VentanaPrincipal.COLOR_SECUNDARIO);
        JButton btnTodas   = SwingUtil.crearBoton("Ver Todas",  new Color(100, 116, 139));

        btnFiltrar.addActionListener(e -> filtrar());
        btnTodas.addActionListener(e -> cargar(servicio.getTodasFacturas()));

        filtros.add(SwingUtil.crearLabel("Inmueble (ID o dirección):")); filtros.add(txtFiltroInm);
        filtros.add(SwingUtil.crearLabel("Desde (dd/MM/yyyy):")); filtros.add(txtDesde);
        filtros.add(SwingUtil.crearLabel("Hasta (dd/MM/yyyy):")); filtros.add(txtHasta);
        filtros.add(btnFiltrar); filtros.add(btnTodas);

        // Botones
        JPanel bots = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bots.setBackground(VentanaPrincipal.COLOR_FONDO);
        JButton btnNueva = SwingUtil.crearBoton("＋ Registrar Factura", VentanaPrincipal.COLOR_ACENTO);
        btnNueva.addActionListener(e -> registrar());
        bots.add(btnNueva);

        p.add(filtros, BorderLayout.NORTH);
        p.add(bots, BorderLayout.SOUTH);
        return p;
    }

    private void filtrar() {
        String texto = txtFiltroInm.getText().trim();
        String desde = txtDesde.getText().trim();
        String hasta = txtHasta.getText().trim();

        if (texto.isEmpty()) { cargar(servicio.getTodasFacturas()); return; }

        try {
            LocalDate d = desde.isEmpty() ? LocalDate.of(2000, 1, 1) : LocalDate.parse(desde, FMT_VISTA);
            LocalDate h = hasta.isEmpty() ? LocalDate.now() : LocalDate.parse(hasta, FMT_VISTA);

            List<Factura> resultado = new ArrayList<>();
            List<Inmueble> todos = servicio.getTodosInmuebles();
            for (Inmueble inm : todos) {
                if (inm.getId().toLowerCase().contains(texto.toLowerCase())
                        || inm.getDireccion().toLowerCase().contains(texto.toLowerCase())) {
                    resultado.addAll(servicio.consultarFacturasPorPeriodo(inm.getId(), d, h));
                }
            }
            cargar(resultado);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Use dd/MM/yyyy (ej: 15/01/2024)",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrar() {
        List<Inmueble> inmuebles = servicio.getTodosInmuebles();
        if (inmuebles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay inmuebles registrados.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opInm = inmuebles.stream()
                .map(i -> i.getId() + " — " + i.getDireccion() + " " + i.getNumero())
                .toArray(String[]::new);

        JComboBox<String> cmbInm   = new JComboBox<>(opInm);
        JComboBox<Factura.ConceptoFactura> cmbConc =
                new JComboBox<>(Factura.ConceptoFactura.values());
        JTextField tfFecha    = SwingUtil.crearTextField(12); tfFecha.setText(LocalDate.now().format(FMT_VISTA));
        JTextField tfProv     = SwingUtil.crearTextField(20);
        JTextField tfCosto    = SwingUtil.crearTextField(12);

        cmbInm.setFont(VentanaPrincipal.FUENTE_NORMAL);
        cmbConc.setFont(VentanaPrincipal.FUENTE_NORMAL);

        Object[] form = {
                "Inmueble:", cmbInm,
                "Fecha (dd/MM/yyyy):", tfFecha,
                "Concepto:", cmbConc,
                "Proveedor / Compañía:", tfProv,
                "Costo ($):", tfCosto
        };

        int res = JOptionPane.showConfirmDialog(this, form,
                "Registrar Factura", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            String idInm   = inmuebles.get(cmbInm.getSelectedIndex()).getId();
            LocalDate fecha = LocalDate.parse(tfFecha.getText().trim(), FMT_VISTA);
            Factura.ConceptoFactura conc = (Factura.ConceptoFactura) cmbConc.getSelectedItem();
            String prov    = tfProv.getText().trim();
            if (prov.isBlank()) throw new IllegalArgumentException("El proveedor es obligatorio.");
            double costo   = Double.parseDouble(tfCosto.getText().trim());
            Validador.validarPositivo(costo, "Costo");

            String id = servicio.registrarFactura(idInm, fecha, conc, prov, costo);
            JOptionPane.showMessageDialog(this,
                    " Factura registrada con ID: " + id,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargar(servicio.getTodasFacturas());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El costo debe ser un número válido y mayor a cero.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Use dd/MM/yyyy (ej: 15/01/2024).",
                    "Error de fecha", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargar(List<Factura> lista) {
        modelo.setRowCount(0);
        for (Factura f : lista) {
            modelo.addRow(new Object[]{
                    f.getId(), f.getFechaEmision().format(FMT_VISTA), f.getInmuebleId(),
                    f.getConcepto().getDescripcion(), f.getProveedor(),
                    String.format("%.2f", f.getCosto())
            });
        }
    }

}
