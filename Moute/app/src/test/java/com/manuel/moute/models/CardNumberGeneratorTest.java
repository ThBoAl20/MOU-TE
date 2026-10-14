package com.manuel.moute.models;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Prueba local del formato de identificadores de tarjetas de demostración.
 * No requiere Android ni Firebase; la semilla fija hace reproducible el caso.
 * No demuestra unicidad global, seguridad criptográfica ni validez bancaria.
 */
public class CardNumberGeneratorTest {
    /** Exige doce caracteres del alfabeto que usa el generador: mayúsculas y dígitos del 1 al 9. */
    @Test
    public void generatesATwelveCharacterAlphanumericIdentifier() {
        String value = CardNumberGenerator.generate(new Random(7));

        assertEquals(12, value.length());
        assertTrue(value.matches("[A-Z1-9]{12}"));
    }
}
