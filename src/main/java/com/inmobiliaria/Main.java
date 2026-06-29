package com.inmobiliaria;

import com.inmobiliaria.servicio.InmuebleServicio;
import com.inmobiliaria.util.SeedData;
import com.inmobiliaria.vista.VentanaPrincipal;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación inmobiliaria.
 * @author Equipo POO
 */
public class Main {
    /**
     * Inicia la aplicación cargando la ventana principal en el hilo de eventos de Swing.
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Aplicar look & feel del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Cargar datos de prueba si el repositorio está vacío
        SeedData.cargarSiVacio(new InmuebleServicio());

        // Iniciar la interfaz en el hilo de eventos de Swing
        SwingUtilities.invokeLater(VentanaPrincipal::new);
    }
}
