package com.example.belarusgeogame;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Country {
    private List<PointF[]> border;
    private List<Path> paths;
    private String name;


    public Country(String name) {
        setName(name);
    }

    public static boolean pip(PointF p, PointF[] polygon) {
        int n = polygon.length;
        int s = 0;
        for (int i = 0; i < n - 1; i++) {
            float dy = polygon[i + 1].y - polygon[i].y;
            float t = (p.y - polygon[i].y) / dy;
            float x = polygon[i].x + t * (polygon[i + 1].x - polygon[i].x);
            if (t > 0 && t < 1 && x > p.x) s++;
        }
        //Log.d("PIP", " s=" + s);
        return s % 2 == 1;
    }

    public static boolean pip(PointF p, List<PointF[]> polygons) {
        int s = 0;
        for (PointF[] polygon : polygons) {
            if (pip(p, polygon)) s++;
        }
        return s % 2 == 1;
    }

    private List<Path> computePath(List<PointF[]> border) {
        List<Path> paths = new ArrayList<>();
        Path path = null;
        PointF[] lastPolygon = null;
        for (PointF[] polygon : border) {
            if (lastPolygon == null || !pip(polygon[0], lastPolygon)) {
                path = new Path();
                paths.add(path);
                lastPolygon = polygon;
            }
            path.moveTo(polygon[0].x, polygon[0].y);
            int n = polygon.length;
            for (int i = 1; i < n; i++) {
                path.lineTo(polygon[i].x, polygon[i].y);
               //Log.d("coordinates", polygon[i].x + " " + polygon[i].y);
            }
        }
        return paths;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public void setPaths(List<Path> paths) {
        this.paths = paths;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public List<PointF[]> getBorder() {
        return border;
    }

    public void setBorder(List<PointF[]> border) {
        this.border = border;
        paths = computePath(border);
    }
}
