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

    private static final String[] COLS = {
            "ID", "Nombre", "Cédula", "Edad", "Sexo", "Contacto", "Respaldo"
    };

    public PanelInquilinos(InmuebleServicio servicio) {
        this.servicio = servicio;
        setLayout(new BorderLayout(8, 8));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(titulo(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(botones(), BorderLayout.SOUTH);

        actualizar();
    }

    private JLabel titulo() {
        JLabel l = new JLabel(" Gestión de Inquilinos");
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

    private JPanel botones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);

        JButton btnNuevo = btn("＋ Registrar Inquilino", VentanaPrincipal.COLOR_ACENTO);
        JButton btnElim  = btn("🗑 Eliminar",             VentanaPrincipal.COLOR_ERROR);

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
        JTextField fNombre   = campo(); JTextField fCedula  = campo();
        JTextField fEdad     = campo();
        JTextField fContacto = campo();
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
        if (fila < 0) { aviso("Seleccione un inquilino."); return; }
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

    private JTextField campo() {
        JTextField tf = new JTextField(20);
        tf.setFont(VentanaPrincipal.FUENTE_NORMAL);
        return tf;
    }

    private JButton btn(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(VentanaPrincipal.FUENTE_NORMAL);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(7, 16, 7, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}