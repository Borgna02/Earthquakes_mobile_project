package it.danieleborgna.earthquakes;

import android.app.Application;

import it.danieleborgna.earthquakes.service.Repository;

public class Earthquakes extends Application {

    private Repository repository;

    @Override
    public void onCreate() {
        super.onCreate();

        repository = new Repository();
    }

    public Repository getRepository() {
        return repository;
    }
}
