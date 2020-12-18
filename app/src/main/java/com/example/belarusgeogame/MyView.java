package com.example.belarusgeogame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class MyView extends View {
    private Paint paint;
    private Path path;
    private float scale = 1, dx, dy;

    public MyView(Context context) {
        super(context);
        init();
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (path != null) {
            canvas.translate(dx, dy);
            canvas.scale(scale, scale);
            canvas.drawPath(path, paint);

        }
    }

    public void init() {
        setOnTouchListener(new OnTouchListener());
    }

    public void addPath(Path path) {
        this.path = path;
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        invalidate();
    }

    public class OnTouchListener implements View.OnTouchListener {
        float x = 0, y = 0;
        boolean move = false;

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
                        if (x > 300) scale += 0.25;
                        else scale -= 0.25;
                        invalidate();
                    }
                    break;
            }
            return true;
        }
    }
}
