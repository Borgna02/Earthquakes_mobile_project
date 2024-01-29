package it.danieleborgna.earthquakes.database;
import androidx.room.TypeConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Converters {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    @TypeConverter
    public static String fromDate(Date date) {
        return date != null ? dateFormat.format(date) : null;
    }

    @TypeConverter
    public static Date toDate(String value) {
        try {
            return value != null ? dateFormat.parse(value) : null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
