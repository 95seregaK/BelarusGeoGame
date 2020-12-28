package com.example.belarusgeogame.geometries;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PolygonG extends Geometry {
    private static final float MIN_SQUARE = 400;
    private int pointCount;
    private float square;
    private List<PointF[]> polygons;


    public PolygonG(List<PointF[]> polygons) {
        this.polygons = polygons;
        square = computeSquare();
        paths = computePath();
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
    public PointG computeCentre() {
        double x = 0, y = 0;
        int n = 0;
        for (PointF[] polygon : polygons) {
            for (int i = 0; i < polygon.length; i++) {
                x += polygon[i].x;
                y += polygon[i].y;
                n++;
            }
        }
        x /= n;
        y /= n;
        return new PointG(new PointF((float) x, (float) y));
    }

    @Override
    public void scale(float sc) {
        for (PointF[] polygon : polygons) {
            for (int i = 0; i < polygon.length; i++) {
                polygon[i].x *= sc;
                polygon[i].y *= sc;
            }
        }
        square = computeSquare();
        paths = computePath();
    }

    private float computeSquare() {
        float square = 0;
        for (PointF[] polygon : polygons) {
            float s = polygon[polygon.length - 1].x * polygon[0].y
                    - polygon[0].x * polygon[polygon.length - 1].y;
            for (int i = 0; i < polygon.length - 1; i++) {
                s += polygon[i].x * polygon[i + 1].y - polygon[i + 1].x * polygon[i].y;
            }
            square += s;
        }
        return Math.abs(square / 2);
    }

    @Override
    protected List<Path> computePath() {
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
        Log.d("Square", "square = " + computeSquare());
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

    @Override
    public boolean isTouchable() {
        return square < MIN_SQUARE;
    }

    public float getSquare() {
        return square;
    }
}
