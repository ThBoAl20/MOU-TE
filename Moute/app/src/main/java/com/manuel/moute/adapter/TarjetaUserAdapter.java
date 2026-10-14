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
import com.manuel.moute.R;
import com.manuel.moute.models.TarjetaUser;
import com.manuel.moute.models.GuestCardStore;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Presenta las tarjetas de «Mis tarjetas», tanto locales como de la cuenta.
 * Al abrir una fila envía sus datos y marca si el identificador existe en
 * GuestCardStore; VerTarjetaFragment usa esa marca para elegir la persistencia.
 * Este adaptador no modifica ni elimina las tarjetas.
 */
public class TarjetaUserAdapter extends RecyclerView.Adapter<TarjetaUserAdapter.Card> {
    private Context contextç;
    private ArrayList <TarjetaUser> tarjetasUser;
    private int resource;

    /** Recibe del fragmento la lista ya preparada y el layout de cada fila. */
    public TarjetaUserAdapter(Context context, int resource, ArrayList<TarjetaUser> tarjetasUser) {
        this.contextç = context;
        this.tarjetasUser = tarjetasUser;
        this.resource = resource;

    }

    /** Infla una fila; el ViewHolder mantiene sus controles para reutilizarlos. */
    @NonNull
    @Override
    public TarjetaUserAdapter.Card onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contextç).inflate(resource, null);
        Card card = new Card(v);
        return card;
    }

    /** Rellena los datos de la tarjeta; la moneda depende de la región del dispositivo. */
    @Override
    public void onBindViewHolder(@NonNull TarjetaUserAdapter.Card holder, int position) {
        final TarjetaUser t = tarjetasUser.get(position);
        holder.lblNombre.setText(t.getNombre());
        holder.lblApellidos.setText(t.getApellidos());
        holder.lblDni.setText(t.getDni());
        holder.lblTItulo.setText(t.getTitulo());
        holder.lblNumCard.setText(t.getNumCard());
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
        holder.lblPrecio.setText(numberFormat.format(t.getPrecio()));
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // El modelo viaja por Parcelable; su origen local se identifica aparte.
                Bundle bundle = new Bundle();
                bundle.putParcelable("cardUser", t);
                bundle.putBoolean("guestCard", new GuestCardStore(v.getContext()).contains(t.getNumCard()));
                Navigation.findNavController(v).navigate(R.id.nav_ver_tarjeta, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tarjetasUser.size();
    }

    /** Controles de una fila de tarjeta contratada. */
    public class Card extends RecyclerView.ViewHolder {

        private TextView lblNombre, lblApellidos, lblDni, lblNumCard, lblTItulo, lblPrecio;
        private CardView card;
        public Card(@NonNull View itemView) {
            super(itemView);
            lblNombre = itemView.findViewById(R.id.txtNombreCardListaUser);
            lblApellidos = itemView.findViewById(R.id.txtApellidoCardListaUser);
            lblDni = itemView.findViewById(R.id.txtDniCardListaUser);
            lblNumCard = itemView.findViewById(R.id.txtNumCardListaUser);
            lblTItulo = itemView.findViewById(R.id.lblTipoCardListaUser);
            lblPrecio = itemView.findViewById(R.id.lblPrecioCardListUser);
            card = itemView.findViewById(R.id.cardView);


        }
    }
}
