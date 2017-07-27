package com.domencai.runin.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.domencai.runin.utils.ScreenUtils;

import java.util.List;

/**
 * Created by Domen、on 2016/11/22.
 */

public class ChartView extends View {

    private Paint mPaint;
    private Paint mStrokePaint;
    private Path mPath;
    private Path mStrokePath;

    private int mWidth;

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setColor(0xff498ee0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(ScreenUtils.dp2px(getContext(), 2));

        mStrokePaint = new Paint();
        mStrokePaint.setAlpha(128);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(ScreenUtils.dp2px(getContext(), 1));

        mPath = new Path();
        mStrokePath = new Path();

        mWidth = ScreenUtils.getScreenWidth(getContext());
        mStrokePath.moveTo(mWidth * 0.08f, 0);
        mStrokePath.rLineTo(0, mWidth * 0.42f);
        mStrokePath.rLineTo(mWidth * 0.84f, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(size, size / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.drawLine(mWidth * 0.08f, 0, mWidth * 0.08f, mWidth * 0.42f, mPaint);
//        canvas.drawLine(mWidth * 0.08f, mWidth * 0.42f, mWidth * 0.92f, mWidth * 0.42f, mPaint);
//        canvas.drawLine(mWidth * 0.92f, 0, mWidth * 0.92f, mWidth * 0.42f, mPaint);

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mStrokePath, mStrokePaint);
    }

    public void setPath(List<Float> speed) {
        if (speed.size() < 2)
            return;
        mPath.reset();
        float unitX = mWidth * 4f / ((speed.size() - 1) * 5);
        float min = speed.get(0), max = speed.get(0);
        for (float s : speed) {
            if (s > max)
                max = s;
            if (s < min)
                min = s;
        }
        float unitY = mWidth / ((max - min) * 5);
        mPath.moveTo(mWidth * 0.1f, mWidth / 16f + (max - speed.get(0)) * unitY);
        mPath.rLineTo(0.3f * unitX, 0.3f * (speed.get(0) - speed.get(1)) * unitY);
        for (int i = 1; i < speed.size() - 1; i++) {
            float y1 = (speed.get(i - 1) - speed.get(i)) * unitY;
            float y2 = (speed.get(i) - speed.get(i + 1)) * unitY;

            mPath.rLineTo(0.4f * unitX, 0.4f * y1);
            mPath.rQuadTo(0.3f * unitX, y1 * 0.3f, 0.6f * unitX, (y1 + y2) * 0.3f);
        }
        mPath.lineTo(mWidth * 0.9f, mWidth / 16f + (max - speed.get(speed.size() - 1)) * unitY);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            Log.w("CustomView", "onTouchEvent: ");
//            test.add(mRandom.nextFloat() * 50 + 200);
//            setPath(test);
//        }
        return super.onTouchEvent(event);
    }
}
