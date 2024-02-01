package it.danieleborgna.earthquakes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import it.danieleborgna.earthquakes.databinding.FragmentMapBinding;
import it.danieleborgna.earthquakes.model.Earthquake;
import it.danieleborgna.earthquakes.service.LocationHelper;

public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap map;

    private MainViewModel mainViewModel;

    private FragmentMapBinding binding;

    private Bitmap myPosMarkerIcon;

    private final ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), granted -> {
                System.out.println("Inside permission request, granted: " + granted);
                if (granted) {
                    LocationHelper.updateLocation(requireContext(), MapFragment.this);
                } else {
                    Toast.makeText(requireContext(), getString(R.string.location_required), Toast.LENGTH_SHORT).show();
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

        this.myPosMarkerIcon = loadIconBitmap();

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        if (!checkPermission()) permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public boolean checkPermission() {
        int fineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        System.out.println("Asking permission, current value" + fineLocation);
        return fineLocation == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onResume() {
        super.onResume();

        int fineLocation = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (fineLocation == PackageManager.PERMISSION_GRANTED) {
            LocationHelper.updateLocation(requireContext(), this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        LocationHelper.stop(requireContext(), this);
    }

    // I permessi sono già stati dati arrivati a questo punto
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        LocationHelper.updateLocation(requireContext(), this);
        showMarkers();

        map.setOnInfoWindowClickListener(marker -> {
            Earthquake earthquake = (Earthquake) marker.getTag();
            if (earthquake != null) {

                Bundle bundle = new Bundle();
                bundle.putSerializable(DetailActivity.EXTRA_EARTHQUAKE, earthquake);

                Navigation.findNavController(requireView())
                        .navigate(R.id.action_mapFragment_to_detailActivity, bundle);
            }
        });

        binding.updatePositionButton.setOnClickListener(view -> {
            if (!checkPermission())
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);

            LocationHelper.updateLocation(requireContext(), this);
        });
    }

    private void showMarkers() {
        mainViewModel.getEarthquakes()
                .observe(getViewLifecycleOwner(), earthquakes -> {

                    map.clear();
                    earthquakes.forEach(this::createEarthquakeMarker);
                });
    }

    private void createEarthquakeMarker(Earthquake earthquake) {
        MarkerOptions options = new MarkerOptions();
        options.title(earthquake.getMagnitudeToString());
        options.contentDescription(earthquake.getDateToString());
        options.position(new LatLng(earthquake.getLatitude(), earthquake.getLongitude()));
        options.icon(BitmapDescriptorFactory.defaultMarker(10.0f));
        Marker marker = map.addMarker(options);
        assert marker != null;
        marker.setTag(earthquake);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions options = new MarkerOptions();
        options.title(getString(R.string.my_location));
        options.position(myPosition);
        options.zIndex(Integer.MAX_VALUE);
        options.icon(BitmapDescriptorFactory.fromBitmap(this.myPosMarkerIcon));
        map.addMarker(options);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 12));
    }

    private Bitmap loadIconBitmap() {
        Drawable vectorDrawable = VectorDrawableCompat.create(getResources(), R.drawable.my_pos_circle, null);

        assert vectorDrawable != null;
        int width = vectorDrawable.getIntrinsicWidth();
        int height = vectorDrawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);

        return bitmap;
    }
}
