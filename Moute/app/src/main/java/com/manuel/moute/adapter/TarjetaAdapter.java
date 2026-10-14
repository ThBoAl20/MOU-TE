package com.manuel.moute.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

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
import com.manuel.moute.models.Tarjeta;
import com.manuel.moute.models.Usuario;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * Presenta las tarjetas disponibles para contratar y abre su configuración.
 *
 * <p>La lista procede de ContratarFragment. El titular se obtiene del perfil de
 * Firebase cuando hay sesión, o se muestra como «Invitado» en la demostración
 * local. No crea contratos ni inicia pagos: esa responsabilidad es del destino
 * nav_tipoCard, al que envía el objeto Tarjeta como Parcelable.</p>
 */
public class TarjetaAdapter extends RecyclerView.Adapter<TarjetaAdapter.Card> {

    private Context context;
    private ArrayList<Tarjeta> listaTarjeta;
    private int resource;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference reference;
    private Map<String, Usuario> mapUser;

    private String nombre, apellidos, dni, numCard;
    private ArrayList<Usuario> listaUsuarios;

    /** Constructor heredado; la pantalla usa el que recibe contexto, layout y lista. */
    public TarjetaAdapter(){

    }

    /** Conserva la lista del catálogo y comienza la carga asíncrona del titular. */
    public TarjetaAdapter(Context context, int resource, ArrayList<Tarjeta> listaTarjeta) {
        this.context = context;
        this.listaTarjeta = listaTarjeta;
        this.resource = resource;


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Usuarios");
        listaUsuarios = new ArrayList<>();
        userData();
    }

    /** Crea una fila utilizando el recurso de tarjeta elegido por el fragmento. */
    @NonNull
    @Override
    public Card onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, null);
        Card card = new Card(view);
        return  card;
    }

    /**
     * Muestra producto, precio y titular disponibles al enlazar la fila. La moneda
     * sigue la configuración regional del dispositivo (NumberFormat).
     */
    @Override
    public void onBindViewHolder(@NonNull final TarjetaAdapter.Card holder, int position) {
        final Tarjeta tarjeta = listaTarjeta.get(position);

        holder.lblNombre.setText(nombre);
        holder.lblApellidos.setText(apellidos);
        holder.lblDniUser.setText(dni);
        holder.lblNumCard.setText("Nº Tarjeta");
        holder.lblTitulo.setText(tarjeta.getTitulo());
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        holder.lblPrecio.setText(numberFormat.format(tarjeta.getPrecio()));

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // «tarjeta» es el contrato de argumentos de TypeOfCardFragment.
                Bundle bundle = new Bundle();
                bundle.putParcelable("tarjeta", tarjeta);
                Navigation.findNavController(v).navigate(R.id.nav_tipoCard, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaTarjeta.size();
    }

    /** Referencias reutilizables a los controles de una fila del RecyclerView. */
    public class Card extends RecyclerView.ViewHolder {

        private TextView lblNombre, lblApellidos, lblNumCard, lblDniUser, lblPrecio, lblTitulo;
        private CardView card;

        public Card(@NonNull View itemView) {

            super(itemView);
            lblNombre = itemView.findViewById(R.id.txtNombreCardUser);
            lblApellidos = itemView.findViewById(R.id.txtApellidoCardUser);
            lblDniUser = itemView.findViewById(R.id.txtDniCardUser);
            lblTitulo = itemView.findViewById(R.id.lblTipoCardUser);
            lblNumCard = itemView.findViewById(R.id.txtNumCardUser);
            lblPrecio = itemView.findViewById(R.id.lblPrecioCard);
            card = itemView.findViewById(R.id.card);
        }
    }

    /**
     * Escucha Usuarios/{uid}, cuya estructura heredada contiene perfiles hijos
     * (por ejemplo Datos), y selecciona el perfil por correo. Cada respuesta
     * reemplaza la lista temporal para no acumular duplicados.
     *
     * <p>El callback actualiza las variables de titular; no fuerza un nuevo bind
     * de las filas ya visibles. El listener queda asociado a Firebase, no a un
     * ciclo de vida propio del adaptador.</p>
     */
    private void userData(){
        if (currentUser != null){
            final FirebaseUser u;
            u = auth.getCurrentUser();
            reference.child(u.getUid()).addValueEventListener(new ValueEventListener() {
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
                           nombre = listaUsuarios.get(i).getNombre();
                           apellidos = listaUsuarios.get(i).getApellido();
                           dni = listaUsuarios.get(i).getDni();
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            if ( currentUser == null){
                cardDefault();
            }
        }
    }

    /** Datos neutrales para previsualizar el catálogo sin una cuenta. */
    private void cardDefault(){
        nombre = "Invitado";
        apellidos = "";
        dni = "";
        numCard = "Nº de tarjeta";
    }


}
