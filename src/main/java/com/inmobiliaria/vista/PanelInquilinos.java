package com.inmobiliaria.vista;

import com.inmobiliaria.modelo.Inquilino;
import com.inmobiliaria.servicio.InmuebleServicio;
import com.inmobiliaria.util.Validador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel para registrar, consultar y eliminar inquilinos.
 */
public class PanelInquilinos extends JPanel {

    private final InmuebleServicio servicio;
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtBuscar;

    private static final String[] COLS = {
            "ID", "Nombre", "Cédula", "Edad", "Sexo", "Contacto", "Respaldo"
    };

    public PanelInquilinos(InmuebleServicio servicio) {
        this.servicio = servicio;
        setLayout(new BorderLayout(8, 8));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(crearPanelBusqueda(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(botones(), BorderLayout.SOUTH);

        actualizar();
    }

    private JPanel crearPanelBusqueda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);

        JLabel lbl = new JLabel(" Buscar por nombre o cédula:");
        lbl.setFont(VentanaPrincipal.FUENTE_NORMAL);

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(VentanaPrincipal.FUENTE_NORMAL);

        JButton btnBuscar = SwingUtil.crearBoton("Buscar", VentanaPrincipal.COLOR_SECUNDARIO);
        btnBuscar.addActionListener(e -> buscar());

        JButton btnTodos = SwingUtil.crearBoton("Ver Todos", new Color(100, 116, 139));
        btnTodos.addActionListener(e -> actualizar());

        p.add(lbl); p.add(txtBuscar); p.add(btnBuscar); p.add(btnTodos);
        return p;
    }

    private void buscar() {
        String busq = txtBuscar.getText().trim().toLowerCase();
        modelo.setRowCount(0);
        for (Inquilino inq : servicio.getTodosInquilinos()) {
            if (inq.getNombre().toLowerCase().contains(busq)
                    || inq.getCedula().contains(busq)) {
                modelo.addRow(new Object[]{
                        inq.getId(), inq.getNombre(), inq.getCedula(),
                        inq.getEdad(), inq.getSexo(),
                        inq.getMedioContacto(), inq.getTipoRespaldo().getDescripcion()
                });
            }
        }
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

    private JPanel botones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);

        JButton btnNuevo = SwingUtil.crearBoton("＋ Registrar Inquilino", VentanaPrincipal.COLOR_ACENTO);
        JButton btnElim  = SwingUtil.crearBoton("🗑 Eliminar",             VentanaPrincipal.COLOR_ERROR);

        btnNuevo.addActionListener(e -> abrirFormulario());
        btnElim.addActionListener(e -> eliminar());

        p.add(btnNuevo); p.add(btnElim);
        return p;
    }

    private void actualizar() {
        modelo.setRowCount(0);
        for (Inquilino inq : servicio.getTodosInquilinos()) {
            modelo.addRow(new Object[]{
                    inq.getId(), inq.getNombre(), inq.getCedula(),
                    inq.getEdad(), inq.getSexo(),
                    inq.getMedioContacto(), inq.getTipoRespaldo().getDescripcion()
            });
        }
    }

    private void abrirFormulario() {
        // Campos del formulario
        JTextField fNombre   = SwingUtil.crearTextField(20); JTextField fCedula  = SwingUtil.crearTextField(20);
        JTextField fEdad     = SwingUtil.crearTextField(20);
        JTextField fContacto = SwingUtil.crearTextField(20);
        JComboBox<Inquilino.Sexo> cSexo = new JComboBox<>(Inquilino.Sexo.values());
        JComboBox<Inquilino.TipoRespaldo> cRespaldo =
                new JComboBox<>(Inquilino.TipoRespaldo.values());

        Object[] campos = {
                "Nombre completo:", fNombre,
                "Cédula:", fCedula,
                "Edad:", fEdad,
                "Sexo:", cSexo,
                "Medio de contacto (tel/email):", fContacto,
                "Tipo de respaldo:", cRespaldo
        };

        int res = JOptionPane.showConfirmDialog(this, campos,
                "Registrar Inquilino", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        try {
            String nombre   = req(fNombre,   "Nombre");
            String cedula   = req(fCedula,   "Cédula");
            Validador.validarCedula(cedula);
            int    edad     = Integer.parseInt(fEdad.getText().trim());
            Validador.validarEdad(edad);
            Inquilino.Sexo sexoEnum = (Inquilino.Sexo) cSexo.getSelectedItem();
            String sexo     = sexoEnum.getDescripcion();
            String contacto = req(fContacto, "Medio de contacto");
            Validador.validarContacto(contacto);
            Inquilino.TipoRespaldo respaldo =
                    (Inquilino.TipoRespaldo) cRespaldo.getSelectedItem();

            String id = servicio.registrarInquilino(
                    nombre, cedula, edad, sexo, contacto, respaldo);

            JOptionPane.showMessageDialog(this,
                    "  Inquilino registrado con ID: " + id,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            actualizar();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { SwingUtil.mostrarAviso(this, "Seleccione un inquilino."); return; }
        String id = (String) modelo.getValueAt(fila, 0);
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Eliminar inquilino " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            boolean ok = servicio.eliminarInquilino(id);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Inquilino eliminado correctamente.",
                        "Info", JOptionPane.INFORMATION_MESSAGE);
                actualizar();
            } else {
                JOptionPane.showMessageDialog(this,
                        "NO se puede eliminar: tiene alquileres activos. Desalquile primero.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String req(JTextField tf, String nombre) {
        String v = tf.getText().trim();
        if (v.isEmpty()) throw new IllegalArgumentException(
                "\"" + nombre + "\" es obligatorio.");
        return v;
    }

}