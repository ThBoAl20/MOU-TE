import { AppPage } from './app.po';
import { browser, logging } from 'protractor';

// Escenario de ejemplo conservado: aún espera la bienvenida original de Angular.
// No verifica el listado ni Firebase, y no se presenta como una prueba superada.
describe('workspace-project App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should display welcome message', () => {
    page.navigateTo();
    expect(page.getTitleText()).toEqual('mouteAdmin app is running!');
  });

  afterEach(async () => {
    // Comprueba además que Chrome no haya registrado errores de nivel SEVERE.
    // Assert that there are no errors emitted from the browser
    const logs = await browser.manage().logs().get(logging.Type.BROWSER);
    expect(logs).not.toContain(jasmine.objectContaining({
      level: logging.Level.SEVERE,
    } as logging.Entry));
  });
});
