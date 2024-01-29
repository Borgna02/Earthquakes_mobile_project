package it.danieleborgna.earthquakes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import it.danieleborgna.earthquakes.database.DB;
import it.danieleborgna.earthquakes.model.Earthquake;
import it.danieleborgna.earthquakes.service.Repository;
import it.danieleborgna.earthquakes.service.Request;

public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<List<Earthquake>> earthquakes = new MutableLiveData<>();
    private Repository repository;


    public MainViewModel(@NonNull Application application) {
        super(application);

        new Thread(() -> {
            repository = ((Earthquakes) application).getRepository();
            // Recupero i terremoti dal database
            List<Earthquake> list = DB.getInstance(application).getEarthquakeDAO().findAll();
            // Se il database è vuoto, eseguo la richiesta HTTP
            if(list.isEmpty()) {
                repository.downloadData(application, new Request.RequestCallback() {
                    @Override
                    public void onCompleted(UrlRequest request, UrlResponseInfo info, byte[] data, CronetException error) {
                        List<Earthquake> tempEarthquakes = new ArrayList<>();
                        if(data != null) {
                            String response = new String(data);
                            try {
                                JSONArray array = new JSONArray(response);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject item = array.optJSONObject(i);
                                    Earthquake earthquake = Earthquake.parseJson(item);
                                    if(earthquake != null) tempEarthquakes.add(earthquake);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if(error != null) {
                                error.printStackTrace();
                            }
                        }


                        DB.getInstance(application).getEarthquakeDAO().insert(tempEarthquakes);

                        earthquakes.postValue(tempEarthquakes); // uso postValue perché sono in un thread separato
                    }
                });
            } else {
                earthquakes.postValue(list);
            }

        }).start();
    }

    private LiveData<List<Earthquake>> getStations() {
        return earthquakes;
    }
}
