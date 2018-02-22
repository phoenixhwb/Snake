package com.game.whuang.snake;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.TimerTask;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler MHandler = new Handler();
        InitialMap();
    }

    Handler MHandler =new Handler();

    private int IDTarget = 2000;
    private int[] PositionTarget={0,0};


    private int IDSnakeHead = 1000;
    private int LengthSnake = 0;
    private ArrayList<int[]> PositonSnake = new ArrayList<>();
    private String SnakeFoward="Left";


    private int BlockSize = 0;
    private RelativeLayout Map;

    private Boolean OnRrefresh=false;
    //
    private void InitialMap()
    {
        Map = (RelativeLayout)findViewById(R.id.Map);
        WindowManager WM = this.getWindowManager();
        int WinWidth  = WM.getDefaultDisplay().getWidth();

        BlockSize = WinWidth/40;

        Map.setMinimumWidth(WinWidth);
        Map.setMinimumHeight(WinWidth);
        Map.setBackgroundColor(Color.LTGRAY);

        InitialGame();
    }

    private void InitialGame()
    {
        LengthSnake=0;
        Map.removeAllViews();
        PositonSnake.clear();
        int[] Position = {0, 20};

        for (int i = 0; i < 5; i++)
        {
            AddSnake(20 + i, 20);
            Position[0] = 20 + i;
        }
        SnakeFoward="Left";
        AddTarget();
    }

    private void SetBlockPosition(TextView VBlock,int x,int y)
    {
        VBlock.setX(x*BlockSize);
        VBlock.setY(y*BlockSize);
    }

    private void AddSnake(int x,int y)
    {
        TextView T =new TextView(this);
        ViewGroup.LayoutParams VL = new ViewGroup.LayoutParams(BlockSize-1,BlockSize-1);
        T.setLayoutParams(VL);

        if (LengthSnake == 0)
        {
            T.setId(IDSnakeHead);
            T.setBackgroundColor(Color.RED);
        }
        else
        {
            T.setId(IDSnakeHead + LengthSnake);
            T.setBackgroundColor(Color.BLUE);
        }
        Map.addView(T);
        SetBlockPosition(T,x,y);
        if(LengthSnake<5)
        {
            int[] Position = {x,y};
            PositonSnake.add(Position);
        }
        LengthSnake++;
    }

    private void AddTarget()
    {
        Random Random = new Random();
        int x=0;
        int y=0;
        int i = 1;
        while(i==1)
        {
            i=0;
            x = Random.nextInt(39);
            y = Random.nextInt(39);
            for (int[] P : PositonSnake)
            {
                if (x == P[0]&&y==P[1]) i=1;
            }
        }
        TextView A = findViewById(IDTarget);
        if (A==null)
        {
            A = new TextView(this);
            ViewGroup.LayoutParams VL = new ViewGroup.LayoutParams(BlockSize,BlockSize);
            A.setLayoutParams(VL);
            A.setId(IDTarget);
            A.setBackgroundColor(Color.GREEN);
            Map.addView(A);
       }
       else
        {
            A = findViewById(IDTarget);
        }

        PositionTarget[0]=x;
        PositionTarget[1]=y;
        SetBlockPosition(A,x,y);

    }

    private void SnakeMove(String Direction)
    {
        int x = PositonSnake.get(0)[0];
        int y = PositonSnake.get(0)[1];
        switch(Direction)
        {
            case "Up":
                y--;
                break;
            case "Down":
                y++;
                break;
            case "Left":
                x--;
                break;
            case "Right":
                x++;
                break;
        }

        HeadPositionVerify(x,y);
        Log.d("Position","X:"+Integer.toString(x)+"  Y:"+Integer.toString(y));
        SnakeFoward=Direction;

    }

    private void HeadPositionVerify(int x,int y)
    {
        int SnakeStatus = 0;

        if (x == PositionTarget[0] && y == PositionTarget[1])
        {
            SnakeStatus = 1;
        }

        else if (x < 0 || x > 39 || y < 0 || y > 39)
        {
            SnakeStatus = 2;
        }

        else
        {
            for (int i = 1; i < LengthSnake - 1; i++)
            {
                if (x == PositonSnake.get(i)[0] && y == PositonSnake.get(i)[1])
                {
                    SnakeStatus = 2;
                    break;
                }
            }
        }

        HeadPositionHandle(SnakeStatus,x,y);
    }

    private void HeadPositionHandle(int SnakeStatus,int x,int y)
    {
        TextView TSnakeHead = findViewById(IDSnakeHead);
        switch (SnakeStatus)
        {
            case 1:
                AddTarget();
                AddSnake(PositonSnake.get(LengthSnake-1)[0],PositonSnake.get(LengthSnake-1)[1]);

            case 0:
                SetBlockPosition(TSnakeHead,x,y);
                for(int i = 1; i < LengthSnake ; i++)
                {
                    int IDBody = IDSnakeHead + i;
                    TextView TBody = findViewById(IDBody);
                    SetBlockPosition(TBody,PositonSnake.get(i-1)[0],PositonSnake.get(i-1)[1]);
                }
                int[] Position={x,y};
                PositonSnake.add(0,Position);
                break;

            case 2:
                Gameover();
                break;
        }
    }

    private void Gameover()
    {
        EnableRefresh(false);

        InitialGame();
    }

    private GestureDetector.SimpleOnGestureListener MyGestureListener =new GestureDetector.SimpleOnGestureListener()
    {

        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("GestureDemoView", "onSingleTapUp() ");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            Log.d("GestureDemoView", "onScroll() distanceX = " + distanceX);
            Log.d("GestureDemoView", "onScroll() distanceY = " + distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d("GestureDemoView", "onFling() velocityX = " + velocityX);
            Log.d("GestureDemoView", "onFling() velocityY = " + velocityY);

            if(Math.abs(velocityX)>Math.abs(velocityY))
            {
                if (velocityX > 0)
                {
                    if (SnakeFoward != "Left")
                        SnakeFoward = "Right";
                    if(!OnRrefresh)EnableRefresh(true);
                }
                else
                {   if (SnakeFoward != "Right")
                        SnakeFoward = "Left";
                    if(!OnRrefresh)EnableRefresh(true);
                }
            }
            else
            {
                if (velocityY > 0)
                {
                    if (SnakeFoward != "Up")
                        SnakeFoward = "Down";
                    if(!OnRrefresh)EnableRefresh(true);
                }
                else
                {   if (SnakeFoward !="Down")
                        SnakeFoward = "Up";
                    if(!OnRrefresh)EnableRefresh(true);
                }
            }
            if(!OnRrefresh)EnableRefresh(true);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

     };

    private GestureDetector GD = new GestureDetector(MyGestureListener) ;

    public boolean onTouchEvent(MotionEvent E)
    {
        GD.onTouchEvent(E);
        return super.onTouchEvent(E);

    }


    Runnable MRun =new Runnable()
    {
        @Override
        public void run() {
            SnakeMove(SnakeFoward);
        }
    };

    Timer MTimer;

    public void EnableRefresh(boolean Start)
    {

        if(Start)
        {
            MTimer=new Timer();
            TimerTask Refresh = new TimerTask() {
                @Override
                public void run() {
                    MHandler.post(MRun);
                }
            };
            MTimer.schedule(Refresh, 0, 200);
            OnRrefresh=true;
        }
        else
        {
            MTimer.cancel();
            OnRrefresh=false;
        }

    }
}
