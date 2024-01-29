package it.danieleborgna.earthquakes.model;

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
