import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../models/User';
import { UserService } from '../../services/user.service';

/**
 * Lista perfiles remotos y abre su ficha conservando el UID de Firebase.
 * La lectura es continua: la interfaz se reconstruye al recibir cada snapshot.
 * Requiere permisos de lectura adecuados; aquí no se solicita una sesión de admin.
 */
@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {
  usersList: User[] = [];

  constructor(private userService: UserService, private router: Router) {}

  /** Convierte Usuarios/{uid}/Datos en filas de la tabla. */
  ngOnInit() {
    this.userService.getUsers().snapshotChanges().subscribe(items => {
      // Firebase emite el conjunto completo en cada cambio: reconstruir evita duplicados.
      this.usersList = items.reduce((users: User[], item) => {
        const record: any = item.payload.val();
        if (record && record.Datos) {
          // Añadir el UID permite navegar al nodo original, sin buscar por correo.
          users.push({ ...record.Datos, id: item.key });
        }
        return users;
      }, []);
    });
  }

  /** Transfiere únicamente el UID; la siguiente pantalla vuelve a leer su registro. */
  enviarDatos(idUser: string) {
    this.router.navigate(['/verusuarios', idUser]);
  }
}
