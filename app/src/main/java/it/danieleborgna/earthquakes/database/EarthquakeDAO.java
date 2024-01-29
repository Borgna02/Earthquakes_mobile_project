package it.danieleborgna.earthquakes.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.OnConflictStrategy;

import java.util.List;

import it.danieleborgna.earthquakes.model.Earthquake;

@Dao
public interface EarthquakeDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<Earthquake> data);

    @Query("DELETE FROM earthquakes")
    public void deleteAll();

    @Query("SELECT * FROM earthquakes ORDER BY date DESC")
    public List<Earthquake> findAll();
}
