/**
 * Esqueleto de prueba generado por Angular CLI.
 * Requiere dobles de UserService y Router para ejecutar el componente aislado.
 * No verifica solicitudes físicas ni realiza operaciones reales en Firebase.
 */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SolicitudTarjetaComponent } from './solicitud-tarjeta.component';

describe('SolicitudTarjetaComponent', () => {
  let component: SolicitudTarjetaComponent;
  let fixture: ComponentFixture<SolicitudTarjetaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SolicitudTarjetaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SolicitudTarjetaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
