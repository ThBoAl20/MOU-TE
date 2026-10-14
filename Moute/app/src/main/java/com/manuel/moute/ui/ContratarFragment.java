package com.manuel.moute.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.manuel.moute.adapter.TarjetaAdapter;
import com.manuel.moute.models.Tarjeta;
import com.manuel.moute.models.Usuario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Presenta el catálogo desde el que se elige una tarjeta para configurar y contratar.
 *
 * <p>Con sesión escucha el nodo Tarjetas de Realtime Database y personaliza la
 * cabecera con el perfil del usuario. Sin sesión utiliza el catálogo local mínimo
 * Bus, Cercanias y Metro, sin leer datos remotos. TarjetaAdapter lleva la selección
 * a TypeOfCardFragment para definir producto y precio.</p>
 */
public class ContratarFragment extends Fragment {
    private View root;

    private FirebaseDatabase database;
    private DatabaseReference refCard, refUser;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private ViewGroup containerView;

    // VARIABLES RECYCLERVIEW
    private TarjetaAdapter adapter;
    private Map<String, Tarjeta> mapCard;
    private Map<String, Usuario> mapUser;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Tarjeta> listaTarjetas;
    private ArrayList<Usuario> listaUsuarios;
    private ArrayList<String> indices;
    private int resource = R.layout.tarjeta_turista;
    private TextView lblNombre, lblApellidos, lblDniUser, lblNombreGetCard;
    private String nombre, apellidos, dni;

    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_contratar, container, false);
        lblNombre = root.findViewById(R.id.txtNombreCardListaUser);
        lblApellidos = root.findViewById(R.id.txtApellidoCardListaUser);
        lblDniUser = root.findViewById(R.id.txtDniCardListaUser);
        lblNombreGetCard = root.findViewById(R.id.lblNameUserGetCard);
        imageView = root.findViewById(R.id.imgContratar);


        recyclerView = root.findViewById(R.id.rvContratar);
        indices = new ArrayList<>();
        listaTarjetas = new ArrayList<>();
        listaUsuarios = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        refCard = database.getReference("Tarjetas");
        refUser = database.getReference("Usuarios");

        adapter = new TarjetaAdapter(getContext(), resource, listaTarjetas);
        recyclerView.setAdapter(adapter);
        userData();
        getImagenProfile();
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        // El modo invitado debe poder mostrar un catálogo sin autenticarse en Firebase.
        if (currentUser == null) {
            for (String title : new String[]{"Bus", "Cercanias", "Metro"}) {
                Tarjeta card = new Tarjeta();
                card.setTitulo(title);
                listaTarjetas.add(card);
            }
            adapter.notifyDataSetChanged();
            return root;
        }
        refCard.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Tarjeta>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Tarjeta>>() {};
                mapCard = dataSnapshot.getValue(genericTypeIndicator);
                // Cada evento reemplaza el catálogo para no acumular duplicados del anterior.
                listaTarjetas.clear();
                if (mapCard != null){
                    listaTarjetas.addAll(mapCard.values());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }

    /** Escucha el perfil de la cuenta para mostrar su nombre; oculta esa cabecera al invitado. */
    public void userData(){
        if (currentUser != null){
            lblNombreGetCard.setVisibility(View.VISIBLE);
            final FirebaseUser u;
            u = auth.getCurrentUser();
            refUser.child(u.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator <Map<String, Usuario>> gti = new GenericTypeIndicator<Map<String, Usuario>>(){};
                    mapUser = dataSnapshot.getValue(gti);
                    listaUsuarios.clear();
                    if (mapUser != null){
                        listaUsuarios.addAll(mapUser.values());
                    }
                    for (int i = 0; i < listaUsuarios.size(); i++) {
                        if (u.getEmail().equalsIgnoreCase(listaUsuarios.get(i).getCorreo())){
                           lblNombreGetCard.setText(listaUsuarios.get(i).getNombre());
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else  if (currentUser == null){
            lblNombreGetCard.setText(null);
            lblNombreGetCard.setVisibility(View.GONE);
        }
    }

    /** Carga la foto si Auth tiene una URL; una cuenta sin foto conserva la imagen del layout. */
    public void getImagenProfile(){
        FirebaseUser user;
        user = auth.getCurrentUser();
        if (user != null && user.getPhotoUrl() != null){
            Picasso.with(getActivity()).load(user.getPhotoUrl()).into(imageView);
        }
    }



}
