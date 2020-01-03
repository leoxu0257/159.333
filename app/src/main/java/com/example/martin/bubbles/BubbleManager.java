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
}
