package com.example.belarusgeogame;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.belarusgeogame.geoobjects.GeoObject;

import java.util.List;

class DrawableGeoList {
    List<GeoObject> geoObjects;
    Paint paintFill;
    Paint paintBorder;


    public DrawableGeoList(List<GeoObject> geoObjects, Paint paintBorder, Paint paintFill) {
        this.geoObjects = geoObjects;
        this.paintFill = paintFill;
        this.paintBorder = paintBorder;
    }

    public void drawSquares(Canvas canvas) {
        for (GeoObject geoObject : geoObjects) {
            geoObject.getGeometry().draw(canvas, paintFill);
            geoObject.getGeometry().draw(canvas, paintBorder);
        }
    }

    public void drawPoints(Canvas canvas) {
        for (GeoObject geoObject : geoObjects) {
            if (geoObject.displayedAsPoint()) {
                geoObject.getCentre().draw(canvas, paintFill);
                geoObject.getCentre().draw(canvas, paintBorder);
            }
        }
    }
}
