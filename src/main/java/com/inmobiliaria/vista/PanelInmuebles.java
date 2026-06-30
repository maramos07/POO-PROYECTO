package com.inmobiliaria.vista;

import com.inmobiliaria.modelo.*;
import com.inmobiliaria.servicio.InmuebleServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
            "ID", "Tipo", "Dirección", "Descripción",
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
                    String estado = (String) t.getValueAt(row, 6);
                    c.setBackground("DISPONIBLE".equals(estado)
                            ? new Color(240, 236, 225)   // beige cálido — disponible
                            : new Color(220, 228, 242));  // azul muy suave — ocupado
                }
                return c;
            }
        });

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(BorderFactory.createLineBorder(new Color(201, 169, 110)));

        // Doble clic sobre una fila: abre el detalle completo del inmueble
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    verDetalleSeleccionado();
                }
            }
        });

        return sp;
    }

    private void cargarTabla(List<Inmueble> lista) {
        modeloTabla.setRowCount(0);
        for (Inmueble inm : lista) {
            modeloTabla.addRow(new Object[]{
                    inm.getId(), inm.getTipoInmueble(), inm.getDireccion(),
                    inm.getDescripcion(),
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
        JButton btnVer     = SwingUtil.crearBoton("👁 Ver Detalle",     VentanaPrincipal.COLOR_PRIMARIO);
        JButton btnEditar  = SwingUtil.crearBoton("✏ Editar",          VentanaPrincipal.COLOR_SECUNDARIO);
        JButton btnElim    = SwingUtil.crearBoton("🗑 Eliminar",        VentanaPrincipal.COLOR_ERROR);

        btnNuevo.addActionListener(e -> abrirFormularioNuevo());
        btnVer.addActionListener(e -> verDetalleSeleccionado());
        btnEditar.addActionListener(e -> editarSeleccionado());
        btnElim.addActionListener(e -> eliminarSeleccionado());

        p.add(btnNuevo); p.add(btnVer); p.add(btnEditar); p.add(btnElim);

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

    private void verDetalleSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { SwingUtil.mostrarAviso(this, "Seleccione un inmueble para ver su detalle."); return; }
        String id = (String) modeloTabla.getValueAt(fila, 0);
        Inmueble inm = servicio.buscarPorId(id);
        if (inm == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Dirección: ").append(inm.getDireccion()).append("\n");
        sb.append("Descripción: ").append(inm.getDescripcion()).append("\n");
        sb.append("Código Postal: ").append(inm.getCodigoPostal()).append("\n");
        sb.append(String.format("Precio de Alquiler: $%.2f%n", inm.getPrecioAlquiler()));
        sb.append("Estado: ").append(inm.isDisponible() ? "DISPONIBLE" : "OCUPADO").append("\n");

        if (!inm.isDisponible() && inm.getInquilinoId() != null) {
            Inquilino inq = servicio.buscarInquilinoPorId(inm.getInquilinoId());
            sb.append("Inquilino actual: ")
                    .append(inq != null ? inq.getNombre() + " (" + inq.getId() + ")" : inm.getInquilinoId())
                    .append("\n");
        }

        if (inm instanceof Edificio e) {
            sb.append("Nombre del edificio: ").append(e.getNombreEdificio()).append("\n");
            sb.append("Número total de pisos: ").append(e.getNumeroPisos()).append("\n");
        } else if (inm instanceof Piso p) {
            sb.append("Número de piso: ").append(p.getNumeroPiso()).append("\n");
            sb.append("Tipo de espacio: ").append(p.getTipoEspacio()).append("\n");
            sb.append("Descripción específica: ").append(p.getDescripcionEspecifica()).append("\n");
            sb.append("Edificio al que pertenece: ").append(textoEdificio(p.getEdificioId())).append("\n");
        } else if (inm instanceof Local l) {
            sb.append("Número de piso: ").append(l.getNumeroPiso()).append("\n");
            sb.append("Tipo de local: ").append(l.getTipoLocal()).append("\n");
            sb.append("Descripción específica: ").append(l.getDescripcionEspecifica()).append("\n");
            sb.append("Edificio al que pertenece: ").append(textoEdificio(l.getEdificioId())).append("\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(VentanaPrincipal.FUENTE_NORMAL);
        area.setBackground(VentanaPrincipal.COLOR_FONDO);

        JOptionPane.showMessageDialog(this, area,
                "Detalle — " + inm.getTipoInmueble() + " " + inm.getId(),
                JOptionPane.PLAIN_MESSAGE);
    }

    private String textoEdificio(String edificioId) {
        if (edificioId == null || edificioId.isEmpty()) return "— (independiente)";
        Inmueble edif = servicio.buscarPorId(edificioId);
        return (edif instanceof Edificio e) ? e.getNombreEdificio() + " (" + e.getId() + ")" : edificioId;
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

