package com.example.martin.bubbles;

import android.graphics.Canvas;
import android.graphics.Path;

import java.util.ArrayList;
import java.util.Random;

public class TriangleBubble extends BaseBubble {

    private Path path = new Path();
    private float angle;

    public TriangleBubble(float drawX, float drawY, float polygonR, int width, int height) {
        super(drawX, drawY, polygonR, width, height);
        points = new ArrayList<>();
        initPoints(new Random().nextInt(2) == 1 ? 60 : 0);
}

    public void initPoints(float angle) {
        this.angle = angle;
        points.clear();
        points.add(new Point(-(float) (Math.sqrt(3) * polygonR) / 2, polygonR / 2));//add a point
        points.add(new Point((float) (Math.sqrt(3) * polygonR) / 2, polygonR / 2));//calculate the other point(Equilateral triangle)
        points.add(new Point(0, -polygonR));
        for (int i = 0; i < points.size(); i++) {
            Point point = rotationNewPoint(points.get(i), angle);
            points.set(i, point);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();//save states of the canvas
        canvas.translate(centerX, centerY);//Canvas move to the start position of the bubble
        path.reset();

        path.moveTo(points.get(0).x, points.get(0).y);// The starting point
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }
        path.close(); // Make a closed triangle
        canvas.drawPath(path, paint);
        canvas.restore();//restore the canvas to the last saving states
    }

    /**
     * check if the position is inside of the triangle
     **/
    @Override
    public boolean isInside(float x, float y) {
        /**
         * Check if the line which link from this point to the triangle center, have noun or not.
         */
        Point a = new Point(x, y);
        Point b = new Point(centerX, centerY);
        Point c = getAbsolutePoint(points.get(0));

        for (int i = 1; i < points.size(); i++) {
            Point d = getAbsolutePoint(points.get(i));
            //If there is noun, the point is not inside
            if (getIntersection(a, b, c, d) != 0) {
                return false;
            }
            c = d;
        }
        Point d = getAbsolutePoint(points.get(0));
        if (getIntersection(a, b, c, d) != 0) {
            return false;//If there is noun, the point is not inside
        }
        return true;
    }

    /**
     * Check for collision
     */
    @Override
    public boolean isCollision(BaseBubble bubble) {
        boolean result = false;
        //If the distence between two bubble's certer point greater than the sum of two bubble's radious
        //then collision will not happen
        if (getIntersectDistance(bubble) <= (polygonR + bubble.polygonR)) {
            if (bubble instanceof TriangleBubble) {
                //Check if a vertex is inside or on the edge of another polygon
                for (Point point : ((TriangleBubble) bubble).points) {
                    result = isInside(point.x + bubble.centerX, point.y + bubble.centerY);
                    //If found end loop
                    if (result) {
                        break;
                    } else {
                        result = isOnEdge(point.x + bubble.centerX, point.y + bubble.centerY);
                        //If found end loop
                        if (result) {
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
    /**
     * Check edge contact
     */
    public boolean isEdgeContact(BaseBubble bubble) {
        //Angle have to be different
        if (((TriangleBubble) bubble).angle != angle) {
            Point top1, top2;
            //Angle is 0
            if (isZero(angle)) {
                top1 = points.get(2);
                top2 = bubble.points.get(1);
            } else {
                top1 = points.get(1);
                top2 = bubble.points.get(2);
            }
            //Not two angle contact
            if (top1.x != top2.x) {
                return true;
            }
        }
        return false;
    }

    @Override
    public float getIntersectDistance(BaseBubble bubble) {
        boolean result = false;
        if (bubble instanceof TriangleBubble) {
            for (Point point : ((TriangleBubble) bubble).points) {
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
     * Reset starting point and check if bubble collide with the border
     */
    public void move(float x, float y) {
        centerX = x;
        centerY = y;
        if (isCollision(0, y)) {
            centerX = polygonR;
            mDx = -mDx;
        } else if (isCollision(mWidth, y)) {
            centerX = mWidth - polygonR;
            mDx = -mDx;
        }
        if (isCollision(x, 0)) {
            centerY = polygonR;
            mDy = -mDy;
        } else if (isCollision(x, mHeight)) {
            centerY = mHeight - polygonR;
            mDy = -mDy;
        }
    }
}



