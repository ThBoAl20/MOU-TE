# Moute — aplicación Android

Este módulo contiene la aplicación Android en Java. Abre **esta carpeta** en Android Studio: Gradle compila `app/src` y el manifiesto inicia la actividad `SplashScreen`.

- [Descripción del proyecto](../README.md)
- [Arquitectura y recorrido del código](../docs/ARQUITECTURA.md)
- [Configuración, ejecución y pruebas](../docs/EJECUCION.md)
- [Límites de seguridad y prototipo](../docs/SEGURIDAD-Y-ESTADO.md)
- [Preparación de una copia para GitHub](../docs/PUBLICACION-GITHUB.md)

## Recorrido de lectura

`MainActivity` coordina menú y sesión. `ContratarFragment` presenta el catálogo y `TypeOfCardFragment` personaliza las tarjetas. Sin sesión, `GuestCardStore` guarda tarjetas de demostración en el dispositivo. `MisTarjetasFragment` las lista y `VerTarjetaFragment` muestra el detalle. Con cuenta existen ramas Firebase y una integración de muestra PayPal sandbox; no habilitan cobros reales.

`ui/Register` contiene los tres pasos de registro. Los perfiles no almacenan contraseñas; el argumento de registro se mantiene separado y se entrega a Firebase Auth. Los comentarios identifican los flujos incompletos y las limitaciones asíncronas existentes.

## Herramientas fijadas en el código

Gradle 5.6.4, Android Gradle Plugin 3.6.2, SDK 29 y Build Tools 29.0.2. No uses automáticamente el JDK más reciente para este Gradle antiguo. La guía recoge el entorno verificado y los límites de las pruebas.

## Plantilla web heredada

`app/app.js`, `app/index*.html`, `app/view1`, `app/view2`, `app/core`, `package.json`, `karma.conf.js` y `e2e-tests` pertenecen a angular-seed (AngularJS). No son la interfaz de Android ni el panel administrativo, que está en `../mouteAdmin`. Se conservan para no eliminar material del proyecto original; no necesitas instalarlos ni ejecutarlos para probar la app Android.

El aviso de licencia original de angular-seed permanece en `LICENSE`. No se extiende automáticamente al código propio de Moute.

`package-lock.json` pertenece a esa plantilla y `.jshintrc` declara sus globales de pruebas. `.travis.yml` es su configuración de CI heredada, no un proceso verificado de compilación Android. Los scripts `gradlew`/`gradlew.bat` y el JAR del Wrapper son herramientas generadas: se conservan sin reescribirlos ni tratarlos como código de negocio.
