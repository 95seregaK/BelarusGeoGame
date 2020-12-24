package com.example.belarusgeogame.geometries;

import android.graphics.PointF;

public abstract class Pointable extends Geometry{
    PointG centre = new PointG(new PointF());

    abstract boolean reflectedAsPoint();

    abstract void computeCentre();

}
