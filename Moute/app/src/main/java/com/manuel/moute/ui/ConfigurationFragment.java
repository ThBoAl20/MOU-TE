package com.manuel.moute.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.manuel.moute.MainActivity;
import com.manuel.moute.R;
import com.manuel.moute.models.Usuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;


/**
 * Resumen de la cuenta y accesos a editar perfil, eliminar cuenta y cerrar sesión.
 *
 * <p>Los datos personales proceden de Realtime Database y la foto de Firebase Auth.
 * El menú principal reserva esta pantalla para usuarios autenticados; los accesos
 * navegan a otros fragmentos y el cierre de sesión se delega en MainActivity.</p>
 */
public class ConfigurationFragment extends Fragment {
    private View root;

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser currentUser;
    private MainActivity mainActivity;
    private Map<String, Usuario> mapUser;
    private ArrayList<Usuario> listaUsuarios;
    private ImageView imageView;

    private TextView txtNombre, txtEmail, txtTelefono;
    private LinearLayout editarPefil, eliminarCuenta, cerrarSesion;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       root = inflater.inflate(R.layout.fragment_configuration, container, false);
       txtNombre = root.findViewById(R.id.txtNameCompl);
       txtEmail = root.findViewById(R.id.txtEmailCompl);
       txtTelefono = root.findViewById(R.id.txtPhoneNumberCompl);
       editarPefil = root.findViewById(R.id.lnLayoutEditProfile);
       eliminarCuenta = root.findViewById(R.id.lnLayoutDelete);
       cerrarSesion = root.findViewById(R.id.cerrarSesionLnLayout);
       editarPefil = root.findViewById(R.id.lnLayoutEditProfile);
       imageView = root.findViewById(R.id.imgConfig);
       listaUsuarios = new ArrayList<>();
       reference = FirebaseDatabase.getInstance().getReference("Usuarios");
       auth = FirebaseAuth.getInstance();
       currentUser = auth.getCurrentUser();
       userData();
       getImagenProfile();
       editarPefil.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Navigation.findNavController(v).navigate(R.id.profileFragment);
           }
       });
       eliminarCuenta.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Navigation.findNavController(v).navigate(R.id.nav_deleteAcoutn);
           }
       });

       cerrarSesion.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                mainActivity.logOut();
           }
       });
       return root;
    }

    /** Escucha el perfil y sustituye la lista en cada evento antes de actualizar los textos. */
    public void userData(){
        if (currentUser != null){
            final FirebaseUser u;
            u = auth.getCurrentUser();
            reference.child(u.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<Map<String, Usuario>> gti = new GenericTypeIndicator<Map<String, Usuario>>(){};
                    mapUser = dataSnapshot.getValue(gti);
                    listaUsuarios.clear();
                    if (mapUser != null){
                        listaUsuarios.addAll(mapUser.values());
                    }
                    for (int i = 0; i < listaUsuarios.size(); i++) {
                        if (u.getEmail().equalsIgnoreCase(listaUsuarios.get(i).getCorreo())){
                            txtNombre.setText(listaUsuarios.get(i).getNombre()+" "+listaUsuarios.get(i).getApellido());
                            txtEmail.setText(listaUsuarios.get(i).getCorreo());
                            txtTelefono.setText(listaUsuarios.get(i).getTelefono());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            txtNombre.setText("Nombre completo");
            txtEmail.setText("Correo electrónico");
            txtTelefono.setText("Numero de teléfono");
        }

    }
    /** Obtiene el anfitrión responsable del cierre de sesión y de su cabecera. */
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
    /** Carga la foto si existe; presupone la sesión que exige el acceso a esta pantalla. */
    public void getImagenProfile(){
        FirebaseUser user;
        user = auth.getCurrentUser();
        if (user.getPhotoUrl() != null){
            Picasso.with(getActivity()).load(user.getPhotoUrl()).into(imageView);
        }
    }

}
