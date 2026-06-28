package com.inmobiliaria.vista;

import com.inmobiliaria.modelo.Inmueble;
import com.inmobiliaria.modelo.MovimientoBancario;
import com.inmobiliaria.servicio.InmuebleServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

        txtFiltroInm = tf(12); txtDesde = tf(10); txtHasta = tf(10);

        JButton btnFiltrar = btn(" Filtrar por Inmueble y Período",
                VentanaPrincipal.COLOR_SECUNDARIO);
        JButton btnTodos   = btn("Ver Todos", new Color(100, 116, 139));

        btnFiltrar.addActionListener(e -> filtrar());
        btnTodos.addActionListener(e -> cargar(servicio.getTodosMovimientos()));

        filtros.add(lbl("ID Inmueble:")); filtros.add(txtFiltroInm);
        filtros.add(lbl("Desde (yyyy-MM-dd):")); filtros.add(txtDesde);
        filtros.add(lbl("Hasta:")); filtros.add(txtHasta);
        filtros.add(btnFiltrar); filtros.add(btnTodos);

        // Botones
        JPanel bots = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bots.setBackground(VentanaPrincipal.COLOR_FONDO);
        JButton btnNuevo = btn("＋ Registrar Movimiento", VentanaPrincipal.COLOR_ACENTO);
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
        String idInm = txtFiltroInm.getText().trim();
        if (idInm.isEmpty()) { cargar(servicio.getTodosMovimientos()); return; }
        try {
            LocalDate d = txtDesde.getText().trim().isEmpty()
                    ? LocalDate.of(2000, 1, 1)
                    : LocalDate.parse(txtDesde.getText().trim(), FMT);
            LocalDate h = txtHasta.getText().trim().isEmpty()
                    ? LocalDate.now()
                    : LocalDate.parse(txtHasta.getText().trim(), FMT);
            cargar(servicio.consultarMovimientosPorPeriodo(idInm, d, h));
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha inválido. Use yyyy-MM-dd",
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
        JTextField tfFecha   = tf(12); tfFecha.setText(LocalDate.now().format(FMT));
        JTextField tfImporte = tf(12);
        JTextField tfEntidad = tf(22);

        cmbInm.setFont(VentanaPrincipal.FUENTE_NORMAL);
        cmbTipo.setFont(VentanaPrincipal.FUENTE_NORMAL);

        Object[] form = {
                "Inmueble:", cmbInm,
                "Tipo de movimiento:", cmbTipo,
                "Fecha (yyyy-MM-dd):", tfFecha,
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
            LocalDate fecha   = LocalDate.parse(tfFecha.getText().trim(), FMT);
            double   importe  = Double.parseDouble(tfImporte.getText().trim());
            String   entidad  = tfEntidad.getText().trim();

            String id = servicio.registrarMovimiento(idInm, tipo, fecha, importe, entidad);
            JOptionPane.showMessageDialog(this,
                    "  Movimiento registrado con ID: " + id,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargar(servicio.getTodosMovimientos());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al registrar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargar(List<MovimientoBancario> lista) {
        modelo.setRowCount(0);
        for (MovimientoBancario m : lista) {
            modelo.addRow(new Object[]{
                    m.getId(), m.getTipoMovimiento().getDescripcion(),
                    m.getInmuebleId(), m.getFecha(),
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

    private JTextField tf(int cols) {
        JTextField t = new JTextField(cols);
        t.setFont(VentanaPrincipal.FUENTE_NORMAL);
        return t;
    }

    private JLabel lbl(String txt) {
        JLabel l = new JLabel(txt);
        l.setFont(VentanaPrincipal.FUENTE_NORMAL);
        return l;
    }

    private JButton btn(String txt, Color c) {
        JButton b = new JButton(txt);
        b.setFont(VentanaPrincipal.FUENTE_NORMAL);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(7, 14, 7, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}

