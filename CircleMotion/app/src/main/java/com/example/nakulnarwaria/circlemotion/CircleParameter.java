package com.example.nakulnarwaria.circlemotion;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nakulNarwaria
 */


public class CircleParameter {

    private int circleIndex;
    private float radius=10;
    private float threshold;
    private float centerX;
    private float centerY;
    private static final int DELTA= 10;
    private float velocityXDirection = 0.0f;
    private float velocityYDirection = 0.0f;


    public CircleParameter(float xCenter, float yCenter, float threshold,int circleIndex){
        this.centerX = xCenter;
        this.centerY = yCenter;
        this.threshold = threshold;
        this.circleIndex = circleIndex;
    }

    public void incrementRadius(){
        if (this.radius+DELTA<=threshold)
            this.radius += DELTA;
    }

    public int getCircleIndex(){ return this.circleIndex; }

    public float getRadius(){ return radius; }

    public float getxCenter(){ return this.centerX; }

    public float getyCenter(){ return this.centerY; }

    public float getThreshold(){ return threshold; }

    public float getxVelocity() { return velocityXDirection; }

    public float getyVelocity() { return velocityYDirection; }

    public void setXcenter(float xCenter){ this.centerX = xCenter; }

    public void setyCenter(float yCenter){ this.centerY = yCenter; }

    public void setxVelocity(float xVelocity) { this.velocityXDirection = xVelocity; }

    public void setyVelocity(float yVelocity) { this.velocityYDirection = yVelocity; }

    public void setCircleVelocity(Canvas canvas, List<CircleParameter> circleList) {
        if(centerX - radius + velocityXDirection <= 0 || centerX + radius + velocityXDirection >= canvas.getWidth()) {
            velocityXDirection *= -1;
        }
        else if(centerY - radius + velocityYDirection <= 0 || centerY + radius + velocityYDirection >= canvas.getHeight()) {
            velocityYDirection *= -1;
        }

        centerX+=velocityXDirection;
        centerY+=velocityYDirection;

    }

    private boolean collisionBetweenCircles(CircleParameter objCircle, CircleParameter circle) {
        float distanceBetweenCenters = CircleHandler.findDistance(objCircle.getxCenter(),objCircle.getyCenter(),circle.getxCenter(),circle.getyCenter());
        float radiusSum = objCircle.getRadius() + circle.getRadius();
        return radiusSum>=distanceBetweenCenters;
    }
}
