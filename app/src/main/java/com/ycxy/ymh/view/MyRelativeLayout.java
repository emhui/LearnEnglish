package com.ycxy.ymh.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Y&MH on 2018-1-9.
 */

public class MyRelativeLayout extends RelativeLayout {

    private static final String TAG = "MyRelativeLayout";

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private double startX;
    private double endX;
    private double distanceX;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                Log.d(TAG, "onTouchEvent: " + startX);
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                distanceX = endX - startX;
                Log.d(TAG, "onTouchEvent: " + endX + "--distanceX --" + distanceX);
                if (Math.abs(distanceX) > 100) {
                    onSwipeListener.setOnSwipeListener(distanceX);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    public  OnSwipeListener onSwipeListener;
    public  OnClickListener onClickListener;

    public void setOnSwipeListener(OnSwipeListener onSwipeListener){
        this.onSwipeListener = onSwipeListener;
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    /**
     * 滑动的监听事件
     */
    public interface OnSwipeListener{
        void  setOnSwipeListener(double distanceX);
    }

    public interface OnClickListener{
        void setOnClickListener();
    }

    GestureDetector detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onClickListener.setOnClickListener();
            return super.onSingleTapConfirmed(e);
        }
    });
}
