/**
 * Esqueleto de prueba de creación de la ficha.
 * Necesita dobles de UserService y ActivatedRoute para sus suscripciones.
 * No cubre perfiles eliminados, tarjetas ni cambios de parámetro de ruta.
 */
import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {VerUsuarioComponent} from './ver-usuario.component';

describe('VerUsuarioComponent', () => {
  let component: VerUsuarioComponent;
  let fixture: ComponentFixture<VerUsuarioComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [VerUsuarioComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VerUsuarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
