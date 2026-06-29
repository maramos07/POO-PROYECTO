package com.inmobiliaria.util;

public class Validador {

    public static void validarCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new IllegalArgumentException("La cédula es obligatoria.");
        }
        if (!cedula.matches("\\d+")) {
            throw new IllegalArgumentException("La cédula debe contener solo dígitos.");
        }
        if (cedula.length() < 4 || cedula.length() > 15) {
            throw new IllegalArgumentException("La cédula debe tener entre 4 y 15 dígitos.");
        }
    }
}
