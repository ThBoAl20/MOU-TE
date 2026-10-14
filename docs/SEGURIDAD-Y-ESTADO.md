# Seguridad y estado de la demostración

Este proyecto se presenta como trabajo académico. Los comentarios describen el comportamiento que existe, no certifican su seguridad ni convierten los flujos en servicios de producción.

## Qué es una demostración

- Las tarjetas de invitado viven en el dispositivo y no tienen validez como título de transporte.
- PayPal usa sandbox. Una respuesta del SDK en el cliente no es una verificación de pago en un servidor; no se deben habilitar cobros reales con este diseño.
- Verificación telefónica, lectura/uso NFC, recarga y varios formularios conservan partes visuales o callbacks sin un flujo completo.
- La dirección de envío es un dato guardado; no crea una entrega logística real.

## Límites conocidos al leer el código

- `Register3Fragment` conserva una navegación tras seis segundos, independiente del resultado de creación y escritura del perfil. Puede mostrarse una transición aunque falle el registro; las operaciones tampoco forman una transacción conjunta.
- Algunas pantallas mantienen listeners Firebase sin retirarlos explícitamente. El código requiere trabajo adicional de ciclo de vida y manejo de errores.
- En la rama de cuenta del detalle hay escrituras de activación bajo el título de tarjeta en lugar de la clave de contratación; el diálogo de borrado remoto no completa la eliminación. No confundirlo con el borrado local de invitados, que sí tiene implementación.
- El borrado del perfil y el borrado de Firebase Auth son operaciones distintas: no hay una garantía de ejecución atómica ni de recuperación ante fallos parciales.
- `MainActivity.updateUser()` es un ejemplo heredado con credenciales de muestra; no es un método funcional de edición de perfiles ni debe contener datos reales.
- Moneda y algunas etiquetas/precios de la interfaz conservan comportamiento heredado. Los importes de demostración no constituyen tarifas oficiales.
- El panel Angular no tiene login ni guardas de administrador. La vista de solicitudes no distingue una cola real de envíos y su plantilla conserva etiquetas heredadas.
- La suite de pruebas no cubre todos estos flujos. Los ejemplos del generador, Angular y angular-seed no demuestran seguridad integral.

## Datos y credenciales

El perfil `Usuario` no serializa una contraseña. La contraseña de registro se transmite separada a Firebase Auth y no debe guardarse en Realtime Database, logs ni documentación. Los archivos de exportación originales pueden contener información que ya no refleja el modelo actual; **no deben publicarse**.

La configuración cliente de Firebase y Maps no es una clave privada de servidor y puede aparecer en una app compilada. Aun así, no es un control de acceso: hay que revisar restricciones, cuotas y reglas del proyecto propio. En particular, no se deben publicar claves privadas de servicio, tokens, contraseñas reales o almacenes de firma.

Las reglas incluidas son un borrador no desplegado. Restringen el perfil por UID y las escrituras de tarjetas al claim `admin`; el cliente de cuenta actual todavía escribe tarjetas directamente, por lo que ese flujo puede ser rechazado. Tampoco autorizan el catálogo `Tarjetas` ni la consulta global `Usuarios` usada por el panel. No deben desplegarse a ciegas ni sustituirse por acceso público.

Antes de una aplicación real hace falta un diseño coherente de permisos, autenticación del panel, validación de esquema, escritura de tarjetas desde un backend y pruebas de reglas con datos ficticios. Asignar un claim de administrador debe ser una operación de confianza fuera de los clientes.

## Avisos y licencia

Las condiciones y política presentes en la interfaz son material de la demo; no se afirma que cumplan requisitos legales de un servicio real. Conserva avisos de bibliotecas y plantilla, y revisa el origen de imágenes, logotipos y fuentes antes de publicarlos.
