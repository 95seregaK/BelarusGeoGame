package com.example.belarusgeogame.geometries;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

public class PointG extends Geometry {
    protected android.graphics.PointF point;
    protected float radius = 16;

    public PointG(PointF p) {
        point = new PointF(p.x, p.y);
        paths=computePath();
    }

    @Override
    public void scale(float scale) {
        point.x *= scale;
        point.y *= scale;
        paths = computePath();
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
    public boolean contains(PointF p) {
        float dx = p.x - point.x;
        float dy = p.y - point.y;
        return dx * dx + dy * dy < radius * radius;
    }

    @Override
    public boolean isTouchable() {
        return false;
    }

    @Override
    public PointG computeCentre() {
        return null;
    }

    public void setPosition(PointF p) {
        point = p;
    }

}
