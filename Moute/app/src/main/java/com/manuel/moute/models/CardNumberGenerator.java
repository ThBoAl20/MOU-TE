package com.manuel.moute.models;

import java.util.Random;

/**
 * Genera el identificador alfanumérico mostrado por una tarjeta de demostración.
 * No es una numeración bancaria, un token de pago ni una garantía de unicidad.
 */
public final class CardNumberGenerator {
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    private static final int LENGTH = 12;

    private CardNumberGenerator() {
    }

    /**
     * Produce 12 caracteres del alfabeto definido. Recibir Random permite usar
     * una semilla conocida en las pruebas sin cambiar el algoritmo de producción.
     */
    public static String generate(Random random) {
        StringBuilder value = new StringBuilder(LENGTH);
        for (int index = 0; index < LENGTH; index++) {
            value.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return value.toString();
    }
}
