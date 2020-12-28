package com.example.belarusgeogame.geoobjects;

import android.graphics.Path;
import android.graphics.PointF;

import com.example.belarusgeogame.geometries.Geometry;
import com.example.belarusgeogame.geometries.PointG;

public abstract class GeoObject {
    protected Geometry geometry;
    protected PointG centre;
    private String name;

    public GeoObject(String name) {
        setName(name);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public void scale(float scale) {
        geometry.scale(scale);
        centre.scale(scale);
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
        centre = geometry.computeCentre();
    }

    public boolean displayedAsPoint() {
        return geometry.isTouchable();
    }

    public PointG getCentre() {
        return centre;
    }
}