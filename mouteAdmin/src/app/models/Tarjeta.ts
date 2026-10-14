/**
 * Campos de las tarjetas remotas que muestra la ficha de usuario.
 * La clave del mapa de Firebase es el número de tarjeta, no un ID de pago.
 * Los campos reflejan el prototipo Android; no prueban una compra ni un envío real.
 */
export class Tarjeta {
  nombre: string;
  apellidos: string;
  dni: string;
  numCard: string;
  titulo: string;
  /** Dirección consignada por el usuario; no implica que exista un envío tramitado. */
  direccionDeEnvio: string;
  /** Texto guardado por Android, no un objeto Date normalizado por el panel. */
  fechaContrato: string
}
