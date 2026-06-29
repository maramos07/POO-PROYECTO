package com.inmobiliaria.util;

/**
 * Clase utilitaria con métodos estáticos de validación.
 * Cada método lanza IllegalArgumentException si el valor no cumple las reglas.
 * @author Equipo POO
 */
public class Validador {

    /**
     * Valida que una cédula tenga formato correcto.
     * @param cedula número de cédula a validar
     * @throws IllegalArgumentException si está vacía, contiene no dígitos o longitud fuera de rango
     */
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

    /**
     * Valida que la edad esté en el rango 18-120.
     * @param edad edad en años
     * @throws IllegalArgumentException si es menor a 18 o mayor a 120
     */
    public static void validarEdad(int edad) {
        if (edad < 18) {
            throw new IllegalArgumentException("El inquilino debe ser mayor de edad.");
        }
        if (edad > 120) {
            throw new IllegalArgumentException("La edad no puede superar los 120 años.");
        }
    }

    /**
     * Valida que un valor numérico sea mayor a cero.
     * @param valor valor a validar
     * @param nombreCampo nombre del campo para el mensaje de error
     * @throws IllegalArgumentException si valor <= 0
     */
    public static void validarPositivo(double valor, String nombreCampo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo \"" + nombreCampo + "\" debe ser mayor a cero.");
        }
    }

    /**
     * Valida un medio de contacto (email o teléfono).
     * Si contiene @ se valida como email, si no como teléfono (solo dígitos, 8-15).
     * @param contacto valor a validar
     * @throws IllegalArgumentException si no es un email o teléfono válido
     */
    public static void validarContacto(String contacto) {
        if (contacto == null || contacto.isBlank()) {
            throw new IllegalArgumentException("El medio de contacto es obligatorio.");
        }
        if (contacto.contains("@")) {
            int arroba = contacto.indexOf('@');
            if (arroba == 0 || !contacto.contains(".") || contacto.lastIndexOf('.') < arroba) {
                throw new IllegalArgumentException("Debe ser un email válido o un teléfono de 8-15 dígitos.");
            }
        } else {
            if (!contacto.matches("\\d+") || contacto.length() < 8 || contacto.length() > 15) {
                throw new IllegalArgumentException("Debe ser un email válido o un teléfono de 8-15 dígitos.");
            }
        }
    }

    /**
     * Valida un código postal: solo dígitos, entre 1000 y 99999.
     * @param cp código postal a validar
     * @throws IllegalArgumentException si está vacío, contiene no dígitos o fuera de rango
     */
    public static void validarCodigoPostal(String cp) {
        if (cp == null || cp.isBlank()) {
            throw new IllegalArgumentException("El código postal es obligatorio.");
        }
        if (!cp.matches("\\d+")) {
            throw new IllegalArgumentException("El código postal debe contener solo dígitos.");
        }
        int cpNum = Integer.parseInt(cp);
        if (cpNum < 1000 || cpNum > 99999) {
            throw new IllegalArgumentException("El código postal debe estar entre 1000 y 99999.");
        }
    }
}
