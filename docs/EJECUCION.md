# Ejecutar y probar Moute

## Android

La configuración del repositorio fija Gradle 5.6.4, Android Gradle Plugin 3.6.2, `compileSdkVersion` y `targetSdkVersion` 29, Build Tools 29.0.2 y `minSdkVersion` 16. El mínimo declarado no significa que se hayan probado todas las versiones de Android.

1. Abre la carpeta `Moute` en Android Studio, no `mouteAdmin` ni el directorio de la plantilla AngularJS.
2. Configura el JDK de Gradle para esta versión antigua. La compilación Java verificada en este entorno utilizó JDK 8; no utilices automáticamente el JDK moderno incluido en Android Studio para Gradle 5.6.4. La ruta es una configuración local, no parte del repositorio.
3. Instala Android SDK Platform 29 y Build Tools 29.0.2. Android Studio crea `local.properties` con la ruta del SDK; no copies la ruta absoluta de otro equipo ni publiques ese archivo.
4. Para una copia pública sin configuración local, crea un proyecto Firebase de pruebas, registra la app `com.manuel.moute` y coloca su `google-services.json` en `Moute/app/`. Nunca uses una clave privada del Admin SDK en Android.
5. Si necesitas probar cuentas, habilita el proveedor correo/contraseña de Firebase Auth y configura Realtime Database y Storage con datos ficticios y reglas apropiadas. El archivo de reglas incluido es un borrador y no satisface todos los flujos del cliente actual: lee `firebase/README.md` antes de desplegarlo.
6. La configuración de Google Maps está en `AndroidManifest.xml`. Sustituye la del autor por una propia y restringida si vas a usar mapas. No es necesaria para contratar una tarjeta local de invitado.
7. Sincroniza Gradle. Crea o selecciona un emulador Android 10/API 29, selecciona la configuración `app` y pulsa **Run**. Arrancar el emulador por sí solo no instala ni ejecuta Moute.

Desde una terminal situada en `Moute`, con JDK y SDK configurados:

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`. La firma de depuración es local; no debe publicarse su almacén de claves. Para distribución real se necesita un proceso de firma y actualización de herramientas fuera del alcance de la demo.

### Primera prueba sin cuenta

Abre Contratar tarjeta, elige un producto y confirma «Contratar como invitado». Comprueba que aparece en Mis tarjetas, abre el detalle y reinicia la aplicación para comprobar la persistencia. Crea una segunda tarjeta antes de comprobar el borrado individual, usando solo datos de prueba. No introduzcas información bancaria real: este flujo no cobra dinero ni necesita PayPal.

### Pruebas existentes y límites

`src/test` contiene pruebas locales sencillas y del generador de números. `src/androidTest` contiene pruebas de instrumentación, incluida la conexión del campo de teléfono al selector de país. Estas últimas necesitan un dispositivo o emulador:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

En la revisión anterior se verificaron pruebas locales y el flujo invitado con un APK de depuración en API 29. La compilación de recursos de las pruebas instrumentadas se bloqueó por un fallo nativo de AAPT2 en el entorno de trabajo; no se afirma que toda la suite esté validada. La pasada de comentarios comprueba que la lógica permanece igual, no sustituye una prueba completa de registro, Firebase, pagos o todas las versiones Android.

## Panel web Angular

El panel usa Angular 8.2, CLI 8.3 y TypeScript 3.5 según `package.json`. Sus herramientas son antiguas; una actualización de dependencias requiere una revisión separada.

1. Entra en `mouteAdmin`.
2. Si los archivos locales de entorno no están incluidos, copia `config/environment.example.ts` a `src/environments/environment.ts` y a `environment.prod.ts`; en el segundo cambia `production` a `true`.
3. Rellena `firebaseConfig` con un proyecto de pruebas propio. No pegues credenciales de servicio/Admin SDK ni datos de personas reales.
4. Instala las dependencias del archivo de bloqueo y arranca el servidor:

```powershell
npm ci
npm start
```

Abre `http://localhost:4200/`. Para generar la salida de distribución:

```powershell
npm run build -- --prod
```

El resultado queda en `dist/mouteAdmin`. En el entorno verificado con Node moderno el Webpack antiguo necesitó compatibilidad OpenSSL:

```powershell
$env:NODE_OPTIONS = '--openssl-legacy-provider'
npm run build -- --prod
```

Es una solución de compatibilidad para esta demostración, no una modernización del proyecto. `npm test` y `npm run e2e` contienen pruebas heredadas: varios selectores y montajes no corresponden a todas las pantallas actuales y no deben presentarse como una suite completa aprobada.

Si Firebase rechaza la consulta, no abras las reglas públicamente para hacer funcionar el panel. Actualmente falta implementar su acceso administrativo; usa únicamente un entorno aislado con datos ficticios mientras se resuelve esa limitación.
