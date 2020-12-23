package com.example.belarusgeogame.geometries;

import android.graphics.Path;
import android.graphics.PointF;

import java.util.List;

public abstract class Geometry {
    public abstract void scale(float scale);

    public abstract List<Path> computePath();

    public abstract boolean contains(PointF p);

    public static float distance(PointF p1, PointF p2){
        return p1.x;
    }
}
