import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {UserListComponent} from "./components/user-list/user-list.component";
import {UserFormComponent} from "./components/user-form/user-form.component";
import {VerUsuarioComponent} from "./components/ver-usuario/ver-usuario.component";
import {SolicitudTarjetaComponent} from "./components/solicitud-tarjeta/solicitud-tarjeta.component";


// Rutas del prototipo: listado -> detalle por UID de Firebase, y vista de tarjetas.
// No hay guardas de autenticación/autorización: las reglas de Firebase deben
// restringir los datos. Una ruta visible no equivale a un permiso de administrador.
const routes: Routes = [
  {
    path: '',
    redirectTo: '/users',
    pathMatch: 'full'
  },
  {
    path: 'users',
    component: UserListComponent
  },
  {
    path: 'verusuarios/:id',
    component: VerUsuarioComponent
  },
  {
    path: 'solicitudes',
    component: SolicitudTarjetaComponent
  },
  {
    // Nombre heredado del ejemplo inicial; el formulario todavía está vacío.
    path: 'games/edit/:id',
    component: UserFormComponent
  }
];

/** Registra las pantallas del panel mediante Angular Router. */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
