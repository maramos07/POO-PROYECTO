package com.inmobiliaria.vista;

import com.inmobiliaria.modelo.*;
import com.inmobiliaria.servicio.InmuebleServicio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel para registrar alquileres y desalquileres de inmuebles.
 */
public class PanelAlquileres extends JPanel {

    private final InmuebleServicio servicio;
    private DefaultTableModel modeloAlq;
    private JTable tablaAlq;

    private static final DateTimeFormatter FMT_VISTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] COLS_ALQ = {
            "ID Alquiler", "Inquilino ID", "Inmueble ID", "Fecha Inicio", "Fecha Fin", "Estado"
    };

    public PanelAlquileres(InmuebleServicio servicio) {
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
        JLabel l = new JLabel("Alquileres — Registro de contratos activos e históricos");
        l.setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        l.setForeground(VentanaPrincipal.COLOR_PRIMARIO);
        l.setBorder(new EmptyBorder(0, 0, 8, 0));
        return l;
    }

    private JScrollPane crearTabla() {
        modeloAlq = new DefaultTableModel(COLS_ALQ, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaAlq = new JTable(modeloAlq);
        tablaAlq.setFont(VentanaPrincipal.FUENTE_NORMAL);
        tablaAlq.setRowHeight(28);
        tablaAlq.getTableHeader().setFont(VentanaPrincipal.FUENTE_SUBTITULO);
        tablaAlq.getTableHeader().setBackground(VentanaPrincipal.COLOR_PRIMARIO);
        tablaAlq.getTableHeader().setForeground(VentanaPrincipal.COLOR_ACENTO);
        tablaAlq.setSelectionBackground(new Color(201, 169, 110, 80));
        tablaAlq.setGridColor(new Color(220, 215, 205));
        JScrollPane sp = new JScrollPane(tablaAlq);
        sp.setBorder(BorderFactory.createLineBorder(new Color(201, 169, 110)));
        return sp;
    }

    private JPanel botones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        p.setBackground(VentanaPrincipal.COLOR_FONDO);

        JButton btnAlquilar  = SwingUtil.crearBoton("Alquilar Inmueble",    VentanaPrincipal.COLOR_ACENTO);
        JButton btnDesalq    = SwingUtil.crearBoton("Desalquilar Inmueble",  new Color(245, 158, 11));
        JButton btnActualizar= SwingUtil.crearBoton("Actualizar",             new Color(100, 116, 139));

        btnAlquilar.addActionListener(e  -> alquilar());
        btnDesalq.addActionListener(e    -> desalquilar());
        btnActualizar.addActionListener(e -> actualizar());

        p.add(btnAlquilar); p.add(btnDesalq); p.add(btnActualizar);
        return p;
    }

    private void actualizar() {
        modeloAlq.setRowCount(0);
        for (Alquiler a : servicio.getTodosAlquileres()) {
            modeloAlq.addRow(new Object[]{
                    a.getId(), a.getInquilinoId(), a.getInmuebleId(),
                    a.getFechaInicio().format(FMT_VISTA),
                    a.getFechaFin() != null ? a.getFechaFin().format(FMT_VISTA) : "En curso",
                    a.isActivo() ? "ACTIVO" : "FINALIZADO"
            });
        }
    }

    private void alquilar() {
        // Mostrar inmuebles disponibles
        List<Inmueble> disponibles = servicio.getTodosInmuebles()
                .stream().filter(Inmueble::isDisponible).toList();

        if (disponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay inmuebles disponibles actualmente.",
                    "Sin disponibilidad", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opcionesInm = disponibles.stream()
                .map(i -> i.getId() + " — " + i.getTipoInmueble() +
                        " | " + i.getDireccion() + " (Cód: " + i.getNumero() + ")" +
                        " | $" + String.format("%.0f", i.getPrecioAlquiler()))
                .toArray(String[]::new);

        // Mostrar inquilinos registrados
        List<Inquilino> inquilinos = servicio.getTodosInquilinos();
        if (inquilinos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay inquilinos registrados. Registre uno primero.",
                    "Sin inquilinos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String[] opcionesInq = inquilinos.stream()
                .map(i -> i.getId() + " — " + i.getNombre() + " | " + i.getCedula())
                .toArray(String[]::new);

        JComboBox<String> cmbInm = new JComboBox<>(opcionesInm);
        JComboBox<String> cmbInq = new JComboBox<>(opcionesInq);

        cmbInm.setFont(VentanaPrincipal.FUENTE_NORMAL);
        cmbInq.setFont(VentanaPrincipal.FUENTE_NORMAL);

        Object[] form = {
                "Seleccione el inmueble disponible:", cmbInm,
                "Seleccione el inquilino:", cmbInq
        };

        int res = JOptionPane.showConfirmDialog(this, form,
                "Registrar Alquiler", JOptionPane.OK_CANCEL_OPTION);
        if (res != JOptionPane.OK_OPTION) return;

        String idInm = disponibles.get(cmbInm.getSelectedIndex()).getId();
        String idInq = inquilinos.get(cmbInq.getSelectedIndex()).getId();

        String idAlq = servicio.alquilarInmueble(idInm, idInq);
        if (idAlq != null) {
            JOptionPane.showMessageDialog(this,
                    "Alquiler registrado con ID: " + idAlq +
                            "\nEl inmueble ahora figura como OCUPADO.",
                    "Alquiler exitoso", JOptionPane.INFORMATION_MESSAGE);
            actualizar();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo registrar el alquiler.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desalquilar() {
        // Obtener inmuebles ocupados
        List<Inmueble> ocupados = servicio.getTodosInmuebles()
                .stream().filter(i -> !i.isDisponible()).toList();

        if (ocupados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay inmuebles ocupados actualmente.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] opciones = ocupados.stream()
                .map(i -> i.getId() + " — " + i.getDireccion() +
                        " (Cód: " + i.getNumero() + ")" +
                        " | Inquilino: " + (i.getInquilinoId() != null ? i.getInquilinoId() : "?"))
                .toArray(String[]::new);

        String sel = (String) JOptionPane.showInputDialog(this,
                "Seleccione el inmueble a desalquilar:",
                "Desalquilar Inmueble", JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
        if (sel == null) return;

        String idInm = ocupados.get(java.util.Arrays.asList(opciones).indexOf(sel)).getId();

        int conf = JOptionPane.showConfirmDialog(this,
                "¿Confirmar desalquiler del inmueble " + idInm + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            boolean ok = servicio.desalquilarInmueble(idInm);
            JOptionPane.showMessageDialog(this,
                    ok ? "Inmueble liberado y marcado DISPONIBLE."
                            : " No se pudo desalquilar.",
                    ok ? "Éxito" : "Error",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            actualizar();
        }
    }

}
