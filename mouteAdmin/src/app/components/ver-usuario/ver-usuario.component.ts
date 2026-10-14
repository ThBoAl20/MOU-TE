import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Tarjeta } from '../../models/Tarjeta';
import { User } from '../../models/User';
import { UserService } from '../../services/user.service';

/**
 * Ficha de lectura: perfil Datos y tarjetas del usuario indicado por la ruta.
 * Aplana el mapa listaTarjetas en un array para representarlo con ngFor.
 * No activa tarjetas, no realiza pagos y no tramita el envío mostrado.
 */
@Component({
  selector: 'app-ver-usuario',
  templateUrl: './ver-usuario.component.html',
  styleUrls: ['./ver-usuario.component.css']
})
export class VerUsuarioComponent implements OnInit {
  id: string;
  listaCard: Tarjeta[] = [];
  user: User = {} as User;

  constructor(private userService: UserService, private activatedRoute: ActivatedRoute) {}

  /** Vincula el parámetro :id al registro remoto, que puede cambiar en tiempo real. */
  ngOnInit() {
    this.activatedRoute.params.subscribe(params => {
      this.id = params.id;
      if (!this.id) {
        return;
      }

      this.userService.getUser(this.id).snapshotChanges().subscribe(item => {
        const record: any = item.payload.val();
        // El usuario puede haber sido eliminado; vaciar evita mostrar datos antiguos.
        if (!record || !record.Datos) {
          this.user = {} as User;
          this.listaCard = [];
          return;
        }

        // Realtime Database devuelve un mapa por número de tarjeta, no un array.
        this.listaCard = record.listaTarjetas
          ? Object.keys(record.listaTarjetas).map(key => record.listaTarjetas[key] as Tarjeta)
          : [];
        this.user = { ...record.Datos, id: item.key, listaTarjetas: this.listaCard };
      });
    });
  }
}
