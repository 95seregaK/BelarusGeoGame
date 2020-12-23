package com.example.belarusgeogame.geometries;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class PolygonSet extends Geometry {
    private int numberCount;
    private List<PointF[]> polygons;


    public PolygonSet(List<PointF[]> polygons) {
        this.polygons = polygons;
    }

    public static boolean pip(PointF p, PointF[] polygon) {
        int n = polygon.length;
        int s = 0;
        for (int i = 0; i < n - 1; i++) {
            float dy = polygon[i + 1].y - polygon[i].y;
            if (dy == 0) {
            } else {
                float t = (p.y - polygon[i].y) / dy;
                float x = polygon[i].x + t * (polygon[i + 1].x - polygon[i].x);
                if (t >= 0 && t < 1 && x > p.x) s++;
            }
        }
        //Log.d("PIP", " s=" + s);
        return s % 2 == 1;
    }

    @Override
    public void scale(float scale) {
        for (PointF[] polygon : polygons) {
            for (int i = 0; i < polygon.length; i++) {
                polygon[i].x *= scale;
                polygon[i].y *= scale;
            }
        }
    }

    @Override
    public List<Path> computePath() {
        List<Path> paths = new ArrayList<>();
        Path path = null;
        PointF[] lastPolygon = null;
        for (PointF[] polygon : polygons) {
            if (lastPolygon == null || !contains(polygon[0])) {
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

    @Override
    public boolean contains(PointF p) {
        int s = 0;
        for (PointF[] polygon : polygons) {
            if (pip(p, polygon)) s++;
        }
        return s % 2 == 1;
    }
}
