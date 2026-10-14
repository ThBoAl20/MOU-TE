/**
 * Esqueleto de prueba de creación del formulario reservado.
 * Requiere proporcionar un doble de UserService; no prueba ningún guardado,
 * porque onSubmit todavía no tiene implementación.
 */
import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {UserFormComponent} from './user-form.component';

describe('UserFormComponent', () => {
  let component: UserFormComponent;
  let fixture: ComponentFixture<UserFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [UserFormComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
