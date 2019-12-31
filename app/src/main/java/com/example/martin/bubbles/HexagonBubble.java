package com.example.martin.bubbles;

import android.graphics.Canvas;
import android.graphics.Path;

import java.util.ArrayList;

public class HexagonBubble extends BaseBubble {
  
  private Path path = new Path();

    public HexagonBubble(float drawX, float drawY, float polygonR, int width, int height) {
        super(drawX, drawY, polygonR, width, height);
        points = new ArrayList<>();
        initPoints();
    }

    private void initPoints() {
      //add a point
        points.add(new Point(-polygonR / 2, (float) (polygonR * Math.sqrt(3) / 2)));
      //Calculate the coordinates of the second point based on Hexagon
        points.add(new Point(polygonR / 2, (float) (polygonR * Math.sqrt(3) / 2)));
        points.add(new Point(polygonR, 0));
        points.add(new Point(polygonR / 2, (float) (-polygonR * Math.sqrt(3) / 2)));
        points.add(new Point(-polygonR / 2, (float) (-polygonR * Math.sqrt(3) / 2)));
        points.add(new Point(-polygonR, 0));
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
      //Move canvas to the starting position of the bubble
        canvas.translate(centerX, centerY);
        path.reset();
      //This is the starting point of this Hexagon
        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }
        path.close(); 
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    /**
     *get aboulute point
     
     * @param point
     * @return
     */
    public Point getAbsolutePoint(Point point) {
        return new Point(point.x + centerX, point.y + centerY);
    }

    /**
     * Check the coordinates are inside the bubble or not
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public boolean isInside(float x, float y) {
       //Calculate whether they have intersections or not
        Point a = new Point(x, y);
        Point b = new Point(centerX, centerY);
        Point c = getAbsolutePoint(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            Point d = getAbsolutePoint(points.get(i));
          //Intersection is not inside
            if (getIntersection(a, b, c, d) != 0) {
                return false;
            }
            c = d;
        }
        Point d = getAbsolutePoint(points.get(0));
        if (getIntersection(a, b, c, d) != 0) {
            return false;
        }
        return true;
    }

    /**
     * Check the coordinates are on the Hexagon or not
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isOnEdge(float x, float y) {
      
        Point a = new Point(x, y);
        Point c = getAbsolutePoint(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            Point d = getAbsolutePoint(points.get(i));
          //One intersection 
            if (getIntersection(c, d, a, a) == 1) {
                return true;
            }
            c = d;
        }
      //make a line by connect the last point and start point
        Point d = getAbsolutePoint(points.get(0));
        if (getIntersection(c, d, a, a) == 1) {
            return true;
        }
        return false;
    }

    /**
     *bool is collision
     * @param bubble
     * @return
     */
    @Override
    public boolean isCollision(BaseBubble bubble) {
        boolean result = false;
      //never collision if distance between the center points of two bubbles  greater than their radius
        if(getIntersectDistance(bubble) <= (polygonR+bubble.polygonR)) {
            if (bubble instanceof HexagonBubble) {
              //look for any point inside or on edge of the another hexagon
                for (Point point : ((HexagonBubble) bubble).points) {
                    result = isInside(point.x + bubble.centerX, point.y + bubble.centerY);
                  //find a point inside the another hexagon
                    if (result) {
                        break;
                    } else {
                        result = isOnEdge(point.x + bubble.centerX, point.y + bubble.centerY);
                      //find a point on edge of the another hexagon and finish the loop statement
                        if (result) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Override
    public float getIntersectDistance(BaseBubble bubble) {
        boolean result = false;
        if (bubble instanceof HexagonBubble) {
            for (Point point : ((HexagonBubble) bubble).points) {
                result = isInside(point.x + bubble.centerX, point.y + bubble.centerY);
                if (result) {
                    break;
                }
            }
        }
        if (result) {
            return 1;
        }
        return 0;
    }

    /**
     * move()，reset its starting point, and judge whether it collide the border or not
     *
     * @param x
     * @param y
     */
    public void move(float x, float y) {
        centerX = x;
        centerY = y;
        if (isCollision(0, y)) {
            centerX = polygonR;
            mDx = -mDx;
            changeColor();
        } else if (isCollision(mWidth, y)) {
            centerX = mWidth - polygonR;
            mDx = -mDx;
            changeColor();
        }
        if (isCollision(x, 0)) {
            centerY = polygonR;
            mDy = -mDy;
            changeColor();
        } else if (isCollision(x, mHeight)) {
            centerY = mHeight - polygonR;
            mDy = -mDy;
            changeColor();
        }
    }
}
