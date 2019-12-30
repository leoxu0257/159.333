package com.example.martin.bubbles;

import android.graphics.Canvas;
import android.graphics.Path;

import java.util.ArrayList;

// square

public class SquareBubble extends BaseBubble {
  
  private Path path = new Path();
    public float angle = 0;

    public SquareBubble(float drawX, float drawY, float polygonR, int width, int height) {
        super(drawX, drawY, polygonR, width, height);
        points = new ArrayList<>();
        points.add(new Point(-polygonR, -polygonR));// add a point to the list
        points.add(new Point(-polygonR, polygonR));// calculate rest of points
        points.add(new Point(polygonR, polygonR));
        points.add(new Point(polygonR, -polygonR));
        initPoints(angle);
    }

    /**
     * rotate square
     */
    public void initPoints(float angle) {
        for (int i = 0; i < points.size(); i++) {
            Point point = rotationNewPoint(points.get(i), angle);
            points.set(i, point);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();// store canva's states
        canvas.translate(centerX, centerY);// Canvas move to the starting position of the bubble
        initPoints(angle);
        path.reset();
        path.moveTo(points.get(0).x, points.get(0).y);// this is the starting point of the polygon
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }
        path.close(); // Make these points a closed polygon
        canvas.drawPath(path, paint);
        canvas.restore();// restore last canva's save states
    }

    public Point getAbsolutePoint(Point point) {
        return new Point(point.x + centerX, point.y + centerY);
    }

    /**
     * Check if is inside
     */
    @Override
    public boolean isInside(float x, float y) {
      
        Point a = new Point(x, y);
        Point b = new Point(centerX, centerY);
        Point c = getAbsolutePoint(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            Point d = getAbsolutePoint(points.get(i));
            if (getIntersection(a, b, c, d) != 0) {// have intersection, not inside
                return false;
            }
            c = d;
        }
        Point d = getAbsolutePoint(points.get(0));
        if (getIntersection(a, b, c, d) != 0) {
            return false;// have intersection, not inside
        }
        return true;
    }

    public boolean isOnEdge(float x, float y) {
        
        Point a = new Point(x, y);
        Point c = getAbsolutePoint(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            Point d = getAbsolutePoint(points.get(i));
            if (getIntersection(c, d, a, a) == 1) {// There is an intersection ,on the edge
                return true;
            }
            c = d;
        }
        Point d = getAbsolutePoint(points.get(0));
        if (getIntersection(c, d, a, a) == 1) {
            return true;// There is an intersection on the edge
        }
        return false;
    }

    @Override
    public boolean isCollision(BaseBubble bubble) {
        boolean result = false;
        if(getIntersectDistance(bubble) <= (polygonR+bubble.polygonR)) {// If the center distance of two bubbles is greater than two "radius (distance to each vertex)", they will definitely not collide
            if (bubble instanceof SquareBubble) {
                for (Point point : ((SquareBubble) bubble).points) {// Find if there is a vertex inside or on the edge of another polygon
                    result = isInside(point.x + bubble.centerX, point.y + bubble.centerY);
                    if (result) {// Find a vertex inside another polygon, end the loop
                        break;
                    } else {
                        result = isOnEdge(point.x + bubble.centerX, point.y + bubble.centerY);
                        if (result) {// Find a vertex on the edge of another polygon, end the loop
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
        if (bubble instanceof SquareBubble) {
            for (Point point : ((SquareBubble) bubble).points) {
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
     * Overwrite bubble movement Reset the drawing start point and calculate whether it hits the boundary
     */
    public void move(float x, float y) {
        centerX = x;
        centerY = y;
      
        // x boundary
        if (isCollision(0, y)) {
            centerX = polygonR;
            mDx = -mDx;
        } else if (isCollision(mWidth, y)) {
            centerX = mWidth - polygonR;
            mDx = -mDx;
        }
      
        // y boundary
        if (isCollision(x, 0)) {
            centerY = polygonR;
            mDy = -mDy;
        } else if (isCollision(x, mHeight)) {
            centerY = mHeight - polygonR;
            mDy = -mDy;
        }
    }
}
