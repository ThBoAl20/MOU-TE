# Moute Admin — panel de consulta Angular

Panel web académico en Angular 8 que lee usuarios y tarjetas de Firebase Realtime Database. Es independiente de la plantilla AngularJS conservada dentro de la carpeta Android.

## Lectura del código

- `src/app/app-routing.module.ts`: rutas de listado, detalle por UID y vista de solicitudes.
- `src/app/services/user.service.ts`: referencias observables a `Usuarios`.
- `src/app/components/user-list` y `ver-usuario`: consulta de perfiles.
- `src/app/components/solicitud-tarjeta`: filtra usuarios con tarjetas; no representa una cola real de envíos.
- `src/app/models`: forma esperada de los datos, sin validación de seguridad en tiempo de ejecución.

Los comentarios describen los bindings de las plantillas, sus datos y los restos del prototipo. El formulario `user-form` todavía está vacío y la ruta `games/edit/:id` es heredada.

## Ejecutar

Lee la [guía de ejecución compartida](../docs/EJECUCION.md). Crea los entornos locales a partir de `config/environment.example.ts` y configura un proyecto Firebase de pruebas propio. Usa `npm ci`, `npm start` y `npm run build -- --prod` según la guía.

`angular.json` indica entradas, assets, salida y reemplazo del entorno de producción. `package-lock.json` fija la resolución de dependencias. `tsconfig*.json` configura compilación y pruebas. Son JSON: su documentación va aquí, no en comentarios que invaliden su formato.

## Advertencias

El panel **no implementa login ni guardas de administrador**. Importar AngularFireAuth no protege las rutas. No publiques el panel conectado a datos reales ni abras las reglas de Firebase para evitar un error de permisos. Las tarjetas locales de invitado Android no llegan a este panel.

Los tests de Karma y Protractor conservan ejemplos generados y montajes incompletos. No son una cobertura integral y esta pasada documental no los repara. Consulta [estado y seguridad](../docs/SEGURIDAD-Y-ESTADO.md) y [publicación en GitHub](../docs/PUBLICACION-GITHUB.md).
