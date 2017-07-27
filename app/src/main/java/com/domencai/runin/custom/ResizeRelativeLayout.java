package com.domencai.runin.custom;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by lenovo on 2016/12/7.
 */

public class ResizeRelativeLayout extends RelativeLayout {
    public static final int HIDE = 0;
    public static final int SHOW = 1;

    private Handler mainHandler = new Handler();

    public ResizeRelativeLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public ResizeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onSizeChanged(int w,final int h, int oldw,final int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (oldh - h > 50){
                    keyBoardStateListener.onStateChange(SHOW);
                }else if (h - oldh>50&&h - oldh<150&&oldh>0){
                    keyBoardStateListener.onStateChange(SHOW);
                }
                else {
                    if(keyBoardStateListener != null){
                        keyBoardStateListener.onStateChange(HIDE);
                    }
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private KeyBoardStateListener  keyBoardStateListener;

    public void setKeyBoardStateListener(KeyBoardStateListener keyBoardStateListener) {
        this.keyBoardStateListener = keyBoardStateListener;
    }

    public interface KeyBoardStateListener{
        void onStateChange(int state);
    }
}
