# Moute

Proyecto académico de gestión de tarjetas de transporte. Incluye una aplicación Android en Java y un panel web en Angular para consultar usuarios y tarjetas de Firebase.

**Es una demostración, no un servicio de transporte ni una pasarela de pagos en producción.** PayPal se conserva como ejemplo en sandbox. Las tarjetas creadas como invitado no cobran dinero y solo se guardan en el dispositivo.

## Qué se puede explorar

- Contratación de tarjetas de muestra sin iniciar sesión, consulta del detalle, activación local y borrado individual.
- Registro por pasos, inicio de sesión, recuperación de contraseña y perfil mediante Firebase.
- Catálogo y tarjetas de las cuentas conectadas a Realtime Database.
- Panel web de consulta de usuarios y detalle por UID.

Algunas pantallas y acciones siguen siendo prototipos. La verificación por teléfono no está implementada, la recarga no completa un flujo funcional y el panel no incluye un acceso administrativo protegido. Los comentarios señalan estos límites donde aparecen; ver también [estado y seguridad](docs/SEGURIDAD-Y-ESTADO.md).

## Estructura

| Carpeta | Responsabilidad |
| --- | --- |
| `Moute/app/src/main/java/com/manuel/moute/` | Actividades, fragmentos, adaptadores y modelos Android. |
| `Moute/app/src/main/res/` | Diseños, navegación, textos y recursos visuales Android. |
| `Moute/app/src/test/` y `Moute/app/src/androidTest/` | Pruebas locales e instrumentadas; no son una cobertura completa. |
| `mouteAdmin/src/app/` | Componentes, rutas, modelos y servicio de consulta del panel Angular. |
| `firebase/` | Borrador de reglas de Realtime Database, no desplegado automáticamente. |
| `docs/` | Arquitectura, ejecución, configuración y preparación para GitHub. |

Dentro de `Moute/app/` también hay una plantilla **AngularJS angular-seed** (`app.js`, `view1`, `view2`, `core`). Es material heredado, independiente de la app Android y de `mouteAdmin`; no hay que ejecutarla para probar Moute. No se comentan ni se modifican bibliotecas de terceros o archivos generados.

## Por dónde empezar a leer

1. [Arquitectura y flujos](docs/ARQUITECTURA.md): relación entre pantallas, modelos y almacenamiento.
2. Android: `MainActivity.java`, `ui/ContratarFragment.java`, `ui/TypeOfCardFragment.java`, `models/GuestCardStore.java` y `ui/MisTarjetas/MisTarjetasFragment.java`.
3. Registro: `ui/Register/RegisterFragment.java`, `Register2Fragment.java` y `Register3Fragment.java`.
4. Panel: `app-routing.module.ts`, `services/user.service.ts` y los componentes `user-list` y `ver-usuario`.

Los comentarios están en español y explican responsabilidades, argumentos de navegación, datos locales/remotos y decisiones no evidentes. Los nombres originales se mantienen para no romper referencias de Android o la estructura almacenada en Firebase.

## Ejecutar el proyecto

Esta copia para GitHub omite las configuraciones personales de Firebase y usa valores de ejemplo para Maps y PayPal. Quien clone el repositorio debe configurar su propio proyecto de pruebas siguiendo la guía. No incluye usuarios reales ni el historial Git del trabajo original.

Consulta la [guía de ejecución](docs/EJECUCION.md). Android usa Gradle 5.6.4, Android Gradle Plugin 3.6.2 y SDK 29; el panel usa Angular 8. No son proyectos creados con las versiones actuales de esas herramientas.

Para empezar por la demostración local, abre **la carpeta `Moute`** en Android Studio y utiliza un emulador Android 10/API 29. El modo invitado no necesita una cuenta de usuario, pero la compilación sigue necesitando la configuración Android de Firebase del proyecto.

## Antes de subirlo a GitHub

Lee la [lista de publicación](docs/PUBLICACION-GITHUB.md). El volcado original de la base de datos contiene datos personales y no debe publicarse. Tampoco deben subirse claves privadas, almacenes de firma, configuraciones locales o historiales Git antiguos sin revisión. `.gitignore` ayuda a excluir archivos nuevos, pero no elimina archivos ya registrados ni limpia su historial.

Esta documentación no publica el proyecto ni despliega reglas, modifica Firebase o habilita cobros reales.

## Licencia

El archivo `Moute/LICENSE` procede de la plantilla angular-seed. Se conserva su aviso, pero no se interpreta como una licencia elegida para todo Moute. El autor debe decidir la licencia de su código y revisar los derechos de los recursos antes de distribuirlo.
