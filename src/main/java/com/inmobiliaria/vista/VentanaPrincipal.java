package com.inmobiliaria.vista;

import com.inmobiliaria.servicio.InmuebleServicio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la aplicación inmobiliaria.
 * Organiza las funcionalidades mediante pestañas (JTabbedPane).
 * @author Equipo POO
 */
public class VentanaPrincipal extends JFrame {

    private final InmuebleServicio servicio = new InmuebleServicio();

    // ── Paleta corporativa Inmobiliarios ────────────────────────
    public static final Color COLOR_PRIMARIO    = new Color(30,  46,  82);   // Azul marino oscuro
    public static final Color COLOR_SECUNDARIO  = new Color(58,  95, 160);   // Azul medio
    public static final Color COLOR_TERCIARIO   = new Color(107,155, 210);   // Azul claro
    public static final Color COLOR_ACENTO      = new Color(201,169, 110);   // Dorado/beige oscuro
    public static final Color COLOR_ACENTO_CLARO= new Color(232,213,176);   // Beige claro
    public static final Color COLOR_FONDO       = new Color(248,247,244);    // Blanco cálido
    public static final Color COLOR_TEXTO       = new Color(30,  46,  82);   // Marino para texto
    public static final Color COLOR_EXITO       = new Color(58,  95, 160);   // Azul medio como éxito
    public static final Color COLOR_ERROR       = new Color(180,  50,  50);  // Rojo sobrio

    public static final Font  FUENTE_SUBTITULO  = new Font("Helvetica", Font.BOLD,  14);
    public static final Font  FUENTE_NORMAL     = new Font("Helvetica", Font.PLAIN, 13);

    public VentanaPrincipal() {
        configurarVentana();
        if (!servicio.isDatosCargadosCorrectamente()) {
            JOptionPane.showMessageDialog(this,
                    "Algunos archivos de datos no pudieron cargarse.\n"
                    + "Los datos pueden estar corruptos o faltar.\n"
                    + "Revise la carpeta datos/.",
                    "Advertencia de carga", JOptionPane.WARNING_MESSAGE);
        }
        JTabbedPane tabbedPane = crearTabbedPane();
        add(crearHeader(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión Inmobiliaria");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1150, 750);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARIO);

        // Línea dorada decorativa en la parte inferior del header
        JPanel lineaDorada = new JPanel();
        lineaDorada.setBackground(COLOR_ACENTO);
        lineaDorada.setPreferredSize(new Dimension(0, 4));

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setOpaque(false);
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        // Marca ESCA
        JLabel marca = new JLabel("Sistema de Gestión Inmobilaria");
        marca.setFont(new Font("Impact", Font.PLAIN,28));
        marca.setForeground(COLOR_ACENTO);

        JPanel panelMarca = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelMarca.setOpaque(false);
        panelMarca.add(marca);

        JLabel subtitulo = new JLabel("Administración integral de muebles, inquilinos y finanzas");
        subtitulo.setFont(new Font("Helvetica", Font.ITALIC, 12));
        subtitulo.setForeground(new Color(167, 186, 214)); // azul claro apagado

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 3));
        textos.setOpaque(false);
        textos.add(panelMarca);
        textos.add(subtitulo);

        JButton btnSalir = new JButton("Salir");
        btnSalir.setFont(FUENTE_NORMAL);
        btnSalir.setBackground(COLOR_ERROR);
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> confirmarSalida());
        contenido.add(textos, BorderLayout.WEST);
        contenido.add(btnSalir, BorderLayout.EAST);
        header.add(contenido, BorderLayout.CENTER);
        header.add(lineaDorada, BorderLayout.SOUTH);
        return header;
    }

    private JTabbedPane crearTabbedPane() {
        JTabbedPane tp = new JTabbedPane(JTabbedPane.LEFT);
        tp.setFont(new Font("Helvetica", Font.PLAIN, 13));
        tp.setBackground(COLOR_FONDO);

        tp.addTab("⌂ Inmuebles",   null, new PanelInmuebles(servicio),   "Gestionar inmuebles");
        tp.addTab("Inquilinos",  null, new PanelInquilinos(servicio),  "Gestionar inquilinos");
        tp.addTab("Alquileres",  null, new PanelAlquileres(servicio),  "Alquilar y desalquilar");
        tp.addTab("$ Facturas",    null, new PanelFacturas(servicio),    "Registrar gastos");
        tp.addTab("✎ Movimientos", null, new PanelMovimientos(servicio), "Movimientos bancarios");

        return tp;
    }


    /**
     * Muestra un diálogo de confirmación antes de cerrar la aplicación.
     * Si el usuario confirma, libera los recursos de la ventana y finaliza el proceso.
     */
    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Desea salir del sistema?",
                "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }}
