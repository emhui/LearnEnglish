package com.ycxy.ymh.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Y&MH on 2018-1-11.
 */

@SuppressLint("AppCompatCustomView")
public class MyTextView extends TextView {
    public MyTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
                break;
            case MotionEvent.ACTION_UP:
                endX = event.getX();
                distanceX = endX - startX;

                if (distanceX > 0 && Math.abs(distanceX) > 100) {
                    onSwipeListener.setOnSwipePreListener();
                }

                if (distanceX < 0 && Math.abs(distanceX) > 100) {
                    onSwipeListener.setOnSwipeNextListener();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    GestureDetector detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onClickListener.setOnClickedListener();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }
    });

    // 创建监听接口
    private OnClickedListener onClickListener;
    private OnSwipeListener onSwipeListener;

    public void setOnClickedListener(OnClickedListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    public interface OnClickedListener {
        void setOnClickedListener();
    }

    public interface OnSwipeListener {
        void setOnSwipeNextListener();

        void setOnSwipePreListener();
    }
}
