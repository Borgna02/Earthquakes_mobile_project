package it.danieleborgna.earthquakes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import it.danieleborgna.earthquakes.model.Earthquake;

@Database(entities = {Earthquake.class}, version = 1)
public abstract class DB extends RoomDatabase {

    public abstract EarthquakeDAO getEarthquakeDAO();

    private volatile static DB instance = null;

    public static synchronized DB getInstance(Context context) {
        if(instance == null) {
            synchronized (DB.class){
                if(instance == null) {
                    instance = Room.databaseBuilder(context, DB.class, "database.DB").build();
                }
            }
        }

        return instance;
    }
}
