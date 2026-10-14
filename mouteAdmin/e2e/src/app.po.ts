import { browser, by, element } from 'protractor';

/**
 * Page object del ejemplo inicial de Angular CLI.
 * Centraliza la navegación y el selector de bienvenida para los escenarios e2e.
 * Ese selector ya no existe en la interfaz de Moute: debe adaptarse antes de
 * considerar esta prueba una comprobación del panel actual.
 */
export class AppPage {
  navigateTo() {
    return browser.get(browser.baseUrl) as Promise<any>;
  }

  /** Selector heredado, no el título visible de la barra MOUTE. */
  getTitleText() {
    return element(by.css('app-root .content span')).getText() as Promise<string>;
  }
}
