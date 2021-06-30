package com.sjtu7.bounceball;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MovementView extends SurfaceView implements SurfaceHolder.Callback {

    private List<Square> blocks = new ArrayList<>();
    private List<Circle> balls = new ArrayList<>();
    private int width;
    private int height;
    private int block_width;
    private int status;//0-still 1-ball 2-block
    private float vx = 0;
    private float vy = 0;
    private int ball_count = 0;
    private int ball_index = 0;
    private int add_ball = 0;
    private boolean first = true;//第一个回到底部的球
    private boolean game_over = false;



    UpdateThread updateThread;

    public MovementView(Context context) {

        super(context);
        getHolder().addCallback(this);

    }
    public void surfaceCreated(SurfaceHolder holder) {

        Rect surfaceFrame = holder.getSurfaceFrame();
        width = surfaceFrame.width();
        height = surfaceFrame.height();


        status = Constant.BLOCK;
        block_width = width/Constant.number_of_blocks_in_a_row;
        createBlocks();

        Constant.centerX = width/2;
        Constant.centerY = height-Constant.ball_radius;
        Circle ball = new Circle(Constant.centerX,Constant.centerY,Constant.ball_radius);
        balls.add(ball);
        updateThread = new UpdateThread(this);
        updateThread.setRunning(true);
        updateThread.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.CYAN);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        int textSize = 200;
        textPaint.setTextSize(textSize);
        canvas.drawText("level:"+(Constant.level-1),width/2-textSize-50,height/2+textSize+50,textPaint);
        //canvas.drawText("ball:"+balls.size(),width/2-textSize-50,height/2+textSize*2,textPaint);
        if(status==Constant.BALL)
        {
            for(int i = 0; i < balls.size();i++)
            {
                balls.get(i).draw(canvas);
            }
        }
        else {
            balls.get(0).draw(canvas);
        }

        for(int i = 0; i < blocks.size();i++)
        {
            blocks.get(i).draw(canvas);
        }
        if(game_over)
        {
            textPaint.setColor(Color.RED);
            canvas.drawText("游戏结束！",width/2-textSize*2,height/2-textSize,textPaint);
        }
    }

    public void bounce_ball()
    {
        ball_count += 1;
        if((ball_count-1)%Constant.ball_seperation==0)
        {
            if(ball_index<balls.size()&&balls.get(ball_index).still())
            {
                balls.get(ball_index).setSpeed(vx,vy);
                ball_index += 1;
            }
        }
        boolean finish = true;
        for(int j = 0;j<balls.size();j++)
        {
            Circle ball = balls.get(j);
            if(!ball.still())
            {
                finish = false;
                ball.move();

                if (ball.y - ball.radius < 0 )
                {
                    ball.y = ball.radius;
                    ball.vy *=-1;
                }
                if(ball.y + ball.radius > height)
                {
                    ball.y = height - ball.radius;
                    //ball.vy *= -1;
                    if(first)
                    {
                        Constant.centerX = ball.x;
                        ball.setStill();
                        first = false;
                    }
                    else {
                         ball.y = Constant.centerY;
                         ball.setSpeed(Math.signum(Constant.centerX-ball.x)*Constant.ball_speed,0);
                        }

                }
                if(ball.vy==0&&ball.vx!=0&&Math.abs(ball.x-Constant.centerX)<Constant.ball_speed*2)
                {
                    ball.setStill();
                }
                if (ball.x - ball.radius < 0 )
                {
                    ball.x = ball.radius;
                    ball.vx *=-1;
                }
                if(ball.x + ball.radius > width)
                {
                    ball.x = width - ball.radius;
                    ball.vx *= -1;
                }
                //球与方块碰撞
                for(int i = 0; i < blocks.size();i++)
                {
                    Square block = blocks.get(i);
                    if (block.getLeft()<ball.x && ball.x<block.getRight())
                    {
                        if(ball.y - ball.radius < block.getBottom()&&ball.y - ball.vy+ball.radius > block.getBottom())
                        {
                            block.number -= 1;
                            if(block.ball)
                            {
                                add_ball += 1;
                            }
                            else{
                                ball.y = ball.radius + block.getBottom();
                                ball.vy *=-1;
                            }

                        }
                        if(ball.y + ball.radius > block.getTop()&&ball.y - ball.vy+ball.radius < block.getTop())
                        {
                            block.number -= 1;
                            if(block.ball)
                            {
                                add_ball += 1;
                            }
                            else{
                                ball.y = block.getTop() - ball.radius;
                                ball.vy *= -1;
                            }
                        }
                    }
                    if(block.getTop()<ball.y&&ball.y<block.getBottom())
                    {
                        if (ball.x - ball.radius < block.getRight()&&ball.x - ball.vx+ball.radius > block.getRight())
                        {
                            block.number -= 1;
                            if(block.ball)
                            {
                                add_ball += 1;
                            }
                            else{
                                ball.x = ball.radius + block.getRight();
                                ball.vx *=-1;
                            }
                        }
                        if(ball.x + ball.radius > block.getLeft()&&ball.x - ball.vx+ball.radius < block.getLeft())
                        {
                            block.number -= 1;
                            if(block.ball)
                            {
                                add_ball += 1;
                            }
                            else{
                                ball.x = block.getLeft() - ball.radius;
                                ball.vx *= -1;
                            }
                        }
                    }
                    if(block.number<20)
                    {
                        block.setColor(Color.BLUE);
                    }
                    if(block.number<=0)
                    {
                        blocks.remove(i);
                    }
                }

            }
        }
        if(finish)
        {
            status  = (status+1)%3;
            first = true;
            ball_count = 0;
            ball_index = 0;
            createBlocks();
            for(int i = 0;i<add_ball;i++)
            {
                Circle ball = new Circle(Constant.centerX,Constant.centerY,Constant.ball_radius);
                ball.setColor(Color.RED);
                balls.add(ball);
            }
        }
    }

    public void move_block()
    {
        boolean finish = false;
        for(int i = 0;i < blocks.size();i++)
        {
            Square block = blocks.get(i);
            if(block.y-block.lastY <block.width)
            {
                block.move();
            }
            else{
                block.lastY = block.y;
                finish = true;
            }
            if(block.y>=height-block.width/2&&!block.ball)
            {
                game_over = true;
            }
        }
        if(finish)
        {
            status  = (status+1)%3;
            Constant.level += 1;
        }
    }
    public void update() {
        if(!game_over)
        {
            switch (status)
            {
                case Constant.BALL:
                    bounce_ball();
                    break;
                case Constant.BLOCK:
                    move_block();
                    break;
            }
        }
}


    public boolean onTouchEvent(MotionEvent event)
    {
        if(!game_over)
        {
            int action = event.getAction();
            if(action == MotionEvent.ACTION_UP)
            {
                float x = event.getX();
                float y = event.getY();
                if(Math.abs(y-Constant.centerY)>30)
                {
                    if(status==Constant.STILL)
                    {
                        vx = x - Constant.centerX;
                        vy = y - Constant.centerY;
                        float vr = (float) Math.pow(vx*vx+vy*vy,0.5);
                        vx = vx/vr*Constant.ball_speed;
                        vy = vy/vr*Constant.ball_speed;
                        status  = (status+1)%3;
                        add_ball = 0;
                    }
                }
            }
        }
        else{
            reset();
        }

        return true;
    }
    public void reset()
    {
        vx = 0;
        vy = 0;
        ball_count = 0;
        ball_index = 0;
        add_ball = 0;
        first = true;//第一个回到底部的球
        game_over = false;
        Constant.level = 1;
        status = Constant.BLOCK;

        blocks.clear();
        balls.clear();
        createBlocks();

        Constant.centerX = width/2;
        Circle ball = new Circle(Constant.centerX,Constant.centerY,Constant.ball_radius);
        balls.add(ball);
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

        boolean retry = true;

        updateThread.setRunning(false);
        while (retry) {
            try {
                updateThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void createBlocks()
    {
        Random r = new Random();
        int num = r.nextInt(Constant.number_of_blocks_in_a_row/2)+1;
        int add_ball_index = r.nextInt(num)+1;
        Set<Integer> set = new HashSet<Integer>();
        while(set.size()<=num)
        {
            set.add(r.nextInt(Constant.number_of_blocks_in_a_row));
        }
        int count = 0;
        for(Integer index:set)
        {
            count += 1;
            Square block = new Square(block_width/2+index*block_width,-block_width/2,block_width,Constant.level);
            block.setSpeed(0,Constant.block_speed);
            if(count == add_ball_index)
            {
                block.ball = true;
                block.number = 1;
            }
            blocks.add(block);
        }
    }
}