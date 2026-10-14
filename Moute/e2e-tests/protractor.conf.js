/* Configuración E2E de la plantilla AngularJS servida en el puerto 8000.
No configura pruebas del APK ni las de mouteAdmin/e2e. */
//jshint strict: false
exports.config = {

  allScriptsTimeout: 11000,

  specs: [
    '*.js'
  ],

  capabilities: {
    browserName: 'chrome'
  },

  baseUrl: 'http://localhost:8000/',

  framework: 'jasmine',

  jasmineNodeOpts: {
    defaultTimeoutInterval: 30000
  }

};
