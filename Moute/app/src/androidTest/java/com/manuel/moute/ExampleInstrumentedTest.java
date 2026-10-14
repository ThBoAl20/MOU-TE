package com.manuel.moute;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Prueba instrumentada de la plantilla, ejecutada en dispositivo o emulador.
 * Comprueba el identificador del paquete instalado, no los flujos de negocio.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Contexto del APK de Moute, distinto al contexto del APK auxiliar de pruebas.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.manuel.moute", appContext.getPackageName());
    }
}
