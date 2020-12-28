package com.example.belarusgeogame.geometries;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.List;

public abstract class Geometry {
    protected List<Path> paths;

    public static float distance(PointF p1, PointF p2) {
        return p1.x;
    }

    public abstract void scale(float scale);

    protected abstract List<Path> computePath();

    public abstract boolean contains(PointF p);

    public abstract boolean isTouchable();

    public abstract PointG computeCentre();

    public void draw(Canvas canvas, Paint paint) {
        for (Path path : paths) {
            canvas.drawPath(path, paint);
        }
    }
}
