package com.domencai.runin.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.domencai.runin.R;
import com.domencai.runin.utils.ScreenUtils;

import java.util.Arrays;

/**
 * Created by Domen、on 2016/12/9.
 */

public class LockView extends View {
    private int mDateIndex;
    private boolean isRace = false;
    private int mWidth;
    private Paint mPaint;
    private float mMainBottom;
    private float mSubBottom;

    private int sp_16;
    private int sp_18;
    private int sp_112;
    private int dp_32;

    private long mStartTime;
    private int mTotalTime;
    private String[] mDatas;

    private boolean isShow;

    public LockView(Context context) {
        this(context, null);
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mStartTime = System.currentTimeMillis();
        sp_16 = ScreenUtils.sp2px(context, 16);
        sp_18 = ScreenUtils.sp2px(context, 18);
        sp_112 = ScreenUtils.sp2px(context, 112);
        dp_32 = ScreenUtils.dp2px(context, 32);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        int height;
        if (isRace) {
            mMainBottom = mWidth * 0.4f;
            mSubBottom = mWidth * 1.1f;
            height = (int) (mWidth * 1.4f);
        } else {
            mMainBottom = mWidth / 2;
            mSubBottom = mWidth;
            height = (int) (mWidth * 1.2f);
        }
        setMeasuredDimension(mWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDatas == null)
            return;
        for (String s: mDatas)
            if (TextUtils.isEmpty(s))
                return;

        drawSubLayout(canvas);
        drawTopLayout(canvas);
        if (isRace)
            drawRelative(canvas);
    }

    private void drawSubLayout(Canvas canvas) {
        String[] left = mDatas[1].split(" ");
        String[] center = mDatas[2].split(" ");
        Log.w("LockView", "drawSubLayout: " + Arrays.toString(center));
        String[] right = mDatas[3].split(" ");
        if (left.length != 2 || center.length != 2 || right.length != 2)
            return;
        float start = mWidth / 6f;
        float offset = mWidth / 3f;
        float unitBottom = mSubBottom + sp_16 * 1.5f;
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(sp_16 * 2);
        canvas.drawText(left[0], start, mSubBottom, mPaint);
        canvas.drawText(right[0], start + offset * 2, mSubBottom, mPaint);

        if (isRace) {
            mPaint.setColor(center[0].startsWith("-") ? 0xFFFF5049 : 0xFF00FF00);
        }
        canvas.drawText(center[0], start + offset, mSubBottom, mPaint);
        mPaint.setTextSize(sp_16);
        canvas.drawText(center[1], start + offset, unitBottom, mPaint);

        mPaint.setColor(0xFFFFFFFF);
        canvas.drawText(left[1], start, unitBottom, mPaint);
        canvas.drawText(right[1], start + offset * 2, unitBottom, mPaint);
    }

    private void drawTopLayout(Canvas canvas) {
        String[] top = mDatas[0].split(" ");
        mPaint.setTextSize(sp_112);
        canvas.drawText(top[0], mWidth / 2, mMainBottom, mPaint);
        if (top[1].equals("min/km"))
            return;
        float len = mPaint.measureText(top[0]);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(sp_18);
        canvas.drawText(top[1], (mWidth + len) / 2, mMainBottom, mPaint);
    }

    private void drawRelative(Canvas canvas) {
        float left = 0.05f * mWidth;
        float right = 0.95f * mWidth;
        canvas.drawLine(left, 0.75f * mWidth + dp_32, right, 0.75f * mWidth + dp_32, mPaint);
        canvas.drawLine(left, 0.55f * mWidth + dp_32, right, 0.55f * mWidth + dp_32, mPaint);
        int relative = Integer.parseInt(mDatas[2].split(" ")[0]);
        if (relative > 200)
            relative = 200;
        else if (relative < -200)
            relative = -200;
        float f = relative * 0.4f * mWidth / 200;
        Bitmap me = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_run_green);
        canvas.drawBitmap(me, mWidth / 2 - dp_32 / 2 + f, mWidth * 0.55f, mPaint);
        Bitmap him = getBitmapFromVectorDrawable(getContext(), R.drawable.ic_run_red);
        canvas.drawBitmap(him, mWidth / 2 - dp_32 / 2 - f, mWidth * 0.75f, mPaint);
    }

    private Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void setRace(boolean isRace) {
        this.isRace = isRace;
    }

    public void setTotalTime(int totalTime) {
        this.mTotalTime = totalTime;
        this.mStartTime = System.currentTimeMillis();
    }

    public void setDatas(String[] datas) {
        if (mDatas == null)
            mDatas = new String[4];
        isShow = true;
        for (int i = 0; i < 4; i++) {
            if (!TextUtils.isEmpty(datas[i])) {
                mDatas[i] = datas[i];
            } else {
                this.mDateIndex = i;
                this.postDelayed(mRunnable, 1000);
                mDatas[i] = getFormatTime();
            }
        }
        invalidate();
    }

    public void hide() {
        isShow = false;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isShow)
                return;
            mDatas[mDateIndex] = getFormatTime();
            postInvalidate();
            postDelayed(this, 1000);
            Log.w("LockView", "run: " + mDatas[mDateIndex]);
        }
    };

    private String getFormatTime() {
        int time = (int) (System.currentTimeMillis() - mStartTime + mTotalTime) / 1000;
        int m = time / 60, s = time % 60;
        return (m > 9 ? m : "0" + m) + ":" + (s > 9 ? s : "0" + s) + " min";
    }
}
