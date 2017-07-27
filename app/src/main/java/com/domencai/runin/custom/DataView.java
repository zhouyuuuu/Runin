package com.domencai.runin.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.domencai.runin.utils.ScreenUtils;

/**
 * Created by Domen、on 2016/12/9.
 */

public class DataView extends View {
    public final static int NORMAL = 0;
    public final static int GOAL_DISTANCE = 1;
    public final static int GOAL_DURATION = 2;
    public final static int GOAL_CALORIE = 3;
    public final static int COMPETITION = 4;
    public final static int FINISH = 5;

    private String[] mDatas;

    private int mTopIndex = 0;
    private int mLeftIndex = 1;
    private int mCenterIndex = 2;
    private int mRightIndex = 3;

    private long mStartTime;
    private int mTotalTime;
    private boolean shouldUpdate;
    private int mRunType = 0;
    private int mWidth;
    private float mMainBottom;
    private float mSubBottom;
    private int sizeSubUnit;
    private int sizeMainUnit;
    private int sizeSub;
    private int sizeMain;
    private int dp_8;

    private Paint mDataPaint;
    private Paint mUnitPaint;
    private Paint mShapePaint;
    private LinearGradient shader;

    public DataView(Context context) {
        this(context, null);
    }

    public DataView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        sizeSubUnit = ScreenUtils.sp2px(context, 14);
        sizeMainUnit = ScreenUtils.sp2px(context, 18);
        sizeSub = ScreenUtils.sp2px(context, 32);
        sizeMain = ScreenUtils.sp2px(context, 80);
        dp_8 = ScreenUtils.dp2px(context, 8);
        mDatas = new String[4];
        mDataPaint = new Paint();
        mDataPaint.setAntiAlias(true);
        mDataPaint.setDither(true);
        mDataPaint.setTextAlign(Paint.Align.CENTER);
        mUnitPaint = new Paint();
        mUnitPaint.setAntiAlias(true);
        mUnitPaint.setDither(true);
        mUnitPaint.setTextAlign(Paint.Align.CENTER);
        mShapePaint = new Paint();
        mShapePaint.setStrokeWidth(dp_8);
        mShapePaint.setStrokeCap(Paint.Cap.ROUND);
//        setDate(System.currentTimeMillis());
        setOtherData("0.00", "--", "0");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height = /*mRunType == FINISH ? (int) (mWidth * 0.6f) :*/ mWidth / 2;
        mMainBottom = mRunType == FINISH ? (int) (mWidth * 0.21f) :mWidth / 4;
        mSubBottom = mRunType == FINISH ? (int) (mWidth * 0.35f) :mWidth * 0.41f;
        setMeasuredDimension(mWidth, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        shader = new LinearGradient(w * 0.08f, 0, w * 0.92f, 0, new int[]{0xFFFF0000,
                0xFFFFFF00, 0xFF00FF00}, null, Shader.TileMode.MIRROR);
        mShapePaint.setShader(shader);
        mShapePaint.setTextSize(sizeSubUnit);
        mShapePaint.setTextAlign(Paint.Align.CENTER);
        if (mRunType == FINISH) {
            sizeMain = ScreenUtils.sp2px(getContext(), 64);
            sizeSub = ScreenUtils.sp2px(getContext(), 28);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRunType == FINISH) {
            int w = getWidth();
            canvas.drawLine(w * 0.08f, w * 0.45f, w * 0.92f, w * 0.45f, mShapePaint);
            canvas.drawText("慢", w * 0.04f, w * 0.46f, mShapePaint);
            canvas.drawText("快", w * 0.96f, w * 0.46f, mShapePaint);
        }
        if (mDatas == null)
            return;
        for (String s: mDatas)
            if (TextUtils.isEmpty(s))
                return;

        String[] top = mDatas[mTopIndex].split(" ");
        String[] left = mDatas[mLeftIndex].split(" ");
        String[] center = mDatas[mCenterIndex].split(" ");
        String[] right = mDatas[mRightIndex].split(" ");

        mDataPaint.setTextSize(sizeMain);
        mDataPaint.setColor(0xFF212121);
        canvas.drawText(top[0], mWidth / 2, mMainBottom, mDataPaint);

        float len = mDataPaint.measureText(top[0]);
        float start = mWidth / 6f;
        float offset = mWidth / 3f;
        float unitBottom = mSubBottom + sizeSubUnit * 1.25f;

        mDataPaint.setTextSize(sizeSub);
        canvas.drawText(left[0], start, mSubBottom, mDataPaint);
        canvas.drawText(right[0], start + offset * 2, mSubBottom, mDataPaint);
        if (mRunType == COMPETITION)
            mDataPaint.setColor(center[0].startsWith("-") ? 0xFFFF5049 : 0xFF00FF00);
        canvas.drawText(center[0], start + offset, mSubBottom, mDataPaint);

        mUnitPaint.setColor(0xFF757575);
        mUnitPaint.setTextSize(sizeMainUnit);
        float startX = (mWidth + len + mUnitPaint.measureText(top[1])) / 2;
        canvas.drawText(top[1], startX, mMainBottom, mUnitPaint);
        mUnitPaint.setTextSize(sizeSubUnit);
        canvas.drawText(left[1], start, unitBottom, mUnitPaint);
        canvas.drawText(right[1], start + offset * 2, unitBottom, mUnitPaint);
        if (mRunType == COMPETITION)
            mUnitPaint.setColor(center[0].startsWith("-") ? 0xFFFF5049 : 0xFF00FF00);
        canvas.drawText(center[1], start + offset, unitBottom, mUnitPaint);
    }

    public void setDate(long startTime) {
        shouldUpdate = true;
        this.mStartTime = startTime;
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!shouldUpdate)
                    return;
                mDatas[2] = getFormatTime();
                postInvalidate();
                postDelayed(this, 1000);
            }

            private String getFormatTime() {
                int time = (int) (System.currentTimeMillis() - mStartTime + mTotalTime) / 1000;
                int m = time / 60, s = time % 60;
                return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s) + " min";
            }
        }, 1000);
        if (TextUtils.isEmpty(mDatas[2]))
            mDatas[2] = "00:00 min";
        invalidate();
    }

    public void pause() {
        shouldUpdate = false;
        mTotalTime += (System.currentTimeMillis() - mStartTime);
    }

    public int getTotalTime() {
        return (int) (System.currentTimeMillis() - mStartTime + mTotalTime);
    }

    /**
     * 第一个是距离，第二个是速度，第三个是卡路里/相对距离（视跑步类型定）
     */
    public void setOtherData(String distance, String speed, String other) {
        mDatas[0] = distance + " km";
        mDatas[1] = speed + " min/km";
        mDatas[3] = other + (mRunType == COMPETITION ? " m" : " KCal");
        invalidate();
    }

    public void setDatas(String dis, String speed, String time, String cal) {
        mDatas[0] = dis + " km";
        mDatas[1] = speed + " min/km";
        mDatas[2] = time + " min";
        mDatas[3] = cal + " KCal";
        invalidate();
    }

    public void setRunType(int type) {
        this.mRunType = type;
        switch (type) {
            case GOAL_DURATION:
                mTopIndex = 2;
                mCenterIndex = 0;
                break;
            case GOAL_CALORIE:
                mTopIndex = 3;
                mRightIndex = 0;
                break;
            case COMPETITION:
                mCenterIndex = 3;
                mRightIndex = 2;
                break;
        }
    }

    private float mDownX;
    private float mDownY;
    private boolean isClick;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRunType != 0)
            return true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = event.getRawX();
            mDownY = event.getRawY();
            isClick = true;
        } else {
            if (Math.abs(mDownX - event.getRawX()) > dp_8 || Math.abs(mDownY - event.getRawY()) >
                    dp_8)
                isClick = false;
            if (event.getAction() == MotionEvent.ACTION_UP && isClick) {
                float x = event.getX();
                float y = event.getY();
                if (y > mMainBottom) {
                    int temp;
                    if (x < mWidth / 3f) {
                        temp = mLeftIndex;
                        mLeftIndex = mTopIndex;
                    } else if (x < mWidth * 2f / 3) {
                        temp = mCenterIndex;
                        mCenterIndex = mTopIndex;
                    } else {
                        temp = mRightIndex;
                        mRightIndex = mTopIndex;
                    }
                    mTopIndex = temp;
//                    Log.w("DataView", "onTouchEvent: " + Arrays.toString(getDatas()));
                    invalidate();
                }
            }
        }
        return true;
    }

    public String[] getDatas() {
        String[] strings = new String[]{mDatas[mTopIndex], mDatas[mLeftIndex],
                mDatas[mCenterIndex], mDatas[mRightIndex]};
        for (int i = 0; i < 4; i++) {
            if (strings[i].endsWith("min"))
                strings[i] = "";
        }
        return strings;
    }
}
