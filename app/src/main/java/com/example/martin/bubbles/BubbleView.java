package com.example.martin.bubbles;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by martin on 19/09/17.
 */

public class BubbleView extends View {

    private BubbleManager mBubbleManager;

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mBubbleManager = new BubbleManager(MainActivity.TYPE, getWidth(), getHeight(), 10);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBubbleManager != null)
            mBubbleManager.onTouch(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas c) {
        if (mBubbleManager != null)
            mBubbleManager.onDraw(c);
    }


    public void update(float dt, boolean collide) {
        if (mBubbleManager != null)
            mBubbleManager.update(dt, collide);
    }
}
