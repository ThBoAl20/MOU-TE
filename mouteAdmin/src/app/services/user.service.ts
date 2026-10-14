import {Injectable} from '@angular/core';
import {AngularFireDatabase, AngularFireList, AngularFireObject} from 'angularfire2/database'
import {User} from "../models/User";

/**
 * Punto de acceso de lectura a Realtime Database para las pantallas del panel.
 * Estructura compartida con Android: Usuarios/{uid}/Datos y listaTarjetas.
 * Devuelve referencias observables; los componentes transforman cada snapshot.
 * Los permisos se resuelven en Firebase, no mediante estos tipos de TypeScript.
 */
@Injectable({
  providedIn: 'root'
})

export class UserService {
  userList: AngularFireList<any>
  user: AngularFireObject<any>
  constructor(private firebase: AngularFireDatabase) {
  }

  /** Referencia a todos los registros; snapshotChanges conserva sus claves UID. */
  getUsers() {
    return this.userList = this.firebase.list('Usuarios') as AngularFireList<User[]>;
  }
  /** Referencia al usuario cuyo UID llega por la ruta /verusuarios/:id. */
  getUser(id: string) {
    return this.user = this.firebase.object('Usuarios/' + id) as AngularFireObject<any>;
  }
}
