# Arquitectura y recorrido del código

## Aplicación Android

`SplashScreen` es la actividad del lanzador. Tras la presentación abre `MainActivity`, que prepara la barra de herramientas, el menú lateral, el `NavController` y las referencias a Firebase. Los destinos se declaran en `res/navigation/mobile_navigation.xml`; los botones navegan mediante sus IDs.

Los fragmentos manejan las vistas y también parte de la validación, cálculo y acceso a datos. No existe una capa de repositorios ni una arquitectura MVVM completa: `HomeViewModel` es un resto de la plantilla y no centraliza los flujos.

### Contratación y consulta

| Etapa | Archivo principal | Qué transmite o persiste |
| --- | --- | --- |
| Catálogo | `ContratarFragment` + `TarjetaAdapter` | Una `Tarjeta` en el argumento `tarjeta`. |
| Personalización | `TypeOfCardFragment` | Producto, titular de muestra, número, selecciones e importe. |
| Guardado como invitado | `GuestCardStore` | JSON local en SharedPreferences `guest_cards`, clave `cards`. |
| Guardado con cuenta | `TypeOfCardFragment` | `Usuarios/{uid}/listaTarjetas/{numCard}` después del resultado del ejemplo PayPal. |
| Mis tarjetas | `MisTarjetasFragment` + `TarjetaUserAdapter` | Modelos `TarjetaUser`; al detalle envía `cardUser` y `guestCard`. |
| Detalle | `VerTarjetaFragment` | Selecciona almacenamiento local o rama de cuenta a partir del origen. |

El invitado obtiene un catálogo local (Bus, Cercanías y Metro), confirma una contratación sin cobro y guarda una tarjeta de demostración. No se crea un usuario Firebase, no se abre PayPal y no se sincroniza con el panel. Las tarjetas locales pueden seguir mostrándose si después se inicia sesión: **no se migran automáticamente a la cuenta**.

`GuestCardStore` conserva las tarjetas por número. Guardar el mismo número actualiza esa entrada; borrar uno no debe eliminar el resto. Usa un bloqueo dentro del proceso para el ciclo leer-modificar-escribir y comprueba el resultado de `commit()`. Si el JSON está dañado evita sobrescribirlo. Al borrar los datos de la app o desinstalarla se puede perder este almacenamiento; no es una copia remota ni un título real.

`CardNumberGenerator` produce identificadores de muestra de 12 caracteres. No son números de tarjeta bancaria, no prueban un pago y no garantizan unicidad global.

### Registro y sesión

1. `RegisterFragment` valida nombre, apellidos, correo y contraseña. Envía el `Usuario` como `userA` y la contraseña separada como `registrationPassword`.
2. `Register2Fragment` añade DNI, fecha de nacimiento y género. Envía el perfil como `userB` y conserva el argumento separado de contraseña.
3. `Register3Fragment` añade el teléfono y condiciones, llama a Firebase Auth y escribe el perfil bajo `Usuarios/{uid}/Datos`.

`Usuario` no contiene una propiedad de contraseña. Eso evita serializarla como dato de perfil, pero el argumento de registro sigue siendo información sensible transitoria: no debe registrarse en logs ni tratarse como almacenamiento seguro. El selector de país necesita registrar el campo de teléfono antes de obtener el número con prefijo.

La interfaz se actualiza desde callbacks asíncronos de Firebase. El tercer paso conserva una navegación temporizada independiente del resultado remoto; es una limitación pendiente, no una garantía de que el registro se haya completado. `LoginFragment` autentica con Firebase Auth; `ForgotPasswordFragment` solicita el correo de recuperación. `ProfileFragment` consulta datos e imagen, y `DeleteAccountFragment` reautentica antes de eliminar la cuenta.

### Modelos y navegación

`Tarjeta`, `TarjetaUser` y `Usuario` implementan `Parcelable` para los argumentos entre pantallas. La escritura y lectura del Parcel deben conservar el mismo orden. `Tarjeta` y `TarjetaUser` mantienen esquemas parecidos por compatibilidad con los flujos originales; no son dos bases de datos distintas. `DireccionUser` representa la dirección del formulario de envío, sin integración logística.

Los adaptadores enlazan cada elemento de datos a su fila XML. No son la autoridad de seguridad: ocultar un botón, un menú o un campo no impide que un cliente modificado consulte Firebase si las reglas lo permiten.

## Datos compartidos con Firebase

Estructura utilizada por el código (no es un volcado de usuarios):

```text
Realtime Database
  Tarjetas/                         catálogo remoto de cuentas
  Usuarios/{uid}/
    Datos/                          perfil Usuario, sin contraseña
    listaTarjetas/{numCard}/         tarjetas de cuenta
      direccionDeEnvio/             dirección cuando se solicita
Firebase Auth                       credenciales y sesión, separado del perfil
Firebase Storage                    imágenes de perfil
```

Existen nombres y escrituras heredadas inconsistentes en el detalle de tarjetas. El esquema anterior describe la contratación, no garantiza que todas las acciones posteriores usen correctamente la misma clave. Consulta los comentarios de `VerTarjetaFragment` y el documento de estado antes de ampliar ese flujo.

## Panel Angular

`main.ts` arranca `AppModule`. El módulo importa AngularFire y la configuración `environment.firebaseConfig`. `app-routing.module.ts` dirige `/users` al listado, `/verusuarios/:id` al detalle y `/solicitudes` a una vista que filtra usuarios con tarjetas.

`UserService` devuelve referencias observables a Realtime Database. Los componentes usan `snapshotChanges()` para conservar el UID de cada registro y reconstruyen las listas en cada emisión. Los modelos TypeScript describen datos esperados, pero no validan el contenido remoto ni conceden permisos.

El panel no implementa login ni guardas de administrador. La vista «solicitudes» no es una cola de envíos; `UserFormComponent` y la ruta heredada `games/edit/:id` son bocetos. Las tarjetas de invitado no aparecen porque nunca se suben a Firebase.

## Material heredado

La plantilla AngularJS bajo `Moute/app` registra `myApp`, dos vistas de ejemplo y un módulo de versión. Sus pruebas son ejemplos de la plantilla, no pruebas de las funciones de Moute. Gradle compila el código de `Moute/app/src`; el panel real vive en `mouteAdmin`.
