/**
 * Prueba básica de construcción; no cubre enlaces ni permisos de navegación.
 * Para probar routerLink de la plantilla debe añadirse RouterTestingModule.
 */
import {async, ComponentFixture, TestBed} from '@angular/core/testing';

import {NavigationComponent} from './navigation.component';

describe('NavigationComponent', () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [NavigationComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
