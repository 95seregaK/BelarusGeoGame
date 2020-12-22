package com.example.belarusgeogame.geoobjects;

public class GeoObject {
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
}