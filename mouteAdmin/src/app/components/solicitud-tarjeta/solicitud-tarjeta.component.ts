import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../models/User';
import { UserService } from '../../services/user.service';

/**
 * Vista heredada llamada "solicitudes": muestra usuarios con listaTarjetas.
 * No consulta una cola de solicitudes físicas ni distingue envíos pendientes.
 * Las tarjetas locales de invitado de Android no se sincronizan con este panel.
 */
@Component({
  selector: 'app-solicitud-tarjeta',
  templateUrl: './solicitud-tarjeta.component.html',
  styleUrls: ['./solicitud-tarjeta.component.css']
})
export class SolicitudTarjetaComponent implements OnInit {
  usersList: User[] = [];

  constructor(private userService: UserService, private router: Router) {}

  /** Filtra registros con perfil y nodo de tarjetas, no por estado de envío. */
  ngOnInit() {
    this.userService.getUsers().snapshotChanges().subscribe(items => {
      // Cada emisión sustituye el listado anterior para evitar filas duplicadas.
      this.usersList = items.reduce((users: User[], item) => {
        const record: any = item.payload.val();
        if (record && record.Datos && record.listaTarjetas) {
          users.push({ ...record.Datos, id: item.key });
        }
        return users;
      }, []);
    });
  }

  /** La consulta de "solicitud" reutiliza la ficha general del usuario. */
  enviarDatos(idUser: string) {
    this.router.navigate(['/verusuarios', idUser]);
  }
}
