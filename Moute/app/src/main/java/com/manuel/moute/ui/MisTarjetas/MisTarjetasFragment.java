package com.manuel.moute.ui.MisTarjetas;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.manuel.moute.R;
import com.manuel.moute.adapter.TarjetaUserAdapter;
import com.manuel.moute.models.TarjetaUser;
import com.manuel.moute.models.Tarjeta;
import com.manuel.moute.models.GuestCardStore;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;

/**
 * Lista las tarjetas disponibles y abre su detalle mediante TarjetaUserAdapter.
 *
 * <p>Siempre incluye las tarjetas invitadas de este dispositivo. Si hay sesión,
 * añade las de Usuarios/{uid}/listaTarjetas. La distinción local/remota se conserva
 * en el adaptador para que el detalle no escriba una tarjeta invitada en Firebase.</p>
 *
 * <p>El código NFC al final es un boceto: no implementa validación de transporte,
 * lectura de tarjetas físicas ni escritura de billetes en una etiqueta.</p>
 */
public class MisTarjetasFragment extends Fragment {
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    LocationManager locationManager;

    private View root;
    //------VARIABLES FIREBASE-----
    private FirebaseDatabase database;
    private DatabaseReference userReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    // VARIABLES RECYCLERVIEW
    private TarjetaUserAdapter adapter;
    private Map<String,TarjetaUser> mapCard;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TarjetaUser> listaTarjetas;
    private ArrayList<Integer> indices;
    private int resource = R.layout.tarjetas_user;
    //
    private TextView txtNombre, txtApellido, txtDni, txtNumCard, txtTitulo, lblTitulo;
    private Button btnTarjeta;
    private static final String TAG = MifareUltralight.class.getSimpleName();

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_mis_tarjetas, container, false);
        indices = new ArrayList<>();
        listaTarjetas = new ArrayList<>();
        indices = new ArrayList<>();
        database = FirebaseDatabase.getInstance();

        txtNombre = root.findViewById(R.id.txtNombreCardListaUser);
        txtApellido = root.findViewById(R.id.txtApellidoCardListaUser);
        txtDni = root.findViewById(R.id.txtDniCardListaUser);
        txtNumCard = root.findViewById(R.id.txtNumCardListaUser);
        txtTitulo = root.findViewById(R.id.lblTipoCardListaUser);
        btnTarjeta = root.findViewById(R.id.btnTarjeta);
        recyclerView = root.findViewById(R.id.rv);

        nfcCodigo();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userReference = database.getReference("Usuarios");

        adapter = new TarjetaUserAdapter(getContext(), resource, listaTarjetas);
        recyclerView.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        btnTarjeta.setText("No has contratado ninguna tarjeta\nContrata una tarjeta");
        btnTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.contratarFragment);
            }
        });
        loadGuestCards();
        updateEmptyState();

        if(auth.getCurrentUser() != null){
            DatabaseReference refCard = userReference.child(user.getUid());
            refCard.child("listaTarjetas").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Firebase puede responder cuando el usuario ya ha salido del fragmento.
                    if (!isAdded()) {
                        return;
                    }
                    GenericTypeIndicator<Map<String,TarjetaUser>> gti = new GenericTypeIndicator<Map<String,TarjetaUser>>() {};
                    mapCard = dataSnapshot.getValue(gti);
                    // Reconstruye la lista local antes de añadir el último estado de la cuenta.
                    loadGuestCards();
                    if (mapCard != null) {
                        btnTarjeta.setVisibility(View.GONE);
                        listaTarjetas.addAll(mapCard.values());
                    }else {
                        btnTarjeta.setVisibility(View.VISIBLE);
                        btnTarjeta.setText("No has contratado ninguna tarjeta\nContrata una tarjeta");
                        btnTarjeta.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Navigation.findNavController(v).navigate(R.id.contratarFragment);
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
        }



        /**
         *Codigo NFC
         */
        return root;
    }

    /** Adapta el modelo local Tarjeta al modelo de presentación TarjetaUser y reinicia la lista. */
    private void loadGuestCards() {
        listaTarjetas.clear();
        for (Tarjeta card : new GuestCardStore(requireContext()).getCards()) {
            TarjetaUser storedCard = new TarjetaUser(card.getNombre(), card.getApellidos(),
                    card.getDni(), card.getNumCard(), card.getTitulo(), card.getTipo(),
                    card.getCantidadViajes(), card.getPrecio(), card.isEstado(),
                    card.getFechaContrato(), null, null);
            listaTarjetas.add(storedCard);
        }
    }

    /** El acceso a contratar solo se muestra como estado vacío cuando no hay ninguna tarjeta. */
    private void updateEmptyState() {
        btnTarjeta.setVisibility(listaTarjetas.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    /**
     * Prepara referencias y un filtro NFC del prototipo, sin activar foreground dispatch.
     * El PendingIntent apunta a getClass() (este Fragment), no a una actividad receptora;
     * no debe interpretarse este boceto como una integración NFC funcional.
     */
    public void nfcCodigo() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        pendingIntent = PendingIntent.getActivity(getContext(), 0, new Intent(getActivity(),
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        }

    }

    /**
     * Construye un registro NDEF de texto de ejemplo. No se invoca en el flujo actual
     * y no escribe el registro en ningún soporte físico.
     */
    private NdefRecord createTicket(String text) throws UnsupportedEncodingException {
        String lang = "us";
        byte[] textBytes = text.getBytes();
        byte[] langBytes = lang.getBytes("US-ASCII");
        int langLength = langBytes.length;
        int textLength = textBytes.length;
        byte[] payLoad = new byte[1 + langLength + textLength];

        payLoad[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payLoad, 1, langLength);
        System.arraycopy(textBytes, 0, payLoad, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payLoad);

        return recordNFC;
    }

}
