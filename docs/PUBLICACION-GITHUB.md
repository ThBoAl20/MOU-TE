# Preparar una copia para GitHub

La carpeta de trabajo conserva material original y configuraciones para poder ejecutar la demo. **No subas toda la carpeta arrastrándola a GitHub ni publiques el ZIP original.**

## Copia pública

1. Prepara una carpeta limpia con `README.md`, `docs/`, `firebase/`, el código de `Moute/` y `mouteAdmin/`. No borres ni sustituyas la copia de trabajo.
2. Excluye las carpetas `.git` que puedan venir dentro de los módulos: su historial no ha sido revisado para publicación. Si se inicializa un nuevo repositorio con ellas dentro, pueden convertirse en repositorios anidados en lugar de subir el código esperado.
3. Conserva el código fuente, los recursos necesarios, archivos de configuración de compilación, Gradle Wrapper y los archivos de bloqueo de dependencias. Excluye bibliotecas descargadas y salidas generadas (`node_modules`, `build`, `dist`, cachés, APK y AAB).
4. No incluyas `moute-5404e-export.json` ni otros volcados de usuarios. El PDF original también se excluye por precaución: revisa su contenido y permisos antes de decidir publicarlo. No se ha eliminado ninguno de esos originales.
5. Excluye `local.properties`, carpetas de IDE, claves privadas, archivos `.env`, tokens, certificados privados y almacenes de firma. No confundas los certificados públicos de fuentes Android con una clave privada.
6. Omite la configuración Firebase del autor (`google-services.json`, `environment.ts` y `environment.prod.ts`). El ejemplo de `mouteAdmin/config/` y la guía de ejecución permiten crear una configuración propia. Revisa y sustituye también la configuración Maps del manifiesto y el identificador cliente PayPal antes de publicar; esta pasada documental no modifica sus valores para no romper la ejecución local.
7. Usa solo datos ficticios en capturas, demostraciones y muestras. No reutilices datos del volcado original ni el estado de una cuenta real.
8. Elige una licencia para el código propio y revisa los derechos de imágenes/logos. El aviso de angular-seed no licencia automáticamente el proyecto completo.

## Revisión antes del primer envío

Revisa los archivos que Git va a registrar y su contenido antes de crear un commit. `.gitignore` no elimina archivos ya seguidos, datos en un commit anterior ni una clave embebida dentro de un archivo fuente. Si ya existe un historial, necesita una revisión propia; esta tarea no lo ha reescrito.

Los archivos JSON y de bloqueo no admiten comentarios de código, y los archivos binarios tampoco. Su función se explica en los documentos; no se añaden comentarios que rompan su formato. No deben versionarse las dependencias de terceros como si fueran código escrito para Moute.

## Qué comunicar en GitHub

Describe Moute como demostración académica y enlaza el documento de estado. Indica que los pagos son sandbox, las tarjetas de invitado son locales, el panel administrativo no está listo para exponer datos reales y las pruebas no tienen cobertura total. No anuncies los prototipos visuales como funciones terminadas.

La copia preparada para GitHub no conserva los historiales antiguos ni las configuraciones del autor. Maps y PayPal utilizan marcadores de ejemplo; el aviso de privacidad se identifica como demostración y se omite un retrato sin procedencia acreditada que no se usaba en las pantallas. No se despliegan reglas Firebase ni se habilitan cobros reales al subir el código.
