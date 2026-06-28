package com.inmobiliaria.vista;

import com.inmobiliaria.servicio.InmuebleServicio;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicación inmobiliaria.
 * Organiza las funcionalidades mediante pestañas (JTabbedPane).
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
        JTabbedPane tabbedPane = crearTabbedPane();
        add(crearHeader(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión Inmobiliaria");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

        contenido.add(textos, BorderLayout.WEST);
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


    public static void main(String[] args) {
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
