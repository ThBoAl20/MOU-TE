package com.manuel.moute;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.manuel.moute.Loandings.LoandigDialog;
import com.manuel.moute.Loandings.LoginLoading;
import com.manuel.moute.models.Usuario;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Map;

/**
 * Contenedor principal de las pantallas de Moute y del menú lateral.
 *
 * <p>Android Navigation decide qué fragmento se muestra. Esta actividad enlaza
 * la barra superior y el menú, presenta el perfil de Firebase en la cabecera y
 * permite cerrar la sesión. Contratar y Mis tarjetas permanecen disponibles
 * sin cuenta para la demostración local; ocultar opciones no sustituye las
 * reglas de seguridad de Firebase.</p>
 */
public class MainActivity extends AppCompatActivity {

    private static final long MIN_TIME = 10000;
    private static final int CODE_CAMERA = 21;
    private static final int CODE_GALLERY = 22;
    public
    // Campos heredados de la presentación inicial; el arranque animado está en SplashScreen.
    Animation topAnim, bottomAni;
    private ImageView imagen;

    // Perfil compartido por el lector alternativo UpdateNavHeader2; no contiene credenciales.
    public static Usuario u;
    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference reference;
    private FirebaseDatabase database;
    private Map<String, Usuario> mapUser;
    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<String> indices;
    private View v;
    private NavigationView navigationView;
    private TextView txtNombre, txtEmail, txMensaje, lblNombreGetCard;
    private Menu menuAt;
    private DrawerLayout drawer;

    private String permisos[];
    private final int PERMISOS_INICIALES = 1;


    /**
     * Configura permisos, Navigation y cabecera al crear la actividad. La sesión
     * se consulta antes de leer el perfil; el contenido de Firebase llega después
     * mediante callbacks, no como resultado inmediato de getReference.
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Desde Android 6, cámara y ubicación requieren aprobación en tiempo de ejecución.
        permisos = new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION};

        ArrayList<String>permisosDenegados = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permiso : permisos) {
                if (checkSelfPermission(permiso) == PackageManager.PERMISSION_DENIED) {
                    permisosDenegados.add(permiso);
                }
            }
            if (permisosDenegados.size() > 0) {
                requestPermissions(permisosDenegados.toArray(new String[permisosDenegados.size()]),
                        PERMISOS_INICIALES);
            }
        }

        listaUsuarios = new ArrayList<>();
        indices = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Usuarios");
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Estos destinos se consideran pantallas principales del menú lateral.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_tarjetas, R.id.loginFragment, R.id.contratarFragment,
                R.id.nav_conocenos, R.id.profileFragment, R.id.nav_acercaDenosotros, R.id.nav_configuration)
                .setDrawerLayout(drawer)
                .build();
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Mostrar el Nombre y el Email del usuario en el nav_header
        View v = navigationView.getHeaderView(0);
        txtEmail = v.findViewById(R.id.lblEmailPerfil);
        txtNombre = v.findViewById(R.id.lblNombrePerfil);
        imagen = v.findViewById(R.id.imageUserProfile);
        imagen.setImageResource(R.drawable.miperfil);
        if (auth.getCurrentUser() != null) {
            if (currentUser.getPhotoUrl() != null){
            }
            UpdateNavHeader();
            imageProfile();
        }
        logOutCondition();
        // Código para cerrar sesión desde el menu
        menuAt = navigationView.getMenu();
        menuAt.findItem(R.id.nav_logOut).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_logOut:
                        logOut();
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /** Añade las acciones definidas en res/menu/main.xml a la barra superior. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** Delega la flecha superior en Navigation y en la configuración del menú lateral. */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Lector alternativo heredado para un Usuario guardado directamente bajo
     * Usuarios/{uid}. El registro actual guarda el perfil en el hijo Datos,
     * por lo que el flujo de onCreate utiliza UpdateNavHeader en su lugar.
     */
    public void UpdateNavHeader2() {
        if (auth.getCurrentUser() != null) {
            final FirebaseUser user;
            user = auth.getCurrentUser();
            reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<Usuario> gti = new GenericTypeIndicator<Usuario>() {
                    };
                    u = dataSnapshot.getValue(gti);
                    if (u != null) {
                        txtNombre.setText(u.getNombre());
                        txtEmail.setText(u.getCorreo());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    /**
     * Observa los perfiles hijos de Usuarios/{uid} y muestra el que coincide con
     * el correo de la sesión. Requiere una cuenta activa al llamarlo. El listener
     * es continuo; esta implementación no lo retira explícitamente al destruir
     * la actividad.
     */
    public void UpdateNavHeader() {
            final FirebaseUser user;
            user = auth.getCurrentUser();
            reference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<Map<String, Usuario>> gti = new GenericTypeIndicator<Map<String, Usuario>>() {
                    };
                    mapUser = dataSnapshot.getValue(gti);
                    listaUsuarios.clear();
                    if (mapUser != null) {
                        listaUsuarios.addAll(mapUser.values());
                    }
                    for (int i = 0; i < listaUsuarios.size(); i++) {
                        if (user.getEmail().equalsIgnoreCase(listaUsuarios.get(i).getCorreo())) {
                            txtNombre.setText(listaUsuarios.get(i).getNombre());
                            txtEmail.setText(listaUsuarios.get(i).getCorreo());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    /** Restablece los textos de la cabecera para el modo sin sesión. */
    public void restarNavHeader() {
        txtNombre.setText("Bienvenido a Moute");
        txtEmail.setText("inicia sesión");
    }

    /**
     * Adapta la visibilidad del menú a la sesión actual. Las pantallas personales
     * se ocultan al invitado, pero la contratación y las tarjetas locales siguen
     * accesibles. Este método solo afecta a la interfaz, no a permisos de servidor.
     */
    public void logOutCondition() {
        final Menu menu;
        menu = navigationView.getMenu();
        if (auth.getCurrentUser() == null) {
            menu.findItem(R.id.profileFragment).setVisible(false);
            menu.findItem(R.id.nav_register).setVisible(true);
            menu.findItem(R.id.loginFragment).setVisible(true);
            menu.findItem(R.id.nav_logOut).setVisible(false);
            menu.findItem(R.id.nav_tarjetas).setVisible(true);
            menu.findItem(R.id.contratarFragment).setVisible(true);
            menu.findItem(R.id.nav_configuration).setVisible(false);

        } else if (auth.getCurrentUser() != null) {
            menu.findItem(R.id.profileFragment).setVisible(true);
            menu.findItem(R.id.nav_register).setVisible(false);
            menu.findItem(R.id.loginFragment).setVisible(false);
            menu.findItem(R.id.nav_logOut).setVisible(true);
            menu.findItem(R.id.nav_tarjetas).setVisible(true);
            menu.findItem(R.id.contratarFragment).setVisible(true);
            menu.findItem(R.id.nav_configuration).setVisible(true);
        }

    }

    /** Primero cierra el cajón lateral; si está cerrado, aplica Atrás normalmente. */
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Pide confirmación antes de cerrar la sesión de Firebase. No elimina la
     * cuenta, las tarjetas de Firebase ni las tarjetas locales de GuestCardStore.
     */
    public void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.log_out, null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        builder.create().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnLogOut = view.findViewById(R.id.btnLogOutAlert);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                restarNavHeader();
                logOutCondition();
                onBackPressed();
                Toast.makeText(MainActivity.this, "Ha cerrado sesion correctamente", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Abre Google Maps mediante un intent externo si está instalado. No incluye
     * un mapa propio ni un destino concreto; sin esa aplicación no abre una vista.
     */
    public void goToMap(){
        Uri gmmIntentUri = Uri.parse("geo:0,0");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    /** Carga con Picasso la foto de la cuenta activa cuando tiene una URL de perfil. */
    public void imageProfile(){
        View v = navigationView.getHeaderView(0);
        imagen = v.findViewById(R.id.imageUserProfile);
        FirebaseUser user;
        user = auth.getCurrentUser();
        if (user.getPhotoUrl() != null){
            Picasso.with(MainActivity.this).load(user.getPhotoUrl()).into(imagen);
        }
    }

    /**
     * Ejemplo heredado de reautenticación, invocado desde ProfileFragment.
     * Las credenciales fijas son de muestra: no son las del usuario actual.
     * Este método no guarda cambios de perfil y su mensaje no comprueba si la
     * tarea tuvo éxito; no debe confundirse con una actualización funcional.
     */
    public void updateUser(){
        FirebaseUser user;
        user = auth.getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential("user@example.com", "password1234");
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, " Los datos se han actualizado correctamente", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /** Animación de espera de cinco segundos; no espera ni verifica una tarea remota. */
    public void progresbar(){
        final LoandigDialog loandigDialog = new LoandigDialog(MainActivity.this);
        loandigDialog.starLoading();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loandigDialog.dissmisDialog();
            }
        }, 5000);
    }

    /** Variante visual para login, también limitada a un temporizador de cinco segundos. */
    public void progresbarLogin(){
        final LoginLoading loginLoading = new LoginLoading(MainActivity.this);
        loginLoading.starLoading();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loginLoading.dissmisDialog();
            }
        }, 5000);
    }








}
