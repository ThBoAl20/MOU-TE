import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user.service";

/**
 * Esqueleto de formulario heredado del prototipo.
 * La ruta games/edit/:id lo muestra, pero no hay campos ni operación de guardado.
 */
@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styleUrls: ['./user-form.component.css']
})
export class UserFormComponent implements OnInit {

  constructor(private userService: UserService) {
  }

  ngOnInit() {
  }

  /** Punto reservado para el envío; actualmente no modifica ningún usuario. */
  onSubmit() {

  }

}
