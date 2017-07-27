package com.domencai.runin.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Domen、on 2016/12/5.
 */

public class RulerView extends View {

    private int mMax = 120;
    private int mCheckedColor = 0xFF4990E2;
    private float mMaxOffset;
    private float mUnit = 1;

    private Paint mPaint;
    private Path mPath;
    private GestureDetectorCompat mDetector;
    private OnSelectChangedListener mListener;

    private boolean isFling;
    private int lastScale;

    private int mWidth;
    private int mHeight;

    private float mSpace;
    private float mOffset;

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPath = new Path();
        mDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float
                    distanceY) {
                float x = mOffset + distanceX;
                if (x >= 0 && x <= mMaxOffset) {
                    mOffset = x;
                    invalidate();
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float
                    velocityY) {
                isFling = true;
                int nowScale = Math.round(mOffset / mSpace);
                int offsetToScale = Math.round((mOffset - velocityX * 0.1f) / mSpace);
                int time = 400;
                if (offsetToScale < 0) {
                    time = time * nowScale / (nowScale - offsetToScale);
                    offsetToScale = 0;
                } else if (offsetToScale > mMax) {
                    time = time * (mMax - nowScale) / (offsetToScale - nowScale);
                    offsetToScale = mMax;
                }
                showAnimation(offsetToScale * mSpace, time);
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    private void showAnimation(final float offset, int time) {
        ValueAnimator animator = ValueAnimator.ofFloat(mOffset, offset).setDuration(time);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mOffset = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = mWidth / 4;
        mSpace = mWidth / 50f;
        mMaxOffset = mMax * mSpace;
        setMeasuredDimension(mWidth, mHeight);
        mPaint.setTextSize(mHeight * 0.15f);
        float halfWidth = mHeight * 0.07f;
        mPath.moveTo(mWidth / 2, (float) (mHeight * 0.65));
        mPath.rLineTo(-halfWidth, mHeight * 0.12f);
        mPath.rLineTo(halfWidth * 2, 0);
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawScale(canvas);
        drawPointer(canvas);
        super.onDraw(canvas);
    }

    private void drawScale(Canvas c) {
        int center = mHeight / 2;
        float big = mHeight * 0.1f;
        float small = mHeight * 0.05f;
        float f = -((mWidth / 2 - mOffset) / mSpace - 25);
        int scale = (int) Math.floor(f);
        float fraction = f - scale;
        if (mListener != null && scale != lastScale) {
            if (mUnit >= 1)
                mListener.onSelectChanged(String.valueOf(Math.round(scale * mUnit)));
            else
                mListener.onSelectChanged(String.valueOf(scale / 10f));
            lastScale = scale;
        }
        c.save();
        c.translate(mWidth / 2 - mOffset, 0);

        for (int i = 0; i <= mMax; i++) {
            float index = i * mSpace;
            if (i % 10 == 0) {
                setPaintColor(i, scale, fraction, 0xFF212121);
                mPaint.setStrokeWidth(small / 2);
                c.drawLine(index, center - big, index, center + big, mPaint);
                String s = String.valueOf(Math.round(i * mUnit));
                c.drawText(s, index, mHeight * 0.3f, mPaint);
            } else {
                setPaintColor(i, scale, fraction, 0xFF858585);
                mPaint.setStrokeWidth(small / 3);
                c.drawLine(index, center - small, index, center + small, mPaint);
            }
        }
        c.restore();
    }

    private void setPaintColor(int current, int scale, float fraction, int color) {
        if (current == scale) {
            mPaint.setColor(getColor(fraction, mCheckedColor, color));
        } else if (current == scale + 1) {
            mPaint.setColor(getColor(fraction, color, mCheckedColor));
        } else {
            mPaint.setColor(color);
        }
    }

    private int getColor(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return (startA + (int)(fraction * (endA - startA))) << 24 |
                (startR + (int)(fraction * (endR - startR))) << 16 |
                (startG + (int)(fraction * (endG - startG))) << 8 |
                (startB + (int)(fraction * (endB - startB)));
    }

    private void drawPointer(Canvas c) {
        mPaint.setColor(mCheckedColor);
        c.drawPath(mPath, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            isFling = false;
        if (event.getAction() == MotionEvent.ACTION_UP && !isFling) {
            mOffset = Math.round(mOffset / mSpace) * mSpace;
            invalidate();
        }
        return true;
    }

    public void setOnSelectChangedListener(OnSelectChangedListener listener) {
        this.mListener = listener;
    }

    public void setRange(float unit, int max) {
        this.mUnit = unit;
        this.mMax = max;
        mOffset = 0;
        invalidate();
    }

    public void setCheckedColor(int color) {
        this.mCheckedColor = color;
    }

    public interface OnSelectChangedListener {
        void onSelectChanged(String num);
    }
}
