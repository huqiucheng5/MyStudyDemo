package com.example.hu.mystudydemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by q623407474 on 2015/7/6.
 */
public class FellowCircle extends View {
    private float currentX = 80;
    private float currentY = 80;
    Paint paint = new Paint();

    public FellowCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FellowCircle(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(currentX, currentY, 50, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;

        }

        return true;
    }
}
