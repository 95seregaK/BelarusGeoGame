package com.example.belarusgeogame;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;
import android.widget.Toast;

import com.example.belarusgeogame.geoobjects.GeoObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Game {
    public static final int CODE_END = 2;
    public static final int CODE_SUCCESS = 1;
    private final List<GeoObject> allGeoObjects, activeGeoObjects, guessedGeoObjects;
    private final MapView mapView;
    private GeoObject currentGeoObject;
    private Random random;
    private AttemptCallback attemptCallback;
    private Toast toast;
    private int attempt = 0;
    private boolean active = false;

    public Game(List<GeoObject> geoObjects, MapView mapView) {
        allGeoObjects = new ArrayList<>(geoObjects);
        /*Collections.sort(allGeoObjects, new Comparator<GeoObject>() {
            @Override
            public int compare(GeoObject o1, GeoObject o2) {
                return o1.getPriority()-o2.getPriority();
            }
        });*/
        activeGeoObjects = new ArrayList<>(geoObjects);
        guessedGeoObjects = new ArrayList<>();
        this.mapView = mapView;
        random = new Random();
        mapView.setOnMapClickListener((p) -> {
            if (active)
                onAttempt(p);
        });
        defineDrawers();
        toast = Toast.makeText(mapView.getContext(), "Hello", Toast.LENGTH_SHORT);

    }

    public void startGame() {
        active = true;
        currentGeoObject = nextGeoObject();
        toast.show();
    }

    private void endGame() {
        active = false;
    }

    public GeoObject nextGeoObject() {
        int n = activeGeoObjects.size();
        if (n > 0)
            return activeGeoObjects.get(random.nextInt(n));
        return null;
    }

    public GeoObject getCurrentGeoObject() {
        return currentGeoObject;
    }

    private void onAttempt(PointF p) {
        if (attempt == 2 || currentGeoObject.getGeometry().contains(p)) {
            onSuccess();
            attempt = 0;
            mapView.invalidate();
        } else {
            GeoObject geoObject = findGeoObject(p, allGeoObjects);
            if (geoObject == null) {
                showToast("Fault");
            } else {
                attempt++;
                showToast(geoObject.getName());
            }
        }
    }

    private void showToast(String text) {
        toast.cancel();
        toast = Toast.makeText(mapView.getContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void onSuccess() {
        showToast("Success");
        activeGeoObjects.remove(currentGeoObject);
        guessedGeoObjects.add(currentGeoObject);
        currentGeoObject = nextGeoObject();
        if (currentGeoObject == null) {
            endGame();
            if (attemptCallback != null) attemptCallback.callback(CODE_END);
        } else {
            if (attemptCallback != null) attemptCallback.callback(CODE_SUCCESS);
        }

    }

    public GeoObject findGeoObject(PointF p, List<GeoObject> geoObjects) {
        for (GeoObject geoObject : geoObjects) {
            if (geoObject.getGeometry().contains(p)) {
                Log.d("findGeoObject", geoObject.getName());
                return geoObject;
            }
        }
        return null;
    }

    public void scale(float s) {
        for (GeoObject geoObject : allGeoObjects) {
            geoObject.scale(s);
        }
        mapView.scale(s);
    }

    private void defineDrawers() {
        Paint paintBorder = new Paint();
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setColor(Color.BLACK);
        paintBorder.setStrokeWidth(2);

        Paint paintFill = new Paint();
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(Color.YELLOW);
        mapView.addDrawer(new Drawer(activeGeoObjects, paintBorder, paintFill));

        paintFill = new Paint();
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(Color.RED);
        mapView.addDrawer(new Drawer(guessedGeoObjects, paintBorder, paintFill));
    }

    public void setAttemptCallback(AttemptCallback callback) {
        this.attemptCallback = callback;
    }

    public interface AttemptCallback {
        void callback(int res);
    }
}
