package com.manuel.moute.models;

/**
 * Identificador de cliente usado por el SDK de PayPal en la demostración sandbox.
 * El entorno de prueba se selecciona en los fragmentos de contratación/detalle;
 * esta constante no habilita por sí sola cobros reales ni valida pagos en servidor.
 * Antes de reutilizar la integración, configura tu propia aplicación de pruebas.
 */
public class PayPalConfig {
    // Es un client ID del SDK, no una contraseña ni un client secret de servidor.
    public static final String PAYPAL_CLIENT_ID = "YOUR_PAYPAL_SANDBOX_CLIENT_ID";
}
