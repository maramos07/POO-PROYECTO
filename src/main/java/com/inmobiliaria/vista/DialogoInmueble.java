package com.inmobiliaria.vista;

import com.inmobiliaria.modelo.*;
import com.inmobiliaria.servicio.InmuebleServicio;
import com.inmobiliaria.util.Validador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Diálogo modal para registrar o editar un inmueble.
 * Adapta los campos según el tipo seleccionado: Edificio, Piso o Local.
 */
public class DialogoInmueble extends JDialog {

    private final InmuebleServicio servicio;
    private final Inmueble inmuebleEditar; // null = nuevo
    private boolean guardado = false;

    // Campos comunes
    private JComboBox<String> cmbTipo;
    private JTextField txtDireccion, txtNumero, txtDescripcion,
            txtCodPostal, txtPrecio;

    // Campos extra (Piso/Local)
    private JTextField txtNumPiso, txtTipoEspacio, txtDescEsp, txtEdificioId;
    private JPanel panelExtra;

    // Campos extra (Edificio)
    private JTextField txtNumPisosTotales, txtNombreEdificio;
    private JPanel panelEdificio;

    public DialogoInmueble(JFrame padre, InmuebleServicio servicio, Inmueble editar) {
        super(padre, editar == null ? "Registrar Inmueble" : "Editar Inmueble", true);
        this.servicio = servicio;
        this.inmuebleEditar = editar;

        setSize(540, 560);
        setLocationRelativeTo(padre);
        setResizable(false);
        construirUI();

        if (editar != null) precargarDatos(editar);
    }

    private void construirUI() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBorder(new EmptyBorder(20, 24, 12, 24));
        contenido.setBackground(VentanaPrincipal.COLOR_FONDO);

        // Tipo
        JPanel pTipo = fila("Tipo de inmueble:");
        String[] tipos = {"EDIFICIO", "PISO", "LOCAL"};
        cmbTipo = new JComboBox<>(tipos);
        cmbTipo.setFont(VentanaPrincipal.FUENTE_NORMAL);
        if (inmuebleEditar != null) {
            cmbTipo.setSelectedItem(inmuebleEditar.getTipoInmueble());
            cmbTipo.setEnabled(false);
        }
        cmbTipo.addActionListener(e -> actualizarPanelExtra());
        pTipo.add(cmbTipo);

        // Campos comunes
        txtDireccion  = campo(); txtNumero    = campo();
        txtDescripcion= campo(); txtCodPostal = campo();
        txtPrecio     = campo();

        // Panel campos extra Piso/Local
        panelExtra = new JPanel();
        panelExtra.setLayout(new BoxLayout(panelExtra, BoxLayout.Y_AXIS));
        panelExtra.setBackground(VentanaPrincipal.COLOR_FONDO);
        txtNumPiso    = campo(); txtTipoEspacio = campo();
        txtDescEsp    = campo(); txtEdificioId  = campo();

        // Panel campos extra Edificio
        panelEdificio = new JPanel();
        panelEdificio.setLayout(new BoxLayout(panelEdificio, BoxLayout.Y_AXIS));
        panelEdificio.setBackground(VentanaPrincipal.COLOR_FONDO);
        txtNumPisosTotales = campo(); txtNombreEdificio = campo();

        contenido.add(pTipo);
        contenido.add(Box.createVerticalStrut(6));
        agregarFila(contenido, "Dirección:", txtDireccion);
        agregarFila(contenido, "Número / ID interno:", txtNumero);
        agregarFila(contenido, "Descripción:", txtDescripcion);
        agregarFila(contenido, "Código Postal:", txtCodPostal);
        agregarFila(contenido, "Precio de Alquiler ($):", txtPrecio);
        contenido.add(panelExtra);
        contenido.add(panelEdificio);
        contenido.add(Box.createVerticalGlue());
        contenido.add(crearBotones());

        setContentPane(new JScrollPane(contenido));
        actualizarPanelExtra();
    }

    private void actualizarPanelExtra() {
        String tipo = (String) cmbTipo.getSelectedItem();
        panelExtra.removeAll();
        panelEdificio.removeAll();

        if ("EDIFICIO".equals(tipo)) {
            agregarFila(panelEdificio, "Nombre del Edificio:", txtNombreEdificio);
            agregarFila(panelEdificio, "Número total de pisos:", txtNumPisosTotales);
        } else {
            agregarFila(panelExtra, "Número de piso:", txtNumPiso);
            agregarFila(panelExtra, "Tipo de espacio:", txtTipoEspacio);
            agregarFila(panelExtra, "Descripción específica:", txtDescEsp);
            agregarFila(panelExtra, "ID Edificio (opcional):", txtEdificioId);
        }
        panelExtra.revalidate(); panelExtra.repaint();
        panelEdificio.revalidate(); panelEdificio.repaint();
    }

    private void precargarDatos(Inmueble inm) {
        txtDireccion.setText(inm.getDireccion());
        txtNumero.setText(inm.getNumero());
        txtDescripcion.setText(inm.getDescripcion());
        txtCodPostal.setText(inm.getCodigoPostal());
        txtPrecio.setText(String.valueOf(inm.getPrecioAlquiler()));

        if (inm instanceof Piso p) {
            txtNumPiso.setText(String.valueOf(p.getNumeroPiso()));
            txtTipoEspacio.setText(p.getTipoEspacio());
            txtDescEsp.setText(p.getDescripcionEspecifica());
            txtEdificioId.setText(p.getEdificioId() != null ? p.getEdificioId() : "");
        } else if (inm instanceof Local l) {
            txtNumPiso.setText(String.valueOf(l.getNumeroPiso()));
            txtTipoEspacio.setText(l.getTipoLocal());
            txtDescEsp.setText(l.getDescripcionEspecifica());
            txtEdificioId.setText(l.getEdificioId() != null ? l.getEdificioId() : "");
        } else if (inm instanceof Edificio e) {
            txtNombreEdificio.setText(e.getNombreEdificio());
            txtNumPisosTotales.setText(String.valueOf(e.getNumeroPisos()));
        }
    }

    private JPanel crearBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);

        JButton btnGuardar = btn("Guardar", VentanaPrincipal.COLOR_ACENTO);
        JButton btnCancelar = btn("Cancelar", new Color(100, 116, 139));

        btnGuardar.addActionListener(e -> guardar());
        btnCancelar.addActionListener(e -> dispose());

        p.add(btnCancelar); p.add(btnGuardar);
        return p;
    }

    private void guardar() {
        try {
            String tipo      = (String) cmbTipo.getSelectedItem();
            String dir       = validar(txtDireccion, "Dirección");
            String num       = validar(txtNumero,    "Número");
            String desc      = validar(txtDescripcion,"Descripción");
            String cp        = validar(txtCodPostal,  "Código Postal");
            Validador.validarCodigoPostal(cp);
            double precio    = Double.parseDouble(txtPrecio.getText().trim());
            Validador.validarPositivo(precio, "Precio de Alquiler");

            if (inmuebleEditar != null) {
                // Modo edición: solo actualiza campos modificables
                servicio.modificarInmueble(inmuebleEditar.getId(), desc, cp, precio);
            } else {
                // Modo nuevo
                switch (tipo) {
                    case "EDIFICIO" -> {
                        String nombre = txtNombreEdificio.getText().trim();
                        if (nombre.isBlank()) throw new IllegalArgumentException("El nombre del edificio es obligatorio.");
                        int pisos = Integer.parseInt(txtNumPisosTotales.getText().trim());
                        servicio.registrarEdificio(dir, num, desc, cp, precio, pisos, nombre);
                    }
                    case "PISO" -> {
                        int nPiso   = Integer.parseInt(txtNumPiso.getText().trim());
                        String tEsp = txtTipoEspacio.getText().trim();
                        if (tEsp.isBlank()) throw new IllegalArgumentException("El tipo de espacio es obligatorio.");
                        String dEsp = txtDescEsp.getText().trim();
                        if (dEsp.isBlank()) throw new IllegalArgumentException("La descripción específica es obligatoria.");
                        String edId = txtEdificioId.getText().trim();
                        servicio.registrarPiso(dir, num, desc, cp, precio,
                                nPiso, tEsp, dEsp, edId.isEmpty() ? null : edId);
                    }
                    case "LOCAL" -> {
                        int nPiso   = Integer.parseInt(txtNumPiso.getText().trim());
                        String tLoc = txtTipoEspacio.getText().trim();
                        if (tLoc.isBlank()) throw new IllegalArgumentException("El tipo de local es obligatorio.");
                        String dEsp = txtDescEsp.getText().trim();
                        if (dEsp.isBlank()) throw new IllegalArgumentException("La descripción específica es obligatoria.");
                        String edId = txtEdificioId.getText().trim();
                        servicio.registrarLocal(dir, num, desc, cp, precio,
                                nPiso, tLoc, dEsp, edId.isEmpty() ? null : edId);
                    }
                }
            }

            guardado = true;
            JOptionPane.showMessageDialog(this, "Inmueble guardado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Ingrese valores numéricos válidos en Precio y Número de Piso.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
        }
    }

    public boolean isGuardado() { return guardado; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String validar(JTextField tf, String nombre) {
        String v = tf.getText().trim();
        if (v.isEmpty()) throw new IllegalArgumentException("El campo \"" + nombre + "\" es obligatorio.");
        return v;
    }

    private JTextField campo() {
        JTextField tf = new JTextField();
        tf.setFont(VentanaPrincipal.FUENTE_NORMAL);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        return tf;
    }

    private JPanel fila(String label) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);
        JLabel lbl = new JLabel(label);
        lbl.setFont(VentanaPrincipal.FUENTE_NORMAL);
        p.add(lbl);
        return p;
    }

    private void agregarFila(JPanel parent, String label, JTextField campo) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(VentanaPrincipal.FUENTE_NORMAL);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        campo.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        parent.add(Box.createVerticalStrut(4));
        parent.add(lbl);
        parent.add(campo);
    }

    private JButton btn(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(VentanaPrincipal.FUENTE_NORMAL);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
