package com.ycxy.ymh.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zhengken.me on 2016/11/27.
 * ClassName    : LyricView
 * Description  :
 */
public class LyricView extends View {

    private static final String TAG = "LyricView";

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    private static final float SLIDE_COEFFICIENT = 0.2f;

    private static final int UNITS_SECOND = 1000;
    private static final int UNITS_MILLISECOND = 1;

    private static final int FLING_ANIMATOR_DURATION = 500 * UNITS_MILLISECOND;

    private static final int THRESHOLD_Y_VELOCITY = 1600;

    private static final int INDICATOR_ICON_PLAY_MARGIN_LEFT = 7;//dp
    private static final int INDICATOR_ICON_PLAY_WIDTH = 15;//sp
    private static final int INDICATOR_LINE_MARGIN = 10;//dp
    private static final int INDICATOR_TIME_TEXT_SIZE = 10;//sp
    private static final int INDICATOR_TIME_MARGIN_RIGHT = 7;//dp

    private static final int DEFAULT_TEXT_SIZE = 16;//sp
    private static final int DEFAULT_MAX_LENGTH = 300;//dp
    private static final int DEFAULT_LINE_SPACE = 25;//dp

    private int mHintColor;
    private int mDefaultColor;
    private int mHighLightColor;
    private int mTextAlign;


    private int mLineCount;
    private int mTextSize;
    private float mLineHeight;
    private LyricInfo mLyricInfo;
    private String mDefaultHint;
    private int mMaxLength;

    private TextPaint mTextPaint;
    private Paint mBtnPlayPaint;
    private Paint mLinePaint;
    private Paint mTimerPaint;

    private boolean mFling = false;
    private ValueAnimator mFlingAnimator;
    private float mScrollY = 0;
    private float mLineSpace = 0;
    private boolean mIsShade;
    private float mShaderWidth = 0;
    private int mCurrentPlayLine = 0;
    private boolean mShowIndicator;

    private VelocityTracker mVelocityTracker;
    private float mVelocity = 0;
    private float mDownX;
    private float mDownY;
    private float mLastScrollY;
    private boolean mUserTouch = false;
    private int maxVelocity;
    private int mLineNumberUnderIndicator = 0;
    private Rect mBtnPlayRect = new Rect();
    private Rect mTimerRect;
    private String mDefaultTime = "00:00";

    private int mLineColor = Color.parseColor("#EFEFEF");
    private int mBtnColor = Color.parseColor("#EFEFEF");

    private List<Integer> mLineFeedRecord = new ArrayList<>();
    private boolean mEnableLineFeed = false;
    private int mExtraHeight = 0;

    private int mTextHeight;

    private String mCurrentLyricFilePath = null;

    private OnPlayerClickListener mClickListener;
    private OnPlayerSingleClickListener mSingleClickListener;

    public LyricView(Context context) {
        super(context);
        initMyView(context);
    }

    public LyricView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        getAttrs(context, attributeSet);
        initMyView(context);

    }

    public LyricView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        getAttrs(context, attributeSet);
        initMyView(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
                actionCancel(event);
                break;
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(event);
                break;
            default:
                break;
        }
        invalidateView();
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mBtnPlayRect.set((int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, INDICATOR_ICON_PLAY_MARGIN_LEFT),
                (int) (getHeight() * 0.5f - getRawSize(TypedValue.COMPLEX_UNIT_SP, INDICATOR_ICON_PLAY_WIDTH) * 0.5f),
                (int) (getRawSize(TypedValue.COMPLEX_UNIT_SP, INDICATOR_ICON_PLAY_WIDTH) + getRawSize(TypedValue.COMPLEX_UNIT_DIP, INDICATOR_ICON_PLAY_MARGIN_LEFT)),
                (int) (getHeight() * 0.5f + getRawSize(TypedValue.COMPLEX_UNIT_SP, INDICATOR_ICON_PLAY_WIDTH) * 0.5f));
        mShaderWidth = getWidth() * 0.4f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (scrollable()) {
            if (mShowIndicator) {
                drawIndicator(canvas);
            }

            for (int i = 0; i < mLineCount; i++) {
                float x = 0;
                switch (mTextAlign) {
                    case LEFT:
                        x = INDICATOR_ICON_PLAY_MARGIN_LEFT + INDICATOR_LINE_MARGIN + mBtnPlayRect.width();
                        break;
                    case CENTER:
                        x = getWidth() * 0.5f;
                        break;
                    case RIGHT:
                        x = getWidth() - INDICATOR_LINE_MARGIN * 2 - mTimerRect.width() - INDICATOR_ICON_PLAY_MARGIN_LEFT;
                        break;
                }

                float y;
                if (mEnableLineFeed && i > 0) {
                    y = getMeasuredHeight() * 0.5f + i * mLineHeight - mScrollY + mLineFeedRecord.get(i - 1);
                } else {
                    y = getMeasuredHeight() * 0.5f + i * mLineHeight - mScrollY;
                }

//                float y = getHeight() * 0.5f + i * mLineHeight - mScrollY;
                if (y < 0) {
                    continue;
                }
                if (y > getHeight()) {
                    break;
                }
                if (i == mCurrentPlayLine - 1) {
                    mTextPaint.setColor(mHighLightColor);
                } else if (i == mLineNumberUnderIndicator && mShowIndicator) {
                    mTextPaint.setColor(Color.LTGRAY);
                } else {
                    mTextPaint.setColor(mDefaultColor);
                }
                if (mIsShade && (y > getHeight() - mShaderWidth || y < mShaderWidth)) {
                    if (y < mShaderWidth) {
                        mTextPaint.setAlpha(26 + (int) (23000.0f * y / mShaderWidth * 0.01f));
                    } else {
                        mTextPaint.setAlpha(26 + (int) (23000.0f * (getHeight() - y) / mShaderWidth * 0.01f));
                    }
                } else {
                    mTextPaint.setAlpha(255);
                }

                if (mEnableLineFeed) {
                    StaticLayout staticLayout = new StaticLayout(mLyricInfo.songLines.get(i).content, mTextPaint,
                            mMaxLength,
                            Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    canvas.save();
                    canvas.translate(x, y);
                    staticLayout.draw(canvas);
                    canvas.restore();
                } else {
                    canvas.drawText(mLyricInfo.songLines.get(i).content, x, y, mTextPaint);
                }
            }
        } else {
            mTextPaint.setColor(mHintColor);
            canvas.drawText(mDefaultHint, getMeasuredWidth() / 2, getMeasuredHeight() / 2, mTextPaint);
        }
    }

    private void getAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, me.zhengken.lyricview.R.styleable.LyricView);
        mIsShade = ta.getBoolean(me.zhengken.lyricview.R.styleable.LyricView_fadeInFadeOut, false);
        mDefaultHint = ta.getString(me.zhengken.lyricview.R.styleable.LyricView_hint) != null
                ? ta.getString(me.zhengken.lyricview.R.styleable.LyricView_hint)
                : getResources().getString(me.zhengken.lyricview.R.string.default_hint);
        mHintColor = ta.getColor(me.zhengken.lyricview.R.styleable.LyricView_hintColor, Color.parseColor("#FFFFFF"));
        mDefaultColor = ta.getColor(me.zhengken.lyricview.R.styleable.LyricView_textColor, Color.parseColor("#8D8D8D"));
        mHighLightColor = ta.getColor(me.zhengken.lyricview.R.styleable.LyricView_highlightColor, Color.parseColor("#FFFFFF"));
        mTextSize = ta.getDimensionPixelSize(me.zhengken.lyricview.R.styleable.LyricView_textSize, (int) getRawSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE));
        mTextAlign = ta.getInt(me.zhengken.lyricview.R.styleable.LyricView_textAlign, CENTER);
        mMaxLength = ta.getDimensionPixelSize(me.zhengken.lyricview.R.styleable.LyricView_maxLength, (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MAX_LENGTH));
        mLineSpace = ta.getDimensionPixelSize(me.zhengken.lyricview.R.styleable.LyricView_lineSpace, (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_LINE_SPACE));
        ta.recycle();
    }

    public void setOnPlayerClickListener(OnPlayerClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }

    public void setOnPlayerSingleClickListener(OnPlayerSingleClickListener mSingleClickListener) {
        this.mSingleClickListener = mSingleClickListener;
    }

    public void setAlignment(@Alignment int alignment) {
        mTextAlign = alignment;
    }

    public void setCurrentTimeMillis(long current) {
        scrollToCurrentTimeMillis(current);
    }

    public void setLyricFile(File file) {

        if (file == null || !file.exists()) {
            reset();
            mCurrentLyricFilePath = "";
            return;
        } else if (file.getPath().equals(mCurrentLyricFilePath)) {
            return;
        } else {
            mCurrentLyricFilePath = file.getPath();
            reset();
        }
        try {

            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[1024];
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }

            detector.dataEnd();
            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                setLyricFile(file, encoding);
            } else {
                setLyricFile(file, "UTF-8");
            }
            detector.reset();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLyricFile(File file, String charsetName) {
        if (file != null && file.exists()) {
            try {
                setupLyricResource(new FileInputStream(file), charsetName);

                for (int i = 0; i < mLyricInfo.songLines.size(); i++) {

                    StaticLayout staticLayout = new StaticLayout(mLyricInfo.songLines.get(i).content, mTextPaint,
                            (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_MAX_LENGTH),
                            Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

                    if (staticLayout.getLineCount() > 1) {
                        mEnableLineFeed = true;
                        mExtraHeight = mExtraHeight + (staticLayout.getLineCount() - 1) * mTextHeight;
                    }

                    mLineFeedRecord.add(i, mExtraHeight);

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            invalidateView();
        }
    }

    private void setLineSpace(float lineSpace) {
        if (mLineSpace != lineSpace) {
            mLineSpace = getRawSize(TypedValue.COMPLEX_UNIT_DIP, lineSpace);
            measureLineHeight();
            mScrollY = measureCurrentScrollY(mCurrentPlayLine);
            invalidateView();
        }
    }

    public void reset() {
        resetView();
    }

    private void actionCancel(MotionEvent event) {
        releaseVelocityTracker();
    }

    private void actionDown(MotionEvent event) {
        removeCallbacks(hideIndicator);
        mLastScrollY = mScrollY;
        mDownX = event.getX();
        mDownY = event.getY();
        if (mFlingAnimator != null) {
            mFlingAnimator.cancel();
            mFlingAnimator = null;
        }
        setUserTouch(true);
    }

    private boolean overScrolled() {

        return scrollable() && (mScrollY > mLineHeight * (mLineCount - 1) + mLineFeedRecord.get(mLineCount - 1) + (mEnableLineFeed ? mTextHeight : 0) || mScrollY < 0);
    }


    private void actionMove(MotionEvent event) {
        if (scrollable()) {
            final VelocityTracker tracker = mVelocityTracker;
            tracker.computeCurrentVelocity(UNITS_SECOND, maxVelocity);
            mScrollY = mLastScrollY + mDownY - event.getY();
            mVelocity = tracker.getYVelocity();
            measureCurrentLine();
        }
    }

    private void actionUp(MotionEvent event) {

        postDelayed(hideIndicator, 3 * UNITS_SECOND);

        releaseVelocityTracker();

        if (scrollable()) {
            if (overScrolled() && mScrollY < 0) {
                smoothScrollTo(0);
                return;
            }
            if (overScrolled() && mScrollY > mLineHeight * (mLineCount - 1) + mLineFeedRecord.get(mLineCount - 1) + (mEnableLineFeed ? mTextHeight : 0)) {
                smoothScrollTo(mLineHeight * (mLineCount - 1) + mLineFeedRecord.get(mLineCount - 1) + (mEnableLineFeed ? mTextHeight : 0));
                return;
            }
            if (Math.abs(mVelocity) > THRESHOLD_Y_VELOCITY) {
                doFlingAnimator(mVelocity);
                return;
            }
            if (mShowIndicator && clickPlayer(event)) {
                if (mLineNumberUnderIndicator != mCurrentPlayLine) {
                    mShowIndicator = false;
                    if (mClickListener != null) {
                        setUserTouch(false);
                        mClickListener.onPlayerClicked(mLyricInfo.songLines.get(mLineNumberUnderIndicator).start, mLyricInfo.songLines.get(mLineNumberUnderIndicator).content);
                    }
                }
            }
        }
    }

    private String measureCurrentTime() {
        DecimalFormat format = new DecimalFormat("00");
        if (mLyricInfo != null && mLineCount > 0 && mLineNumberUnderIndicator - 1 < mLineCount && mLineNumberUnderIndicator > 0) {
            return format.format(mLyricInfo.songLines.get(mLineNumberUnderIndicator - 1).start / 1000 / 60) + ":" + format.format(mLyricInfo.songLines.get(mLineNumberUnderIndicator - 1).start / 1000 % 60);
        }
        if (mLyricInfo != null && mLineCount > 0 && (mLineNumberUnderIndicator - 1) >= mLineCount) {
            return format.format(mLyricInfo.songLines.get(mLineCount - 1).start / 1000 / 60) + ":" + format.format(mLyricInfo.songLines.get(mLineCount - 1).start / 1000 % 60);
        }
        if (mLyricInfo != null && mLineCount > 0 && mLineNumberUnderIndicator - 1 <= 0) {
            return format.format(mLyricInfo.songLines.get(0).start / 1000 / 60) + ":" + format.format(mLyricInfo.songLines.get(0).start / 1000 % 60);
        }
        return mDefaultTime;
    }

    private void drawIndicator(Canvas canvas) {

        //绘制 播放 按钮
        Path pathPlay = new Path();
        float yCoordinate = mBtnPlayRect.left + (float) Math.sqrt(Math.pow(mBtnPlayRect.width(), 2) - Math.pow(mBtnPlayRect.width() * 0.5f, 2));
        float remainWidth = mBtnPlayRect.right - yCoordinate;

        pathPlay.moveTo(mBtnPlayRect.centerX() - mBtnPlayRect.width() * 0.5f, mBtnPlayRect.centerY() - mBtnPlayRect.height() * 0.5f);
        pathPlay.lineTo(mBtnPlayRect.centerX() - mBtnPlayRect.width() * 0.5f, mBtnPlayRect.centerY() + mBtnPlayRect.height() * 0.5f);
        pathPlay.lineTo(yCoordinate, mBtnPlayRect.centerY());
        pathPlay.lineTo(mBtnPlayRect.centerX() - mBtnPlayRect.width() * 0.5f, mBtnPlayRect.centerY() - mBtnPlayRect.height() * 0.5f);

        canvas.drawPath(pathPlay, mBtnPlayPaint);

        //绘制 分割线
        Path pathLine = new Path();
        pathLine.moveTo(mBtnPlayRect.right + getRawSize(TypedValue.COMPLEX_UNIT_DIP, INDICATOR_LINE_MARGIN) - remainWidth, getMeasuredHeight() * 0.5f);
        pathLine.lineTo(getWidth() - mTimerRect.width() - getRawSize(TypedValue.COMPLEX_UNIT_DIP, INDICATOR_TIME_MARGIN_RIGHT) - getRawSize(TypedValue.COMPLEX_UNIT_DIP, INDICATOR_LINE_MARGIN), getHeight() * 0.5f);
        canvas.drawPath(pathLine, mLinePaint);

        //绘制 时间
        canvas.drawText(measureCurrentTime(), getWidth() - getRawSize(TypedValue.COMPLEX_UNIT_DIP, INDICATOR_TIME_MARGIN_RIGHT), (getHeight() + mTimerRect.height()) * 0.5f, mTimerPaint);
    }

    private boolean clickPlayer(MotionEvent event) {
        if (mBtnPlayRect != null && mDownX > (mBtnPlayRect.left - INDICATOR_ICON_PLAY_MARGIN_LEFT) && mDownX < (mBtnPlayRect.right + INDICATOR_ICON_PLAY_MARGIN_LEFT) && mDownY > (mBtnPlayRect.top - INDICATOR_ICON_PLAY_MARGIN_LEFT) && mDownY < (mBtnPlayRect.bottom + INDICATOR_ICON_PLAY_MARGIN_LEFT)) {
            float upX = event.getX();
            float upY = event.getY();
            return upX > (mBtnPlayRect.left - INDICATOR_ICON_PLAY_MARGIN_LEFT) && upX < (mBtnPlayRect.right + INDICATOR_ICON_PLAY_MARGIN_LEFT) && upY > (mBtnPlayRect.top - INDICATOR_ICON_PLAY_MARGIN_LEFT) && upY < (mBtnPlayRect.bottom + INDICATOR_ICON_PLAY_MARGIN_LEFT);
        }
        return false;
    }

    private void doFlingAnimator(float velocity) {

        float distance = (velocity / Math.abs(velocity) * (Math.abs(velocity) * SLIDE_COEFFICIENT));
        float to = Math.min(Math.max(0, (mScrollY - distance)), (mLineCount - 1) * mLineHeight + mLineFeedRecord.get(mLineCount - 1) + (mEnableLineFeed ? mTextHeight : 0));

        mFlingAnimator = ValueAnimator.ofFloat(mScrollY, to);
        mFlingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mScrollY = (float) animation.getAnimatedValue();
                measureCurrentLine();
                invalidateView();
            }
        });

        mFlingAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mVelocity = 0;
                mFling = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mFling = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }
        });

        mFlingAnimator.setDuration(FLING_ANIMATOR_DURATION);
        mFlingAnimator.setInterpolator(new DecelerateInterpolator());
        mFlingAnimator.start();
    }

    private void setUserTouch(boolean isUserTouch) {
        if (isUserTouch) {
            mUserTouch = true;
            mShowIndicator = true;
        } else {
            mUserTouch = false;
            mShowIndicator = false;
        }
    }

    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void initMyView(Context context) {
        maxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        initPaint();
        initAllBounds();
    }

    private void initAllBounds() {
        setRawTextSize(mTextSize);

        setLineSpace(mLineSpace);
        measureLineHeight();

        mTimerRect = new Rect();
        mTimerPaint.getTextBounds(mDefaultTime, 0, mDefaultTime.length(), mTimerRect);


    }

    private void initPaint() {
        mTextPaint = new TextPaint();
        mTextPaint.setDither(true);
        mTextPaint.setAntiAlias(true);
        switch (mTextAlign) {
            case LEFT:
                mTextPaint.setTextAlign(Paint.Align.LEFT);
                break;
            case CENTER:
                mTextPaint.setTextAlign(Paint.Align.CENTER);
                break;
            case RIGHT:
                mTextPaint.setTextAlign(Paint.Align.RIGHT);
                break;
        }

        mBtnPlayPaint = new Paint();
        mBtnPlayPaint.setDither(true);
        mBtnPlayPaint.setAntiAlias(true);
        mBtnPlayPaint.setColor(mBtnColor);
        mBtnPlayPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBtnPlayPaint.setAlpha(128);

        mLinePaint = new Paint();
        mLinePaint.setDither(true);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAlpha(64);
        mLinePaint.setStrokeWidth(1.0f);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mTimerPaint = new Paint();
        mTimerPaint.setDither(true);
        mTimerPaint.setAntiAlias(true);
        mTimerPaint.setColor(Color.WHITE);
        mTimerPaint.setTextAlign(Paint.Align.RIGHT);
        mTimerPaint.setTextSize(getRawSize(TypedValue.COMPLEX_UNIT_SP, INDICATOR_TIME_TEXT_SIZE));


    }

    private float measureCurrentScrollY(int line) {
        if (mEnableLineFeed && line > 1) {
            return (line - 1) * mLineHeight + mLineFeedRecord.get(line - 1);
        }
        return (line - 1) * mLineHeight;
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            //  当前线程是主UI线程，直接刷新。
            invalidate();
        } else {
            //  当前线程是非UI线程，post刷新。
            postInvalidate();
        }
    }

    private void measureLineHeight() {
        Rect lineBound = new Rect();
        mTextPaint.getTextBounds(mDefaultHint, 0, mDefaultHint.length(), lineBound);
        mTextHeight = lineBound.height();
        mLineHeight = mTextHeight + mLineSpace;
    }

    /**
     * To measure current showing line number based on the view's scroll Y
     */
    private void measureCurrentLine() {
        float baseScrollY = mScrollY + mLineHeight * 0.5f;

        if (mEnableLineFeed) {
            for (int i = mLyricInfo.songLines.size(); i >= 0; i--) {
                if (baseScrollY > measureCurrentScrollY(i) + mLineSpace * 0.2) {
                    mLineNumberUnderIndicator = i - 1;
                    break;
                }
            }
        } else {
            mLineNumberUnderIndicator = (int) (baseScrollY / mLineHeight);
        }


    }

    private void smoothScrollTo(float toY) {
        final ValueAnimator animator = ValueAnimator.ofFloat(mScrollY, toY);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mScrollY = (Float) valueAnimator.getAnimatedValue();
                invalidateView();
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mFling = false;
                measureCurrentLine();
                invalidateView();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }

            @Override
            public void onAnimationStart(Animator animator) {
                mFling = true;
            }
        });
        animator.setDuration(640);
        animator.setInterpolator(new LinearInterpolator());

        animator.start();
    }

    private boolean scrollable() {
        return mLyricInfo != null && mLyricInfo.songLines != null && mLyricInfo.songLines.size() > 0;
    }

    private void scrollToCurrentTimeMillis(long time) {

        int position = 0;
        if (scrollable()) {
            for (int i = 0, size = mLineCount; i < size; i++) {
                LineInfo lineInfo = mLyricInfo.songLines.get(i);
                if (lineInfo != null && lineInfo.start >= time) {
                    position = i;
                    break;
                }
                if (i == mLineCount - 1) {
                    position = mLineCount;
                }
            }
        }
        if (mCurrentPlayLine != position) {
            mCurrentPlayLine = position;
            if (!mFling && !mUserTouch) {
                smoothScrollTo(measureCurrentScrollY(position));
            }
        }
    }

    private void setupLyricResource(InputStream inputStream, String charsetName) {
        if (inputStream != null) {
            try {
                LyricInfo lyricInfo = new LyricInfo();
                lyricInfo.songLines = new ArrayList<>();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charsetName);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    analyzeLyric(lyricInfo, line);
                }
                reader.close();
                inputStream.close();
                inputStreamReader.close();
                // 排序
                Collections.sort(lyricInfo.songLines, new Comparator<LineInfo>() {
                    @Override
                    public int compare(LineInfo lhs, LineInfo rhs) {
                        if(lhs.start < rhs.start){
                            return  -1;
                        }else if(lhs.start > rhs.start){
                            return  1;
                        }else{
                            return 0;
                        }

                    }
                });

                mLyricInfo = lyricInfo;
                mLineCount = mLyricInfo.songLines.size();
                invalidateView();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            invalidateView();
        }
    }

    /**
     * 逐行解析歌词内容
     */
    private void analyzeLyric(LyricInfo lyricInfo, String line) {
        int index = line.lastIndexOf("]");
        if (line.startsWith("[offset:")) {
            // time offset
            lyricInfo.songOffset = Long.parseLong(line.substring(8, index).trim());
            return;
        }
        if (line.startsWith("[ti:")) {
            // title
            lyricInfo.songTitle = line.substring(4, index).trim();
            return;
        }
        if (line.startsWith("[ar:")) {
            // artist
            lyricInfo.songArtist = line.substring(4, index).trim();
            return;
        }
        if (line.startsWith("[al:")) {
            // album
            lyricInfo.songAlbum = line.substring(4, index).trim();
            return;
        }
        if (line.startsWith("[by:")) {
            return;
        }
        if (index >= 9 && line.trim().length() > index + 1) {
            parsedLyric(lyricInfo,line);
        }
    }

    /**
     * 从字符串中获得时间值
     */
    private long measureStartTimeMillis(String str) {
        long minute = Long.parseLong(str.substring(1, 3));
        long second = Long.parseLong(str.substring(4, 6));
        long millisecond = Long.parseLong(str.substring(7, 9));
        return millisecond + second * 1000 + minute * 60 * 1000;
    }

    private void resetLyricInfo() {
        if (mLyricInfo != null) {
            if (mLyricInfo.songLines != null) {
                mLyricInfo.songLines.clear();
                mLyricInfo.songLines = null;
            }
            mLyricInfo = null;
        }
    }

    private void resetView() {
        mCurrentPlayLine = 0;
        resetLyricInfo();
        invalidateView();
        mLineCount = 0;
        mScrollY = 0;
        mEnableLineFeed = false;
        mLineFeedRecord.clear();
        mExtraHeight = 0;
    }

    private float getRawSize(int unit, float size) {
        Context context = getContext();
        Resources resources;
        if (context == null) {
            resources = Resources.getSystem();
        } else {
            resources = context.getResources();
        }
        return TypedValue.applyDimension(unit, size, resources.getDisplayMetrics());
    }

    private void setRawTextSize(float size) {
        if (size != mTextPaint.getTextSize()) {
            mTextPaint.setTextSize(size);
            measureLineHeight();
            mScrollY = measureCurrentScrollY(mCurrentPlayLine);
            invalidateView();
        }
    }

    private class LyricInfo {
        List<LineInfo> songLines;

        String songArtist;
        String songTitle;
        String songAlbum;

        long songOffset;
    }

    private class LineInfo {
        String content;
        long start;
    }

    Runnable hideIndicator = new Runnable() {
        @Override
        public void run() {
            setUserTouch(false);
            invalidateView();
        }
    };

    @IntDef({LEFT, CENTER, RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Alignment {
    }

    public interface OnPlayerClickListener {
        void onPlayerClicked(long progress, String content);
    }

    public interface OnPlayerSingleClickListener{
        void onPlayerSingleClicked();
    }

    /**
     * 解析一句歌词
     *
     * @param lyricInfo
     * @param line [02:04.12][03:37.32][00:59.73]我在这里欢笑
     * @return
     */
    private String parsedLyric(LyricInfo lyricInfo, String line) {
        ////indexOf第一次出现[的位置
        int pos1 = line.indexOf("[");//0,如果没有返回-1

        int pos2 = line.indexOf("]");//9,如果没有返回-1

        if(pos1 ==0 && pos2 != -1){//肯定是由一句歌词

            //装时间
            long[] times = new long[getCountTag(line)];

            String strTime =line.substring(pos1+1,pos2) ;//02:04.12
            times[0] = strTime2LongTime(strTime);

            String content = line;
            int i = 1;
            while (pos1 ==0 && pos2 != -1){
                content = content.substring(pos2 + 1); //[03:37.32][00:59.73]我在这里欢笑--->[00:59.73]我在这里欢笑-->我在这里欢笑
                pos1 = content.indexOf("[");//0/-1
                pos2 = content.indexOf("]");//9//-1

                if(pos2 != -1 ){
                    strTime = content.substring(pos1 + 1, pos2);//03:37.32-->00:59.73
                    times[i] = strTime2LongTime(strTime);

                    if(times[i] == -1){
                        return  "";
                    }

                    i++;
                }

            }

            LineInfo lineInfo = new LineInfo();
            //把时间数组和文本关联起来，并且加入到集合中
            for(int j = 0;j < times.length;j++){

                if(times[j] !=0){//有时间戳
                    lineInfo.content = content;
                    lineInfo.start = times[j];
                    //添加到集合中
                    lyricInfo.songLines.add(lineInfo);
                    lineInfo = new LineInfo();
                }
            }
            return  content;//我在这里欢笑

        }


        return "";
    }

    /**
     * 判断有多少句歌词
     * @param line [02:04.12][03:37.32][00:59.73]我在这里欢笑
     * @return
     */
    private int getCountTag(String line) {
        int result = -1;
        String [] left = line.split("\\[");
        String [] right = line.split("\\]");

        if(left.length==0 && right.length ==0){
            result = 1;
        }else if(left.length > right.length){
            result = left.length;
        }else{
            result = right.length;
        }
        return result;
    }

    /**
     * 把String类型是时间转换成long类型
     * @param strTime 02:04.12
     * @return
     */
    private long strTime2LongTime(String strTime) {
        long result = -1;
        try{

            //1.把02:04.12按照:切割成02和04.12
            String[] s1 = strTime.split(":");
            //2.把04.12按照.切割成04和12
            String[] s2 = s1[1].split("\\.");

            //1.分
            long min = Long.parseLong(s1[0]);

            //2.秒
            long second = Long.parseLong(s2[0]);

            //3.毫秒
            long mil = Long.parseLong(s2[1]);

            result =  min * 60 * 1000 + second * 1000 + mil*10;
        }catch (Exception e){
            e.printStackTrace();
            result = -1;
        }

        return result;
    }

    // 添加手势识别器
    GestureDetector detector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // 调用回调函数
            if (!mFling) {
                mSingleClickListener.onPlayerSingleClicked();
            }
            return super.onSingleTapConfirmed(e);
        }
    });
}
