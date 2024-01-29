package it.danieleborgna.earthquakes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import it.danieleborgna.earthquakes.databinding.FragmentMapBinding;
import it.danieleborgna.earthquakes.model.Earthquake;
import it.danieleborgna.earthquakes.service.LocationHelper;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap map;

    private MainViewModel mainViewModel;

    private Marker myPositionMarker;

    private List<Earthquake> earthquakes = new ArrayList<>();

    private FragmentMapBinding binding;

    private ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean granted) {
                    if (granted) {
                        LocationHelper.start(requireContext(), MapFragment.this);
                    } else {
                        Toast.makeText(requireContext(), getString(R.string.location_required), Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);

        int fineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(fineLocation == PackageManager.PERMISSION_DENIED) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int fineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(fineLocation == PackageManager.PERMISSION_DENIED) {
            LocationHelper.start(requireContext(), this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        LocationHelper.stop(requireContext(), this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                Earthquake earthquake = (Earthquake) marker.getTag();
                if (earthquake != null) {

                    Bundle bundle = new Bundle();
                    bundle.putSerializable(DetailActivity.EXTRA_EARTHQUAKE, earthquake);

                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_mapFragment_to_detailActivity, bundle);
                }
            }
        });
        showMarkers();
    }

    private void showMarkers() {
        mainViewModel.getEarthquakes()
                .observe(getViewLifecycleOwner(), earthquakes -> {

                    MapFragment.this.earthquakes = earthquakes;

                    map.clear();
                    earthquakes.forEach(this::createEarthquake);
                });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        bounds.include(currentPosition); // se non c'è nessuna posizione va in crash quando creo il bound
        if (myPositionMarker == null) {
            MarkerOptions options = new MarkerOptions();
            options.title(getString(R.string.my_location));
            options.position(currentPosition);
            myPositionMarker = map.addMarker(options);
        } else {
            myPositionMarker.setPosition(currentPosition);
        }
        new Thread(() -> {

            // quando la posizione viene cambiata, posso mostrare solo le stazioni nella mia zona
            if (!earthquakes.isEmpty()) {
                // rettangolo nella mappa

                for (Earthquake earthquake : earthquakes) {
                    Location earthquakeLocation = new Location(getString(R.string.earthquake));
                    earthquakeLocation.setLatitude(earthquake.getLatitude());
                    earthquakeLocation.setLongitude(earthquake.getLongitude());

                    if (earthquakeLocation.distanceTo(location) >= 10000) continue; //10 km

                    bounds.include(new LatLng(earthquake.getLatitude(), earthquake.getLongitude()));
                }
            }

            // questo serve per eseguire l'operazione sul main thread dopo che tutto il resto è stato eseguito
            binding.getRoot().post(() -> {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 16));
            });
        }).start();


    }

    private void createEarthquake(Earthquake earthquake) {
        MarkerOptions options = new MarkerOptions();
        options.title(earthquake.getMagnitudeToString());
        options.position(new LatLng(earthquake.getLatitude(), earthquake.getLongitude()));
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        Marker marker = map.addMarker(options);
        marker.setTag(earthquake);
    }

}
