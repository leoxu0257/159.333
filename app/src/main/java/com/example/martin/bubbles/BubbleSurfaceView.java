package com.example.martin.bubbles;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class BubbleSurfaceView extends SurfaceView {

    private BubbleManager mBubbleManager;

    public BubbleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {// This method is automatically called by the system when the layout changes or the first layout get used
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {// The first time the drawing or layout changes, changed is true
            mBubbleManager = new BubbleManager(MainActivity.TYPE, getWidth(), getHeight(), 10);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {// This method is called by the touch screen system
        if (mBubbleManager != null) {
            mBubbleManager.onTouch(event);
        }
        return true;
    }

    @Override
    public void draw(Canvas c) {// This method is called automatically by system
        super.draw(c);
        if (mBubbleManager != null) {
            mBubbleManager.onDraw(c);// draw bubble
        }
    }

    /**
     * update bubble 
     */
    public void update(float dt, boolean collide) {
        if (mBubbleManager != null) {
            mBubbleManager.update(dt, collide);
        }
    }
}
