package it.danieleborgna.earthquakes.service;

import android.content.Context;

public class Repository {
    public void downloadData(Context context, Request.RequestCallback callback) {
        Request.getInstance(context).requestDownload(callback);
    }
}
