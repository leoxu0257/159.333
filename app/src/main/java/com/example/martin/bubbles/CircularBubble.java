package com.example.martin.bubbles;

import android.graphics.Canvas;

public class CircularBubble extends BaseBubble {
  
  public CircularBubble(float drawX, float drawY, float polygonR, int width, int height) {
        super(drawX, drawY, polygonR, width, height);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();// store canvas states
        canvas.translate(centerX, centerY);// Canvas move to the starting position of the bubble
        canvas.drawCircle(0, 0, polygonR, paint);// draw circle
        canvas.restore();// restore last save states
    }

    /**
     * Check if is inside
     */
    @Override
    public boolean isInside(float x, float y) {
        return ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)) < (polygonR * polygonR);
    }

    @Override
    public boolean isCollision(BaseBubble bubble) {
        return getCenterDistance(bubble) <= polygonR + bubble.polygonR;
    }

    @Override
    public float getIntersectDistance(BaseBubble bubble) {
        return polygonR + bubble.polygonR - getCenterDistance(bubble);
    }
  
}
