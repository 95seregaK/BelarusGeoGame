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
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;

public class MyView extends View {
    private Paint paint;
    private Paint paintFill;
    private List<Country> countries;
    private float scale = 1, dx, dy;
    private Context context;

    public MyView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (countries != null) {
            canvas.translate(dx, dy);
            // canvas.scale(scale, scale);
            for (Country country : countries) {
                for (Path path : country.getPaths()) {
                    canvas.drawPath(path, paintFill);
                    canvas.drawPath(path, paint);
                }
            }
        }
    }

    public void init() {
        setOnTouchListener(new OnTouchListener());
    }

    public void addCountries(List<Country> countries) {
        this.countries = countries;
        paint = new Paint();
        paintFill = new Paint();
        paint.setColor(Color.BLUE);
        paintFill.setColor(Color.MAGENTA);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paintFill.setStyle(Paint.Style.FILL);
        invalidate();
    }

    public class OnTouchListener implements View.OnTouchListener {
        float x = 0, y = 0;
        boolean move = false;
        Toast toast = null;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getX();
                    y = event.getY();
                    move = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    dx += event.getX() - x;
                    dy += event.getY() - y;
                    x = event.getX();
                    y = event.getY();
                    invalidate();
                    move = true;
                case MotionEvent.ACTION_UP:
                    if (!move) {
                        //Log.d("ACTION_UP", (dx) + " " + (dy));
                        int s = 0;
                        for (Country country : countries) {
                            PointF p = new PointF(x - dx, y - dy);
                            boolean b = Country.pip(p, country.getBorder());
                            s++;
                            if (b) {
                                if (toast != null) toast.cancel();
                                toast = Toast.makeText(getContext(), country.getName(), Toast.LENGTH_SHORT);
                                toast.show();
                                Log.d("PIP", country.getName());
                                break;
                            }
                        }
                        Log.d("N", "s=" + s);
                        invalidate();
                    }
                    break;
            }
            return true;
        }
    }
}
