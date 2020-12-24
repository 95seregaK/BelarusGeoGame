package com.example.belarusgeogame.geoobjects;

import android.graphics.Path;

import com.example.belarusgeogame.geometries.Geometry;

import java.util.List;

public abstract class GeoObject {
    protected List<Path> paths;
    protected Geometry geometry;
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
        paths = geometry.computePath();
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
        paths = geometry.computePath();
    }

    public List<Path> getPaths() {
        return paths;
    }

}