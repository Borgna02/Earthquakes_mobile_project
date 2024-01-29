package it.danieleborgna.earthquakes.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

public class LocationHelper {
    public static void start(Context context, LocationListener listener) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // devo controllare i permessi runtime
        int fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (fineLocation == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED) {
            updatePosition(context, listener);
        }
    }

    // Questo metodo sarà chiamato solo quando sono sicuro che i permessi sono già stati dati
    @SuppressLint("MissingPermission")
    public static void updatePosition(Context context, LocationListener listener) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        // Aggiorno la posizione solo all'avvio, poi l'utente è libero di spostarsi nella mappa
        manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null);
        manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);
    }

    public static void stop(Context context, LocationListener listener) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        manager.removeUpdates(listener);
    }
}
