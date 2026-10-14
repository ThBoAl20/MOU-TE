import {Tarjeta} from "./Tarjeta";

/**
 * Vista de un usuario que usan las plantillas, no el objeto completo de Firebase.
 * Los componentes aplanan Datos y añaden la clave del nodo como id.
 * Es un contrato de compilación: no valida ni filtra datos recibidos en ejecución.
 * No incluye contraseñas; esas credenciales pertenecen a Firebase Authentication.
 */
export class User {
  /** UID del nodo Usuarios/{uid}; no es el DNI ni un identificador numérico. */
  id: string;
  nombre: string;
  apellido: string;
  telefono: string;
  correo: string;
  genero: string;
  fechaNac: string;
  dni: string;
  created_at?: Date;
  /** Array derivado del mapa listaTarjetas; puede faltar en la vista de listado. */
  listaTarjetas: Tarjeta[];
}
