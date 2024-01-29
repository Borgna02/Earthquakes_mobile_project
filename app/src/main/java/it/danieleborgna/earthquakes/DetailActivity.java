package it.danieleborgna.earthquakes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import it.danieleborgna.earthquakes.databinding.ActivityDetailBinding;
import it.danieleborgna.earthquakes.model.Earthquake;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_EARTHQUAKE = "extra_earthquake";

    private Earthquake earthquake;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        it.danieleborgna.earthquakes.databinding.ActivityDetailBinding binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        earthquake = (Earthquake) getIntent().getSerializableExtra(EXTRA_EARTHQUAKE);
        if(earthquake != null) {
            binding.setEarthquake(earthquake);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.littleMap);

        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        binding.moreDetailsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println(earthquake.getEventUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(earthquake.getEventUrl()));
                startActivity(intent);
            }
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.getUiSettings().setScrollGesturesEnabled(false);
        googleMap.getUiSettings().setZoomControlsEnabled(false);
        LatLng position = new LatLng(earthquake.getLatitude(), earthquake.getLongitude());

        MarkerOptions options = new MarkerOptions();
        options.title(earthquake.getRegion() + ", " + earthquake.getDateToString() + ", " + earthquake.getMagnitude() + ' ' + earthquake.getMagnitudeType());
        options.position(position);
        googleMap.addMarker(options);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
    }



}
