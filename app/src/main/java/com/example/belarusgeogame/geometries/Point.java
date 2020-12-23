package com.example.belarusgeogame.geometries;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

public class Point extends Geometry {
    protected android.graphics.PointF point;
    protected float radius = 10;

    @Override
    public void scale(float scale) {
        point.x *= scale;
        point.y *= scale;
    }

    @Override
    public List<Path> computePath() {
        Path path = new Path();
        path.addCircle(point.x, point.y, radius, Path.Direction.CW);
        List<Path> paths = new ArrayList<>();
        paths.add(path);
        return paths;
    }

    @Override
    public boolean contains(android.graphics.PointF p) {
        float dx = p.x - point.x;
        float dy = p.y - point.y;
        return dx * dx + dy * dy < radius * radius;
    }
}
