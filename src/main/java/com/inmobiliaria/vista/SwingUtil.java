package com.inmobiliaria.vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SwingUtil {

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

    public static JTextField crearTextField(int columnas) {
        JTextField t = new JTextField(columnas);
        t.setFont(VentanaPrincipal.FUENTE_NORMAL);
        return t;
    }

    public static JLabel crearLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(VentanaPrincipal.FUENTE_NORMAL);
        return l;
    }

    public static void mostrarAviso(Component padre, String msg) {
        JOptionPane.showMessageDialog(padre, msg, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
