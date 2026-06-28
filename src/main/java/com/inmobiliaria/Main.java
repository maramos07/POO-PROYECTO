package com.inmobiliaria;

import com.inmobiliaria.vista.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación inmobiliaria.
 */
public class Main {
    public static void main(String[] args) {
        // Aplicar look & feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Iniciar la interfaz en el hilo de eventos de Swing
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
