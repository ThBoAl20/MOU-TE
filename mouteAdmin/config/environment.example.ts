/**
 * Plantilla sin credenciales del autor para una copia pública del panel.
 * Copiar a src/environments/environment.ts y environment.prod.ts.
 * En la variante prod poner production: true y usar un proyecto de pruebas propio.
 * Esta configuración de cliente no sustituye las reglas de acceso de Firebase.
 */
export const environment = {
  production: false,
  firebaseConfig: {
    apiKey: 'YOUR_FIREBASE_CLIENT_API_KEY',
    authDomain: 'YOUR_PROJECT.firebaseapp.com',
    databaseURL: 'YOUR_REALTIME_DATABASE_URL',
    projectId: 'YOUR_PROJECT',
    storageBucket: 'YOUR_STORAGE_BUCKET',
    messagingSenderId: 'YOUR_SENDER_ID',
    appId: 'YOUR_APP_ID'
  }
};
