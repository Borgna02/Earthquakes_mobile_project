package it.danieleborgna.earthquakes.model;

import org.json.JSONObject;

import java.io.Serializable;

/*
{"id":21851,
"ot":"2024-01-28 09:23:22",
"lat":40.705,
"lon":15.439,
"depth":9,
"mag":3.9,
"mag_type":"Mw",
"region":"6 km NW Ricigliano (SA)",
"event_url":"https:\/\/terremoti.ingv.it\/event\/37441251\/?tab=MeccanismoFocale#TDMTinfo"}
*/
public class Earthquake implements Serializable {

    public static Earthquake parseJson(JSONObject object) {

        if (object == null) return null;

        Earthquake earthquake = new Earthquake();

        // id
        try {
            String idStr = object.optString("id", null);
            earthquake.setId(Integer.parseInt(idStr)); // Può anche essere null perché è Integer
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // date
        earthquake.setDate(object.optString("ot"));

        // latitude
        try {
            String latStr = object.optString("lat", null);
            if (latStr != null) earthquake.setLatitude(Double.parseDouble(latStr));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // longitude
        try {
            String lngStr = object.optString("lon", null);
            if (lngStr != null) earthquake.setLongitude(Double.parseDouble(lngStr));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // depth
        try {
            String depthStr = object.optString("depth", null);
            if (depthStr != null) earthquake.setDepth(Double.parseDouble(depthStr));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // magnitude
        try {
            String magnitudeStr = object.optString("mag", null);
            if (magnitudeStr != null) earthquake.setMagnitude(Double.parseDouble(magnitudeStr));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // magnitude type
        earthquake.setMagnitudeType(object.optString("mag_type"));

        // region
        earthquake.setRegion(object.optString("region"));

        // event_url
        earthquake.setEventUrl(object.optString("event_url"));



        return earthquake;


    }

    private Integer id;
    private String date;
    private double latitude;
    private double longitude;
    private double depth;
    private double magnitude;
    private String magnitudeType;
    private String region;
    private String eventUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public String getMagnitudeType() {
        return magnitudeType;
    }

    public void setMagnitudeType(String magnitudeType) {
        this.magnitudeType = magnitudeType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }
}
