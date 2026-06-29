package com.inmobiliaria.vista;

import com.inmobiliaria.modelo.*;
import com.inmobiliaria.servicio.InmuebleServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel para CRUD completo de inmuebles (Edificios, Pisos, Locales).
 * @author Equipo POO
 */
public class PanelInmuebles extends JPanel {

    private final InmuebleServicio servicio;

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JLabel lblResultado;

    private static final String[] COLUMNAS = {
            "ID", "Tipo", "Dirección", "Cód. Calle", "Descripción",
            "Cód. Postal", "Precio Alquiler", "Estado"
    };

    public PanelInmuebles(InmuebleServicio servicio) {
        this.servicio = servicio;
        setLayout(new BorderLayout(8, 8));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(crearPanelBusqueda(), BorderLayout.NORTH);
        add(crearPanelTabla(),    BorderLayout.CENTER);
        add(crearPanelBotones(),  BorderLayout.SOUTH);

        cargarTabla(servicio.getTodosInmuebles());
    }

    // ── Búsqueda ──────────────────────────────────────────────────────────────

    private JPanel crearPanelBusqueda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);

        JLabel lbl = new JLabel(" Buscar por dirección:");
        lbl.setFont(VentanaPrincipal.FUENTE_NORMAL);

        txtBuscar = new JTextField(25);
        txtBuscar.setFont(VentanaPrincipal.FUENTE_NORMAL);

        JButton btnBuscar = SwingUtil.crearBoton("Buscar", VentanaPrincipal.COLOR_SECUNDARIO);
        btnBuscar.addActionListener(e -> buscar());

        JButton btnTodos = SwingUtil.crearBoton("Ver Todos", new Color(100, 116, 139));
        btnTodos.addActionListener(e -> cargarTabla(servicio.getTodosInmuebles()));

        lblResultado = new JLabel("");
        lblResultado.setFont(new Font("Helvetica", Font.ITALIC, 12));
        lblResultado.setForeground(VentanaPrincipal.COLOR_PRIMARIO);

        p.add(lbl); p.add(txtBuscar);
        p.add(btnBuscar); p.add(btnTodos); p.add(lblResultado);
        return p;
    }

    private void buscar() {
        String dir = txtBuscar.getText().trim();
        if (dir.isEmpty()) { cargarTabla(servicio.getTodosInmuebles()); return; }
        List<Inmueble> resultado = servicio.consultarPorDireccion(dir);
        cargarTabla(resultado);
        lblResultado.setText(resultado.size() + " resultado(s) para \"" + dir + "\"");
    }

    // ── Tabla ─────────────────────────────────────────────────────────────────

    private JScrollPane crearPanelTabla() {
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(VentanaPrincipal.FUENTE_NORMAL);
        tabla.setRowHeight(28);
        tabla.getTableHeader().setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        tabla.getTableHeader().setBackground(VentanaPrincipal.COLOR_PRIMARIO);
        tabla.getTableHeader().setForeground(VentanaPrincipal.COLOR_ACENTO);
        tabla.setSelectionBackground(new Color(201, 169, 110, 80));
        tabla.setGridColor(new Color(220, 215, 205));
        tabla.setShowGrid(true);

        // Colorear filas por estado
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    String estado = (String) t.getValueAt(row, 7);
                    c.setBackground("DISPONIBLE".equals(estado)
                            ? new Color(240, 236, 225)   // beige cálido — disponible
                            : new Color(220, 228, 242));  // azul muy suave — ocupado
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(new Color(201, 169, 110)));
        return sp;
    }

    private void cargarTabla(List<Inmueble> lista) {
        modeloTabla.setRowCount(0);
        for (Inmueble inm : lista) {
            modeloTabla.addRow(new Object[]{
                    inm.getId(), inm.getTipoInmueble(), inm.getDireccion(),
                    inm.getNumero(), inm.getDescripcion(),
                    inm.getCodigoPostal(),
                    String.format("$%.2f", inm.getPrecioAlquiler()),
                    inm.isDisponible() ? "DISPONIBLE" : "OCUPADO"
            });
        }
        if (lista.isEmpty()) lblResultado.setText("No se encontraron inmuebles.");
        else lblResultado.setText(lista.size() + " inmueble(s).");
    }

    // ── Botones de acción ─────────────────────────────────────────────────────

    private JPanel crearPanelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);

        JButton btnNuevo   = SwingUtil.crearBoton("＋ Nuevo Inmueble", VentanaPrincipal.COLOR_ACENTO);
        JButton btnEditar  = SwingUtil.crearBoton("✏ Editar",          VentanaPrincipal.COLOR_SECUNDARIO);
        JButton btnElim    = SwingUtil.crearBoton("🗑 Eliminar",        VentanaPrincipal.COLOR_ERROR);

        btnNuevo.addActionListener(e -> abrirFormularioNuevo());
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnElim.addActionListener(e -> eliminarSeleccionado());

        p.add(btnNuevo); p.add(btnEditar); p.add(btnElim);
        return p;
    }

    // ── Diálogos ──────────────────────────────────────────────────────────────

    private void abrirFormularioNuevo() {
        DialogoInmueble dlg = new DialogoInmueble(
                (JFrame) SwingUtilities.getWindowAncestor(this), servicio, null);
        dlg.setVisible(true);
        if (dlg.isGuardado()) cargarTabla(servicio.getTodosInmuebles());
    }

    private void editarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { SwingUtil.mostrarAviso(this, "Seleccione un inmueble para editar."); return; }
        String id = (String) modeloTabla.getValueAt(fila, 0);
        Inmueble inm = servicio.buscarPorId(id);
        DialogoInmueble dlg = new DialogoInmueble(
                (JFrame) SwingUtilities.getWindowAncestor(this), servicio, inm);
        dlg.setVisible(true);
        if (dlg.isGuardado()) cargarTabla(servicio.getTodosInmuebles());
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { SwingUtil.mostrarAviso(this, "Seleccione un inmueble para eliminar."); return; }
        String id = (String) modeloTabla.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Se eliminarán también todas sus facturas, movimientos y alquileres asociados. ¿Desea continuar?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = servicio.eliminarInmueble(id);
            if (ok) {
                cargarTabla(servicio.getTodosInmuebles());
                JOptionPane.showMessageDialog(this,
                        "Inmueble eliminado correctamente.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "NO se puede eliminar: el inmueble está ocupado. Desalquile primero.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

