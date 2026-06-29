package com.inmobiliaria.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Utilidades de interfaz gráfica para homogeneizar la creación de componentes Swing
 * con la paleta de colores y fuentes definidas en {@link VentanaPrincipal}.
 * @author Equipo POO
 */
public class SwingUtil {

    /**
     * Crea un botón estilizado con la fuente normal y el color de fondo indicado.
     * @param texto texto del botón
     * @param fondo color de fondo del botón
     * @return botón configurado
     */
    public static JButton crearBoton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setFont(VentanaPrincipal.FUENTE_NORMAL);
        b.setBackground(fondo);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(7, 16, 7, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /**
     * Crea un campo de texto con la fuente normal.
     * @param columnas número de columnas visibles
     * @return campo de texto configurado
     */
    public static JTextField crearTextField(int columnas) {
        JTextField t = new JTextField(columnas);
        t.setFont(VentanaPrincipal.FUENTE_NORMAL);
        return t;
    }

    /**
     * Crea una etiqueta de texto con la fuente normal.
     * @param texto texto de la etiqueta
     * @return etiqueta configurada
     */
    public static JLabel crearLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(VentanaPrincipal.FUENTE_NORMAL);
        return l;
    }

    /**
     * Muestra un diálogo de advertencia con el mensaje indicado.
     * @param padre componente padre sobre el que se muestra el diálogo
     * @param msg   mensaje de advertencia
     */
    public static void mostrarAviso(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
