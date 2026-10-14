import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';

// Adaptadores AngularFire para el mismo backend que utiliza la aplicación Android.
import {AngularFireDatabaseModule} from 'angularfire2/database'
import {AngularFireAuthModule, AngularFireAuth} from 'angularfire2/auth'
import {AngularFireModule} from 'angularfire2'


import {environment} from "../environments/environment";
import {UserFormComponent} from './components/user-form/user-form.component';
import {UserListComponent} from './components/user-list/user-list.component';
import {NavigationComponent} from './components/navigation/navigation.component';
import {UserService} from "./services/user.service";
import {VerUsuarioComponent} from "./components/ver-usuario/ver-usuario.component";
import {FormsModule} from "@angular/forms";
import {SolicitudTarjetaComponent} from "./components/solicitud-tarjeta/solicitud-tarjeta.component";


/**
 * Ensambla componentes, rutas, formularios y conexión con Firebase.
 * AngularFireAuth se importa, pero este panel no implementa una pantalla de login.
 * No debe publicarse como un panel administrativo abierto a datos personales.
 */
@NgModule({
  declarations: [
    AppComponent,
    UserFormComponent,
    UserListComponent,
    NavigationComponent,
    VerUsuarioComponent,
    SolicitudTarjetaComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    // Angular CLI sustituye environment por su variante de producción al compilar.
    AngularFireModule.initializeApp(environment.firebaseConfig),
    AngularFireDatabaseModule,
    AngularFireAuthModule,
    FormsModule,
  ],
  providers: [
    UserService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
