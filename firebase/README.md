# Borrador de reglas de Realtime Database

`database.rules.json` es una propuesta de reglas, **no una configuración desplegada ni compatible con todos los flujos actuales**. Ningún archivo de documentación aplica reglas en el proyecto Firebase.

## Qué hace cada nodo

- `Usuarios/$uid/.read`: permite leer ese usuario a su propietario autenticado o a una cuenta con el custom claim `admin: true`.
- `Usuarios/$uid/Datos/.write`: permite escribir el perfil al propietario o administrador.
- `Datos/.validate`: exige nombre, apellido y correo, y rechaza una propiedad llamada `contraseña`. No valida por completo todos los tipos, formatos ni campos adicionales.
- `Usuarios/$uid/listaTarjetas/.write`: limita las escrituras de tarjetas a administradores.

El archivo es JSON de reglas de Firebase y no admite comentarios. Estas explicaciones se mantienen fuera del archivo para no invalidarlo.

## Incompatibilidades que hay que resolver

El cliente Android con cuenta todavía escribe tarjetas directamente. Con estas reglas, una cuenta normal no podrá completar esa escritura. Además, no hay permiso para leer el catálogo `Tarjetas` ni para consultar globalmente `Usuarios`, que es lo que intenta el panel. Autorizar lecturas por usuario no concede automáticamente la consulta de toda la colección.

El invitado utiliza almacenamiento local y no necesita esos permisos para sus tarjetas, aunque Android sigue inicializando el SDK con su configuración de cliente.

## Antes de desplegar

1. Decide el diseño de permisos para catálogo, perfiles, tarjetas y panel. No habilites lectura/escritura pública de datos personales para hacer funcionar la demo.
2. Implementa autenticación administrativa en el panel y asigna el claim de administrador únicamente desde un entorno de confianza, nunca desde el cliente.
3. Para pagos reales, usa un backend que verifique el pago y gestione la creación de tarjetas. **No es necesario habilitar pagos reales para conservar la muestra PayPal sandbox del proyecto.**
4. Prueba casos permitidos y denegados con datos ficticios antes de aplicar las reglas. Las reglas de Storage se configuran aparte y no están incluidas aquí.

Ver [seguridad y estado](../docs/SEGURIDAD-Y-ESTADO.md) y [arquitectura](../docs/ARQUITECTURA.md).
