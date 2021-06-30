package com.sjtu7.bounceball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Square extends Object
{
    public float width;
    public int number;
    public float textSize;
    public float lastY;
    public boolean ball;

    public Square(float x,float y,float w,int numberr)
    {
        super(x,y);
        width =  w;
        number = numberr;
        textSize = w/3;
        lastY = y;
        ball = false;
        if(numberr>=20)
        {
            setColor(Color.MAGENTA);
        }
    }
    public void draw(Canvas canvas)
    {
        if(ball)
        {
            Paint circlePaint = new Paint();
            circlePaint.setColor(Color.YELLOW);
            canvas.drawCircle(x, y, Constant.ball_radius+10, circlePaint);
            Paint textPaint = new Paint();
            textPaint.setColor(Color.RED);
            textPaint.setTextSize(textSize);
            canvas.drawText("x1",x-textSize/2,y+textSize/2-10,textPaint);
        }
        else{
            Paint SquarePaint = new Paint();
            SquarePaint.setColor(getColor());
            canvas.drawRect(getLeft(),getTop(),getRight(),getBottom(),SquarePaint);

            SquarePaint.setColor(Color.GRAY);
            SquarePaint.setStyle(Paint.Style.STROKE);

            canvas.drawRect(getLeft(),getTop(),getRight(),getBottom(),SquarePaint);

            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(textSize);
            canvas.drawText(String.valueOf(number),x-textSize/2,y+textSize/2,textPaint);
        }

    }
    public float getTop()
    {
        return y-width/2;
    }
    public float getLeft()
    {
        return x-width/2;
    }
    public float getRight()
    {
        return x+width/2;
    }
    public float getBottom()
    {
        return y+width/2;
    }
}
