package com.example.nakulnarwaria.circlemotion;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by nakulNarwaria
 */

public class CircleHandler extends View implements View.OnTouchListener {



    private static Paint black;
    private static List<CircleParameter> circleList;
    private int circleIndex = -1;
    private boolean inMovingState = false;
    private boolean longPressActive = false;

    private static float canvasWidth;
    private static float canvasHeight;
    private static float velocityXDirection;
    private static float velocityYDirectiion;

    private GestureDetectorCompat gestureDetectorCompat;
    private VelocityTracker velocityTracker;
    private TimerTask task;
    private Handler handler = new Handler();
    private static final int delayInFrame = 200;


    static {
        black = new Paint();
        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.STROKE);
        black.setStrokeWidth(12.0f);
        circleList = new ArrayList<CircleParameter>();
    }

    public class CircleHandlerGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            longPressActive = true;
            handleLongPress();
        }
    }

    public CircleHandler(Context context) {
        super(context);
        gestureDetectorCompat = new GestureDetectorCompat(context,new CircleHandlerGestureListener());
    }

    public CircleHandler(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        gestureDetectorCompat = new GestureDetectorCompat(context,new CircleHandlerGestureListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.CYAN);
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        int i=0;
        if (!circleList.isEmpty()){
            for (CircleParameter circle: circleList){
                //setCircleVelocity(canvas,circle);
                circle.setCircleVelocity(canvas,circleList);
                canvas.drawCircle(circle.getxCenter(),circle.getyCenter(),circle.getRadius(),black);
                i++;
            }
        }
        if(inMovingState){
            invalidate();
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;

        this.gestureDetectorCompat.onTouchEvent(motionEvent);

        switch (actionCode){
            case MotionEvent.ACTION_DOWN:
                return handleActionDown(motionEvent);
            case MotionEvent.ACTION_MOVE:
                return handleActionMove(motionEvent);
            case MotionEvent.ACTION_UP:
                return handleActionUp(motionEvent);
        }
        return false;
    }

    private boolean handleActionDown(MotionEvent motionEvent){
        if(circleList.isEmpty()){
            circleList.add(new CircleParameter(motionEvent.getX(),motionEvent.getY(),calculateThreshold(motionEvent),circleList.size()));
            return true;
        }
        velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(motionEvent);
        for (int i=0;i<circleList.size();i++){
            if(inRangeOfCircle(circleList.get(i),motionEvent.getX(),motionEvent.getY())){
                circleIndex = i;
                return true;
            }
        }
        circleList.add(new CircleParameter(motionEvent.getX(),motionEvent.getY(),calculateThreshold(motionEvent),circleList.size()));
        return true;
    }

    private boolean handleActionMove(MotionEvent motionEvent){
        if(circleIndex==-1) {
            circleList.get(circleList.size() - 1).incrementRadius();
        }else{
            inMovingState = true;
        }
        if(inMovingState){
            velocityTracker.addMovement(motionEvent);
        }
        invalidate();
        return true;
    }

    private void handleLongPress(){
        final CircleParameter circle= circleList.get(circleList.size()-1);
        task = new TimerTask() {

            @Override
            public void run() {
                if(circleIndex==-1){
                    circle.incrementRadius();
                }
                invalidate();
                handler.postDelayed(this, delayInFrame);
            }
        };
        handler.postDelayed(task, delayInFrame);
    }

    private boolean handleActionUp(MotionEvent motionEvent){
        if(longPressActive){
            handler.removeCallbacks(task);
        }
        if(inMovingState){
            computeCircleVelocity(motionEvent);
        }
        if(circleIndex!=-1){
            circleIndex = -1;
        }
        invalidate();
        return true;
    }


    private float minimumDistanceFromWall(MotionEvent motionEvent){
        float minimumDistanceFromWall = Float.MAX_VALUE;
        float []distancesFromWall=new float[4];

        distancesFromWall[0]= motionEvent.getX(); //left wall
        distancesFromWall[1]= canvasWidth-motionEvent.getX(); //right wall
        distancesFromWall[2]=motionEvent.getY(); //upper wall
        distancesFromWall[3]=canvasHeight-motionEvent.getY(); //below wall

        for(int i=0;i<distancesFromWall.length;i++){
            if(minimumDistanceFromWall>distancesFromWall[i])
                minimumDistanceFromWall=distancesFromWall[i];
        }
        return minimumDistanceFromWall;
    }

    public static float findDistance(float startX,float startY, float endX,float endY){
        return (float)Math.sqrt(Math.pow(Math.abs(startX-endX),2) + Math.pow(Math.abs(startY-endY),2));
    }

    public boolean inRangeOfCircle(CircleParameter circle,float touchPointX,float touchPointY){
        float distanceFromCenter = findDistance(circle.getxCenter(),circle.getyCenter(),touchPointX,touchPointY);
        return distanceFromCenter<=circle.getRadius();
    }

    private void computeCircleVelocity(MotionEvent motionEvent) {
        velocityTracker.addMovement(motionEvent);
        velocityTracker.computeCurrentVelocity(1);
        velocityXDirection = velocityTracker.getXVelocity();
        velocityYDirectiion = velocityTracker.getYVelocity();
        if(circleIndex!=-1){
            circleList.get(circleIndex).setxVelocity(velocityXDirection);
            circleList.get(circleIndex).setyVelocity(velocityYDirectiion);
        }

    }


    private float calculateThreshold(MotionEvent motionEvent){
        float threshold = minimumDistanceFromWall(motionEvent);
        if(!circleList.isEmpty()){
            for (CircleParameter circle: circleList){
                float distanceFromCurrentCircle= findDistance(circle.getxCenter(),circle.getyCenter(),motionEvent.getX(),motionEvent.getY()) - circle.getRadius();
                if(distanceFromCurrentCircle>0 & distanceFromCurrentCircle<threshold)
                    threshold = distanceFromCurrentCircle;
            }
        }
        return threshold;
    }



}
