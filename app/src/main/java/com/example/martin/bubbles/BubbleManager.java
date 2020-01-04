package com.example.martin.bubbles;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//manager class as a factory
public class BubbleManager {
    /**
     * Bubble Array
     */
    private List<BaseBubble> mBubbleList;
    /**
     * touched bubble
     */
    private BaseBubble mBubble;
    /**
     * record the time of the last draw
     */
    private long mLastTime;

    private Paint mTextPaint = new Paint();

    private int mWidth;

    private int mHeight;

    // Create random number
    private Random mRandom;

    private BubbleType mBubbleType;

    private long touchTime;
    /**
     * The touch position x
     */
    private float touchX;
    /**
     * The touch position y
     */
    private float touchY;

    public BubbleManager(BubbleType bubbleType, int width, int height, int num) {
        mWidth = width;
        mHeight = height;
        mBubbleList = new ArrayList<>();
        mRandom = new Random();
        mBubbleType = bubbleType;
        int column = (int) Math.sqrt(num);
        if (column * column < num) {
            column++;
        }
        int ceilSize = (width / column);
        for (int i = 0; i < num; i++) {
            int polygonR = mRandom.nextInt(ceilSize / 2) + 10;//to get random positive integer from 10 to 109
            /**
             * position doesn't overlap
             */
            int startX = ceilSize / 2 + (i % column) * ceilSize;
            int startY = ceilSize / 2 + (i / column) * ceilSize;

            BaseBubble bubble = null;
            
            // deal with factory
            switch (bubbleType) {
                case TRIANGLE:
                    bubble = new TriangleBubble(startX, startY, polygonR, width, height);
                    break;
                case SQUARE:
                    bubble = new SquareBubble(startX, startY, polygonR, width, height);
                    break;
                case HEXAGON:
                    bubble = new HexagonBubble(startX, startY, polygonR, width, height);
                    break;
                case CIRCULAR:
                    bubble = new CircularBubble(startX, startY, polygonR, width, height);
                    break;
            }
            if (bubble != null) {
                mBubbleList.add(bubble);// save to the bubble array
            }
        }

        /**
         * Initialization draw fps setting
         */
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextSize(48);/
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    /**
     * Draw every bubble
     */
    public void onDraw(Canvas canvas) {
        //go through each bubble
        for (BaseBubble bubble : mBubbleList) {
            bubble.onDraw(canvas);
        }
        onDrawFPS(canvas);
    }

    /**
     * Touch event
     */
    public void onTouch(MotionEvent event) {
        float x = event.getX();//get x position of the event
        float y = event.getY();//get x position of the event
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchTime = System.currentTimeMillis();//get current time
                touchX = x;
                touchY = y;
                for (BaseBubble bubble : mBubbleList) {
                    if (bubble.isInside(x, y)) {//Check for which bubble is clicked
                        mBubble = bubble;
                        mBubble.isDragDrop = true;
                        break;//find bubble end loop
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mBubble != null) {
                    mBubble.isDragDrop = false;
                    mBubble = null;//reset the draged bubble as null
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBubble != null) {
                    float tempX = mBubble.centerX;
                    float tempY = mBubble.centerY;
                    mBubble.move(x, y);//Bubble moved by the finger
                    long dragTime = System.currentTimeMillis() - touchTime;//Drag time
                    mBubble.mDx = (x - touchX) / dragTime;//speed
                    mBubble.mDy = (y - touchY) / dragTime;
                    touchX = x;
                    touchY = y;
                    touchTime = System.currentTimeMillis();
                    if (!moveEnable(mBubble)) {
                        mBubble.move(tempX, tempY);//Go back to the old position
                    }
                }
                break;
        }
    }

    /**
     *
     */
    private void onDrawFPS(Canvas canvas) {
        long time = System.nanoTime();
        long dt = time - mLastTime;
        float fps = 0;
        if (dt != 0) fps = 1000000000.0f / dt;
        canvas.drawText(String.format("FPS:%.2s", fps), 16, 192, mTextPaint);
        mLastTime = time;
    }
    
    /**
     * update bubble
     */
    public void update(float dt, boolean isCollide) {
        for (BaseBubble bubble : mBubbleList) {
            bubble.move(dt);
        }
        if (isCollide) {
            collide();
        }
    }

    /**
     * Collision speed loss coefficient 
     */
    float mDampening = 0.0f;

    /**
     * Collision detection
     */
    private void collide() {
        List<BaseBubble> temp = new ArrayList<>();// list of bubbles
        for (BaseBubble bubble : mBubbleList) {
            if (bubble.isMove) {
                temp.add(bubble);
                continue;
            }
            handleCollide(bubble);// Check if a bubble collides with other bubbles
        }
        for(BaseBubble bubble:temp){
            mBubbleList.remove(bubble);// remove bubble from the list (delete bubbles) 
        }
    }
    /**
     * Check if that bubble can be moved to finger's position
     */
    private boolean moveEnable(BaseBubble bubble1) {
        for (BaseBubble bubble2 : mBubbleList) {
            if (bubble1 != bubble2 && bubble1.getIntersectDistance(bubble2) > 0) {//The distance overlapping > 0
                return false;
            }
        }
        return true;
    }

    /**
     * handle for collide problem
     */
    private void handleCollide(BaseBubble bubble1) {
        for (BaseBubble bubble2 : mBubbleList) {
            if (bubble1 != bubble2) {
                if (bubble2.isMove) {
                    continue;
                }
                if (bubble1.isCollision(bubble2)) {//Two bubble collision
                    if (mBubbleType == BubbleType.TRIANGLE && (((TriangleBubble) bubble1).isEdgeContact(bubble2))) {//Triangle edge touched
                        if (bubble1.polygonR > bubble2.polygonR) {//Edge touched bigger bubble eat smaller bubble
                            bubble1.centerX = (bubble1.centerX + bubble2.centerX) / 2;//New bubble show in the middle
                            bubble1.centerY = (bubble1.centerY + bubble2.centerY) / 2;
                            bubble1.polygonR += bubble2.polygonR;//New bubble bigger
                            bubble2.isMove = true;
                            ((TriangleBubble) bubble1).initPoints(0);
                        } else {
                            bubble2.centerX = (bubble1.centerX + bubble2.centerX) / 2;
                            bubble2.centerY = (bubble1.centerY + bubble2.centerY) / 2;
                            bubble2.polygonR += bubble1.polygonR;
                            bubble1.isMove = true;
                            ((TriangleBubble) bubble2).initPoints(0);
                        }
                        continue;
                    }
                    if (bubble1.isDragDrop) {//If bubble is draging by finger then collision
                        bubble2.mDx = -bubble2.mDx;
                        bubble2.mDy = -bubble2.mDy;
                        while (bubble1.getIntersectDistance(bubble2) > 0) {
                            bubble2.move(1);
                        }
                    } else if (bubble2.isDragDrop) {
                        bubble1.mDx = -bubble1.mDx;
                        bubble1.mDy = -bubble1.mDy;
                        while (bubble1.getIntersectDistance(bubble2) > 0) {
                            bubble1.move(1);
                        }
                    } else {
                        /**
                         * M = R^2 calculate speed
                         */
                        float x[] = getSpeed(bubble1.polygonR * bubble1.polygonR, bubble2.polygonR * bubble2.polygonR, bubble1.mDx, bubble2.mDx);
                        float y[] = getSpeed(bubble1.polygonR * bubble1.polygonR, bubble2.polygonR * bubble2.polygonR, bubble1.mDy, bubble2.mDy);
                        bubble1.mDx = x[0];
                        bubble2.mDx = x[1];
                        bubble1.mDy = y[0];
                        bubble2.mDy = y[1];
                        while (bubble1.getIntersectDistance(bubble2) > 0) {//If bubble move will touch boundary, move bubble 1
                            if (bubble2.isCross(bubble2.centerX + bubble2.mDx, bubble2.centerY + bubble2.mDy)) {
                                bubble1.move(bubble1.centerX + bubble1.mDx, bubble1.centerY + bubble1.mDy);
                            } else {
                                bubble2.move(bubble2.centerX + bubble2.mDx, bubble2.centerY + bubble2.mDy);
                            }
                        }
                    }
                    if (mBubbleType == BubbleType.SQUARE) {//Square spin
                        ((SquareBubble) bubble1).angle = bubble1.isClockwise() ? 2 : -2;
                        ((SquareBubble) bubble2).angle = bubble2.isClockwise() ? 2 : -2;
                    }
                }
            }
        }
    }

    /**
     * Calculate velocity after collision without energy loss based on conservation of energy and impulse
     */
    private float[] getSpeed(float m1, float m2, float v1, float v2) {
        float totalMv = m1 * v1 + m2 * v2;// Impulse before collision
        float totalMv2 = (m1 * v1 * v1 + m2 * v2 * v2);// did not multipled by 1/2
        float v[] = new float[2];// Two speeds after collision

        // Elimination below
        float a = m1 * m2 + m1 * m1;
        float b = -2 * totalMv * m1;
        float c = totalMv * totalMv - totalMv2 * m2;
        float d = (b * b - 4 * a * c);
        if (a == 0) {
            v[0] = -c / b;
            v[1] = -c / b;
        } else if (d >= 0) {// have root
            float tempV1 = (float) (-b + Math.sqrt(d)) / (2 * a);
            float tempV2 = (float) (-b - Math.sqrt(d)) / (2 * a);
            if (Math.abs(tempV1 - v1) <= 0.001) {// There are two roots in the calculation. If one root is the same as before, don't use this root.
                v[0] = tempV2;
            } else {
                v[0] = tempV1;
            }
            v[1] = (totalMv - m1 * v[0]) / m2;
        }
        v[0] = (1 - mDampening) * v[0];// Multiplied by a collision velocity loss coefficient
        v[1] = (1 - mDampening) * v[1];
        if (v[0] == 0) {
            v[0] = 1;
        }
        if (v[1] == 0) {
            v[1] = 1;
        }
        return v;
    }
}
