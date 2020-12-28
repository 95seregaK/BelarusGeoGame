package com.example.belarusgeogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MapView extends View {
    public List<DrawableGeoList> reflectedObjects;
    private float scale = 1, dX, dY;
    private OnTouchListener onTouchListener;
    private boolean init = false;
    private int count;

    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        count++;
        if (!init) {
            dX = +getWidth() / 2;
            dY = +getHeight() / 2;
            init = true;
        }
        if (reflectedObjects != null) {
            canvas.translate(dX, dY);
            for (DrawableGeoList reflectedObject : reflectedObjects) {
                reflectedObject.drawSquares(canvas);
            }
            for (DrawableGeoList reflectedObject : reflectedObjects) {
                reflectedObject.drawPoints(canvas);
            }
        }
    }

    public void init() {
        onTouchListener = new OnTouchListener();
        setOnTouchListener(onTouchListener);
        reflectedObjects = new ArrayList<>();
        count = 0;
        /*DisplayMetrics metrics= new DisplayMetrics();
        getContext().getDisplay().getRealMetrics(metrics);
        dX= metrics.xdpi/2;
        dY= metrics.ydpi/2;*/
    }

    public void setOnMapClickListener(OnMapClickListener l) {
        onTouchListener.onMapClickListener = l;
    }

    public void scale(float s) {
        dX = (dX - getWidth() / 2) * s + getWidth() / 2;
        dY = (dY - getHeight() / 2) * s + getHeight() / 2;
        invalidate();
    }

    public void addDrawer(DrawableGeoList drawer) {
        reflectedObjects.add(drawer);
    }

    public interface OnMapClickListener {
        void onClick(PointF p);
    }


    public class OnTouchListener implements View.OnTouchListener {
        private float x = 0, y = 0, dx, dy, eps = 0.1f;
        private boolean move = false;
        private OnMapClickListener onMapClickListener;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getX();
                    y = event.getY();
                    move = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    dx = event.getX() - x;
                    dy = event.getY() - y;
                    if (Math.abs(dx) > eps || Math.abs(dy) > eps) {
                        dX += dx;
                        dY += dy;
                        x = event.getX();
                        y = event.getY();
                        invalidate();
                        move = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!move && onMapClickListener != null) {
                        onMapClickListener.onClick(new PointF(x - dX, y - dY));
                    }
                    break;
            }
            return true;
        }
    }
}

