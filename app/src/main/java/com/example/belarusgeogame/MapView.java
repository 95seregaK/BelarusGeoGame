package com.example.belarusgeogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.belarusgeogame.geoobjects.Country;
import com.example.belarusgeogame.geoobjects.GeoObject;

import java.util.ArrayList;
import java.util.List;

public class MapView extends View {
    public List<Country> countries, selectedCountries;
    private Paint paint;
    private Paint paintFill, paintFill2;
    private float scale = 1, dX, dY;
    private OnGeoObjectSelectedListener onGeoObjectSelectedListener;
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
        if (countries != null) {
            canvas.translate(dX, dY);
            // canvas.scale(scale, scale);
            for (Country country : countries) {
                for (Path path : country.getPaths()) {
                    canvas.drawPath(path, paintFill);
                    canvas.drawPath(path, paint);
                }
            }
            for (Country country : selectedCountries) {
                for (Path path : country.getPaths()) {
                    canvas.drawPath(path, paintFill2);
                    canvas.drawPath(path, paint);
                }
            }
        }
    }

    public void init() {
        setOnTouchListener(new OnTouchListener());
        count = 0;
        /*DisplayMetrics metrics= new DisplayMetrics();
        getContext().getDisplay().getRealMetrics(metrics);
        dX= metrics.xdpi/2;
        dY= metrics.ydpi/2;*/
    }

    public void addCountries(List<Country> countries) {
        this.countries = countries;
        selectedCountries = new ArrayList<>();
        paint = new Paint();
        paintFill = new Paint();
        paintFill2 = new Paint();
        paint.setColor(Color.BLUE);
        paintFill.setColor(Color.MAGENTA);
        paintFill2.setColor(Color.GREEN);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paintFill.setStyle(Paint.Style.FILL);
        invalidate();
    }

    public void setOnGeoObjectSelectedListener(OnGeoObjectSelectedListener onGeoObjectSelectedListener) {
        this.onGeoObjectSelectedListener = onGeoObjectSelectedListener;
    }

    public void scale(float s) {
        for (Country country : countries) {
            country.scale(s);
        }
        for (Country country : selectedCountries) {
            country.scale(s);
        }
        dX = (dX - getWidth() / 2) * s + getWidth() / 2;
        dY = (dY - getHeight() / 2) * s + getHeight() / 2;
        ;
        invalidate();
    }

    public interface OnGeoObjectSelectedListener {

        public boolean onGeoObjectSelected(GeoObject object);
    }

    public class OnTouchListener implements View.OnTouchListener {
        private float x = 0, y = 0, dx, dy, eps = 0.1f;
        private boolean move = false;

        public void onClick() {
//            Log.d("onClick", "count="+count);
            boolean b = false;
            int s = 0;
            for (Country country : countries) {
                PointF p = new PointF(x - dX, y - dY);
                b = Country.pip(p, country.getBorder());
                s++;
                if (b && onGeoObjectSelectedListener != null) {
                    onGeoObjectSelectedListener.onGeoObjectSelected(country);
                    break;
                }
            }
            if (!b && onGeoObjectSelectedListener != null) {
                onGeoObjectSelectedListener.onGeoObjectSelected(null);
            }
            Log.d("N", "s=" + s);
            invalidate();
        }

        public void onMove(float dx, float dy) {

        }

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
                case MotionEvent.ACTION_UP:
                    if (!move) {
                        onClick();
                    }
                    break;
            }
            return true;
        }
    }
}
