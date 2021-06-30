package com.sjtu7.bounceball;

import android.graphics.Canvas;
import android.graphics.Color;

public class Object {
    public float x;
    public float y;
    public float vx;
    public float vy;
    private int color;
    public Object(float xx,float yy)
    {
        x = xx;
        y = yy;
        vx = 0;
        vy = 0;
        color = Color.BLUE;
    }
    public void setColor(int colorr)
    {
        color = colorr;
    }
    public int getColor()
    {
        return color;
    }
    public void move()
    {
        x += vx;
        y += vy;
    }
    public void setSpeed(float x,float y)
    {
        vx = x;
        vy = y;
    }

    public void draw(Canvas canvas) {}
}

