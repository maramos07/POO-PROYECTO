package com.inmobiliaria.vista;

import com.inmobiliaria.modelo.Inmueble;
import com.inmobiliaria.modelo.MovimientoBancario;
import com.inmobiliaria.servicio.InmuebleServicio;
import com.inmobiliaria.util.Validador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel para registrar y consultar movimientos bancarios por inmueble y período.
 */
public class PanelMovimientos extends JPanel {

    private final InmuebleServicio servicio;
    private DefaultTableModel modelo;
    private JTable tabla;
    private JTextField txtFiltroInm, txtDesde, txtHasta;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_VISTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] COLS = {
            "ID", "Tipo", "Inmueble", "Fecha", "Importe ($)", "Persona / Entidad"
    };

    public PanelMovimientos(InmuebleServicio servicio) {
        this.servicio = servicio;
        setLayout(new BorderLayout(8, 8));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(titulo(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearControles(), BorderLayout.SOUTH);

        cargar(servicio.getTodosMovimientos());
    }

    private JLabel titulo() {
        JLabel l = new JLabel(" Movimientos Bancarios por Inmueble");
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

        // Colorear por tipo: ingresos = verde, gastos = rojo
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t,val,sel,foc,row,col);
                if (!sel) {
                    String tipo = (String) t.getValueAt(row, 1);
                    c.setBackground(tipo != null && tipo.startsWith("Ingreso")
                            ? new Color(240, 236, 225)   // beige dorado — ingreso
                            : new Color(220, 228, 242)); // azul suave — gasto
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(new Color(201, 169, 110)));
        return sp;
    }

    private JPanel crearControles() {
        JPanel p = new JPanel(new BorderLayout(8, 4));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filtros.setBackground(VentanaPrincipal.COLOR_FONDO);

        txtFiltroInm = SwingUtil.crearTextField(12); txtDesde = SwingUtil.crearTextField(10); txtHasta = SwingUtil.crearTextField(10);

        JButton btnFiltrar = SwingUtil.crearBoton(" Filtrar por Inmueble y Período",
                VentanaPrincipal.COLOR_SECUNDARIO);
        JButton btnTodos   = SwingUtil.crearBoton("Ver Todos", new Color(100, 116, 139));

        btnFiltrar.addActionListener(e -> filtrar());
        btnTodos.addActionListener(e -> cargar(servicio.getTodosMovimientos()));

        filtros.add(SwingUtil.crearLabel("Inmueble (ID o dirección):")); filtros.add(txtFiltroInm);
        filtros.add(SwingUtil.crearLabel("Desde (dd/MM/yyyy):")); filtros.add(txtDesde);
        filtros.add(SwingUtil.crearLabel("Hasta (dd/MM/yyyy):")); filtros.add(txtHasta);
        filtros.add(btnFiltrar); filtros.add(btnTodos);

        // Botones
        JPanel bots = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bots.setBackground(VentanaPrincipal.COLOR_FONDO);
        JButton btnNuevo = SwingUtil.crearBoton("＋ Registrar Movimiento", VentanaPrincipal.COLOR_ACENTO);
        btnNuevo.addActionListener(e -> registrar());
        bots.add(btnNuevo);

        // Leyenda
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        leyenda.setBackground(VentanaPrincipal.COLOR_FONDO);
        leyenda.add(badge("● Ingreso", VentanaPrincipal.COLOR_ACENTO));
        leyenda.add(badge("● Gasto",   VentanaPrincipal.COLOR_SECUNDARIO));

        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(VentanaPrincipal.COLOR_FONDO);
        sur.add(bots, BorderLayout.WEST);
        sur.add(leyenda, BorderLayout.EAST);

        p.add(filtros, BorderLayout.NORTH);
        p.add(sur,     BorderLayout.SOUTH);
        return p;
    }

    private void filtrar() {
        String texto = txtFiltroInm.getText().trim();
        if (texto.isEmpty()) { cargar(servicio.getTodosMovimientos()); return; }
        try {
            LocalDate d = txtDesde.getText().trim().isEmpty()
                    ? LocalDate.of(2000, 1, 1)
                    : LocalDate.parse(txtDesde.getText().trim(), FMT_VISTA);
            LocalDate h = txtHasta.getText().trim().isEmpty()
                    ? LocalDate.now()
                    : LocalDate.parse(txtHasta.getText().trim(), FMT_VISTA);

            List<MovimientoBancario> resultado = new ArrayList<>();
            List<Inmueble> todos = servicio.getTodosInmuebles();
            for (Inmueble inm : todos) {
                if (inm.getId().toLowerCase().contains(texto.toLowerCase())
                        || inm.getDireccion().toLowerCase().contains(texto.toLowerCase())) {
                    resultado.addAll(servicio.consultarMovimientosPorPeriodo(inm.getId(), d, h));
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
            JOptionPane.showMessageDialog(this, "No hay inmuebles registrados.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opInm = inmuebles.stream()
                .map(i -> i.getId() + " — " + i.getDireccion() + " " + i.getNumero())
                .toArray(String[]::new);

        JComboBox<String> cmbInm  = new JComboBox<>(opInm);
        JComboBox<MovimientoBancario.TipoMovimiento> cmbTipo =
                new JComboBox<>(MovimientoBancario.TipoMovimiento.values());
        JTextField tfFecha   = SwingUtil.crearTextField(12); tfFecha.setText(LocalDate.now().format(FMT_VISTA));
        JTextField tfImporte = SwingUtil.crearTextField(12);
        JTextField tfEntidad = SwingUtil.crearTextField(22);

        cmbInm.setFont(VentanaPrincipal.FUENTE_NORMAL);
        cmbTipo.setFont(VentanaPrincipal.FUENTE_NORMAL);

        Object[] form = {
                "Inmueble:", cmbInm,
                "Tipo de movimiento:", cmbTipo,
                "Fecha (dd/MM/yyyy):", tfFecha,
                "Importe ($):", tfImporte,
                "Persona / Entidad (acreedor o deudor):", tfEntidad
        };

        int res = JOptionPane.showConfirmDialog(this, form,
                "Registrar Movimiento Bancario", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            String idInm  = inmuebles.get(cmbInm.getSelectedIndex()).getId();
            MovimientoBancario.TipoMovimiento tipo =
                    (MovimientoBancario.TipoMovimiento) cmbTipo.getSelectedItem();
            LocalDate fecha   = LocalDate.parse(tfFecha.getText().trim(), FMT_VISTA);
            double   importe  = Double.parseDouble(tfImporte.getText().trim());
            Validador.validarPositivo(importe, "Importe");
            String   entidad  = tfEntidad.getText().trim();
            if (entidad.isBlank()) throw new IllegalArgumentException("La persona o entidad es obligatoria.");

            String id = servicio.registrarMovimiento(idInm, tipo, fecha, importe, entidad);
            JOptionPane.showMessageDialog(this,
                    "  Movimiento registrado con ID: " + id,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargar(servicio.getTodosMovimientos());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El importe debe ser un número válido y mayor a cero.",
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

    private void cargar(List<MovimientoBancario> lista) {
        modelo.setRowCount(0);
        for (MovimientoBancario m : lista) {
            modelo.addRow(new Object[]{
                    m.getId(), m.getTipoMovimiento().getDescripcion(),
                    m.getInmuebleId(), m.getFecha().format(FMT_VISTA),
                    String.format("%.2f", m.getImporte()),
                    m.getPersonaEntidad()
            });
        }
    }

    private JLabel badge(String txt, Color color) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Helvetica", Font.BOLD, 11));
        l.setForeground(color);
        return l;
    }

}

