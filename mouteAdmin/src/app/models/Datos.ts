import {Tarjeta} from "./Tarjeta";

/**
 * Modelo auxiliar del perfil Datos conservado del prototipo original.
 * Las pantallas actuales trabajan con User; este modelo no valida el JSON.
 * id numérico es heredado y no representa el UID string de Firebase Authentication.
 */
export class Datos {
  id?: number;
  nombre?: string;
  apellido?: string;
  telefono?: string;
  correo?: string;
  genero?: string;
  fechaNac: string;
  dni?: string;
  created_at?: Date;
}
