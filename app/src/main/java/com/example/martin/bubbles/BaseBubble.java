package com.example.martin.bubbles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.List;

// base bubble

public abstract class BaseBubble {
    /**
     * vertex
     */
  protected List<Point> points;
  
  protected boolean isMove;
    /**
     * support the painting in the canvas after set the parameters
     */
  protected Paint paint;
    /**
     *  The center of horizontal axis of the bubble
     */ 
  protected float centerX;
    /**
     *   the center of vertical axis of bubble
     */
  protected float centerY;
  
    /**
     * Determine the object is dragged or not
     */
   protected boolean isDragDrop;
  
   protected float polygonR;
   protected float mDx = 16;
  
   protected float mDy = 16;
  
    /**
     * View's width and height
     */
  protected int mWidth;
  
  protected int mHeight;
  
  
  public BaseBubble(float drawX, float drawY, float polygonR, int width, int height) {
        this.centerX = drawX;
        this.centerY = drawY;
        this.polygonR = polygonR;
        this.mWidth = width;
        this.mHeight = height;
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setColor(Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
        this.paint.setStrokeWidth(4);
        this.paint.setTextSize(64);
        this.paint.setAntiAlias(true);
    }
   /**
     * change Hexagon and circle's colors
     */
  public void changeColor() {
        this.paint.setColor(Color.argb(255, (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
  }
   /**
     * create a abstract class to make the subclass implementation
     */
  public abstract void onDraw(Canvas canvas);
  
    
    //  @param x
     // @param y
     
  public void move(float x, float y) {
        centerX = x;
        centerY = y;
        //Determine whether the center of  horizontal and vertical axis are acceptable or not
        if (centerX < (polygonR)) {
            centerX = polygonR;
            mDx = -mDx;
        } else if (centerX > mWidth - polygonR) {
            centerX = mWidth - polygonR;
            mDx = -mDx;
        }
     
        if (centerY < (polygonR)) {
            centerY = polygonR;
            mDy = -mDy;
        } else if (centerY > (mHeight - polygonR)) {
            centerY = mHeight - polygonR;
            mDy = -mDy;
        }
    }
    
    /**
     * Check if it out of view's bounds
     */
    public boolean isCross(float x, float y) {
        if (x < polygonR) {
            return true;
        } else if (x > mWidth) {
            return true;
        }
        //check y coordinate below
        if (y < polygonR) {
            return true;
        } else if (y > mHeight) {
            return true;
        }
        return false;
    }
  

}
