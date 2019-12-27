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
    //@param dt
  
    public void move(float dt) {
        if (!isDragDrop) {
            // move the center location
            centerX += mDx * dt;
            centerY += mDy * dt;
            move(centerX, centerY);
        }
    }
    // create a abstract class to judge whether the horizontal and vertical axis inside the bubble or not
     public abstract boolean isInside(float x, float y);
    
    
        public float getSpeed() {
        float speed = (float) Math.sqrt(mDx * mDx + mDy * mDy);
        return speed;
    }
    //calculate the distance between two center positions
    public float getCenterDistance(BaseBubble buble) {
        return (float) Math.sqrt((centerX - buble.centerX) * (centerX - buble.centerX) + (centerY - buble.centerY) * (centerY - buble.centerY));
    }
     public abstract float getIntersectDistance(BaseBubble buble);
    
    //judge the data is 0 or not
     public boolean isZero(float delta) {
        return delta <= (1e-6) && delta >= -(1e-6);
    }
    
    private float determinant(float v1, float v2, float v3, float v4) {
        return (v2 * v3 - v1 * v4);
    }
    
    //determine if a point is in a line
      private boolean isOnLine(Point point1, Point point2, Point point) {
        float x = point.x;
        float y = point.y;
        return (x - point1.x) * (point1.y - point2.y) == (point1.x - point2.x)
                * (y - point1.y);
    }
    
      private float mult(Point a, Point b, Point c) {
        return (a.x - c.x) * (b.y - c.y) - (b.x - c.x) * (a.y - c.y);
    }
    
    
    //aa and bb are the start point and  endpoint in the line
    // cc and dd are start point and endpoint in another line
    // If the two lines intersect，return true. otherwise,return false
     boolean intersect(Point aa, Point bb, Point cc, Point dd) {
        if (Math.max(aa.x, bb.x) < Math.min(cc.x, dd.x)) {
            return false;
        }
        if (Math.max(aa.y, bb.y) < Math.min(cc.y, dd.y)) {
            return false;
        }
        if (Math.max(cc.x, dd.x) < Math.min(aa.x, bb.x)) {
            return false;
        }
        if (Math.max(cc.y, dd.y) < Math.min(aa.y, bb.y)) {
            return false;
        }
        if (mult(cc, bb, aa) * mult(bb, dd, aa) < 0) {
            return false;
        }
        if (mult(aa, dd, cc) * mult(dd, bb, cc) < 0) {
            return false;
        }
        return true;
    }
    /**judge the 2 line is interset or not
    a: coordinates of starting point
    b: coordinates of endpoint
    
    c: coordinates of starting point in second line
    d: coordinates of endpoint in second line
    
    if there is not any point of intersection, return 0
    if  only one point of intersection, return 1
    if there is 2 point of intersection(Two line segments coincide), return 2
     */
     public int getIntersection(Point a, Point b, Point c, Point d) {
         //Sort two line segments based on the size of the X axis
        if (a.x > b.x) {
            Point temp = a;
            a = b;
            b = temp;
        }
        if (c.x > d.x) {
            Point temp = c;
            c = d;
            d = temp;
        }
        float delta = Math.abs(b.y - a.y) + Math.abs(b.x - a.x) + Math.abs(d.y - c.y)
                + Math.abs(d.x - c.x);
        if (isZero(delta)) {
            if (isZero((c.x - a.x) + (c.y - a.y))) {
                Log.d("getIntersection", "ABCD is same point");
                return 1;
            } else {
                Log.d("getIntersection", "AB is same point，CD is same point，alse AC is different！");
            }
            return 0;
        }
        if (isZero(Math.abs(b.y - a.y) + Math.abs(b.x - a.x))) {
            if (isZero((a.x - d.x) * (c.y - d.y) - (a.y - d.y) * (c.x - d.x)) && (a.x >= c.x && a.x <= d.x)) {
                Log.d("getIntersection", "A、B is same point，this point is in the line segment(CD)！");
                return 1;
            } else {
                Log.d("getIntersection", "A、B is same point，and it is not in the CD！");
            }
            return 0;
        }
        if (isZero(Math.abs(d.y - c.y) + Math.abs(d.x - c.x))) {
            if (isZero((d.x - b.x) * (a.y - b.y) - (d.y - b.y) * (a.x - b.x))
                    && (c.x >= a.x && c.x <= b.x)) {
                Log.d("getIntersection", "C、D is same point，this point is in the line segment(AB)！");
                return 1;
            } else {
                Log.d("getIntersection", "C、D is same point，and it is not in the AB！！");
            }
            return 0;
        }
        delta = determinant(b.x - a.x, c.x - d.x, b.y - a.y, c.y - d.y);
        if (isZero(delta)) { 
            // delta=0 means 2 line segments are parallel or coincide
            if (a.x != b.x) {
                float k = (b.y - a.y) / (b.x - a.x);
                float b1 = b.y - b.x * k;
                float b2 = c.y - c.x * k;
                if (Math.abs(b2 - b1) <= 2 * Math.abs(mDy)) {
                    //y = kx+b; if the b is same, the two line segments are in the same straight line 
                    if ((c.x < b.x && d.x > b.x) || (a.x < d.x && b.x > d.x)) {
                        return 2;
                    }
                    if (c.x == b.x || a.x == d.x) {
                        return 1;
                    }
                }
            } else if (a.x == c.x) {
                float y1 = Math.min(a.y, b.y);
                float y2 = Math.max(a.y, b.y);
                float y3 = Math.min(c.y, d.y);
                float y4 = Math.max(c.y, d.y);
                if ((y3 < y2 && y4 > y2) || (y1 < y4 && y2 > y4)) {
                    return 2;
                }
                if (y1 == y3 || y2 == y4) {
                    return 1;
                }
            }
            return 0;
        }
        if (intersect(a, b, c, d)) {
            return 1;
        }
        return 0;
    }
    
    
}
