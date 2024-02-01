package it.danieleborgna.earthquakes.service;

import android.content.Context;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Request {

    private static volatile Request instance = null;

    private final String DATASET_URL = "https://terremoti.ingv.it/en/tdmt_export?starttime=1985-01-01T00%3A00%3A00&endtime=2024-01-29T23%3A59%3A59&last_nd=-2&minmag=-1&maxmag=10&mindepth=-10&maxdepth=1000&minlat=-90&maxlat=90&minlon=-180&maxlon=180&minversion=100&limit=30&orderby=ot-desc&tdmt_flag%5B%5D=tdmt_auto&tdmt_flag%5B%5D=tdmt_reviewer&tdmt_flag%5B%5D=tdmt_historical&lat=0&lon=0&maxradiuskm=-1&wheretype=area&0=wheretype&1=last_nd&2=limit&3=page&4=box_search&5=maxradiuskm&6=lat&7=lon&format=json";

    public static synchronized Request getInstance(Context context) {
        if (instance == null) {
            synchronized (Request.class) {
                if (instance == null) instance = new Request(context);
            }
        }

        return instance;
    }

    private Request(Context context) {
        engine = new CronetEngine.Builder(context)
                .enableHttp2(true)
                .enableQuic(true)
                .enableBrotli(true)
                .setStoragePath(context.getCacheDir().getAbsolutePath())
                .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_DISK, 10 * 1024 * 1024) //10 MB, prima di definire la dimensione della cache si deve sempre definrie il percorso
                .build();
    }

    private final CronetEngine engine;

    private final Executor executor = Executors.newSingleThreadExecutor();

    public void requestDownload(Request.RequestCallback callback) {
        engine.newUrlRequestBuilder(DATASET_URL, callback, executor)
                .build()
                .start();
    }

    public abstract static class RequestCallback extends UrlRequest.Callback {

        private final ByteArrayOutputStream received = new ByteArrayOutputStream();

        private final WritableByteChannel channel = Channels.newChannel(received);

        @Override
        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) {
            // Come ci comportiamo quando riceviamo un redirect dal server
            request.followRedirect(); // per seguire il redirect
        }

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
            // Inizializziamo la memoria da allocare se la risposta è OK
            if (info.getHttpStatusCode() == 200 /* OK */) {
                //1MB
                int BUFFERSIZE = 1024 * 1024;
                request.read(ByteBuffer.allocateDirect(BUFFERSIZE));
            }
        }

        @Override
        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
            // Aggiungiamo i dati nel buffer
            byteBuffer.flip();

            try {
                channel.write(byteBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Al termine puliamo il buffer
            byteBuffer.clear();
            request.read(byteBuffer);

        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            byte[] data = received.toByteArray();
            onCompleted(request, info, data, null);
        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
            onCompleted(request, info, null, error);

        }

        // Inviamo i dati attraverso la callback
        public abstract void onCompleted(UrlRequest request, UrlResponseInfo info, byte[] data, CronetException error);


    }
}

