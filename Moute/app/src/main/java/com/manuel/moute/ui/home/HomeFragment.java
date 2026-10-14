package com.manuel.moute.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manuel.moute.MainActivity;
import com.manuel.moute.R;

/**
 * Inicio de Moute: accesos a contratación, tarjetas y un MapView de Google Maps.
 *
 * <p>Contratar y ver tarjetas funcionan sin sesión gracias al modo invitado. La
 * pantalla reenvía su ciclo de vida al MapView y solo activa la capa de ubicación
 * si Android ya ha concedido alguno de los permisos de localización.</p>
 *
 * <p>El seguimiento GPS inferior es código experimental, no conectado desde
 * onMapReady: el mapa actual comienza con el marcador de ejemplo en (0, 0).</p>
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private Button btnContratar, btnMyCards, btnGoToMap;
    private View root;
    FirebaseAuth auth;
    FirebaseUser user;
    private MainActivity mainActivity;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    //Google Maps
    private double lat, lon;
    private Location location;
    private Marker marcador;
    private GoogleMap map;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        auth = FirebaseAuth.getInstance();
        btnContratar = root.findViewById(R.id.btnGoContratar);
        btnMyCards = root.findViewById(R.id.btnGoMisTarjetas);
        btnGoToMap = root.findViewById(R.id.btnGoToMap);
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        btnGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.goToMap();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapView);

        user = auth.getCurrentUser();
        // Las dos acciones permanecen disponibles para la demostración local sin login.
        btnContratar.setEnabled(true);
        btnMyCards.setEnabled(true);
        btnContratar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.contratarFragment);
            }
        });
        btnMyCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.nav_tarjetas);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    // MapView necesita recibir estos eventos para gestionar renderizado y recursos del mapa.
    @Override
    public void onStart() {
        mapView.onStart();
        super.onStart();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    /** Muestra el mapa base y habilita mi ubicación solo con permisos concedidos previamente. */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        double lat, lon;
        LatLng latitud = new LatLng(0, 0);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latitud));

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);

    }

    /**
     * Boceto de centrado de cámara y marcador, no usado por el mapa inicial.
     * El campo map no se asigna en onMapReady en la implementación actual.
     */
    private void addMarker(double lat, double lon) {
        LatLng coordenadas = new LatLng(lat, lon);
        CameraUpdate miUbicaion = CameraUpdateFactory.newLatLngZoom(coordenadas, 16);
        if (marcador != null) {
            marcador = map.addMarker(new MarkerOptions().position(coordenadas).title("Mi posición"));
            map.animateCamera(miUbicaion);
        }
        miUbicacion();
    }

    /** Pasa las coordenadas del proveedor GPS al boceto de marcador si hay una ubicación. */
    private void updateLocation(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lon = location.getLongitude();
            addMarker(lat, lon);
        }
    }

    // Receptor del boceto GPS: no representa cálculo de rutas ni horarios de transporte.
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /** Boceto de lectura GPS, protegido por permisos; no solicita permisos en este método. */
    private void miUbicacion() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateLocation(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000,0,locationListener);

    }

    /** Enlaza el botón de mapa externo con la acción proporcionada por MainActivity. */
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}
