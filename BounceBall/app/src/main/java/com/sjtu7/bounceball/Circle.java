package com.sjtu7.bounceball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Circle extends Object
{
    public float radius;
    public Circle(float x,float y,float r)
    {
        super(x,y);
        radius = r;
        setColor(Color.RED);
    }
    public void draw(Canvas canvas)
    {
        Paint circlePaint = new Paint();
        circlePaint.setColor(getColor());
        canvas.drawCircle(x, y, radius, circlePaint);
    }
    public void setStill()
    {
        vx = 0;
        vy = 0;
        x = Constant.centerX;
        y = Constant.centerY;
    }
    public boolean still()
    {
        return vx==0&&vy==0;
    }
}
