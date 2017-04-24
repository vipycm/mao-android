package com.vipycm.mao.camera;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * 拍照、录像用的按钮
 * Created by mao on 17-4-24.
 */

public class CaptureButton extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final int STATE_NORMAL = 0;
    private static final int STATE_LONG_PRESS = 1;
    private static final int STATE_LONG_PRESS_TIME_OUT = 2;
    private int mState = STATE_NORMAL;
    private long mLongPressStartTime = 0;
    private int mLongPressTimeOut = 30 * 1000;

    private ICaptureButtonListener mListener;

    private ObjectAnimator mScaleUpAnimator;
    private ObjectAnimator mScaleDownAnimator;

    public CaptureButton(@NonNull Context context) {
        super(context);
        init();
    }

    public CaptureButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CaptureButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.4f, 1.0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 1.5f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 1.5f);
        mScaleUpAnimator = ObjectAnimator.ofPropertyValuesHolder(this, alpha, scaleX, scaleY).setDuration(200);
        mScaleUpAnimator.setStartDelay(100);
        mScaleUpAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.4f, 1.0f);
        scaleX = PropertyValuesHolder.ofFloat("scaleX", 1.5f, 1.0f);
        scaleY = PropertyValuesHolder.ofFloat("scaleY", 1.5f, 1.0f);
        mScaleDownAnimator = ObjectAnimator.ofPropertyValuesHolder(this, alpha, scaleX, scaleY).setDuration(200);
        mScaleDownAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        int padding = size / 3;
        int strokeWidth = size / 12;
        int radius = (size - padding - strokeWidth) / 2;
        int centerX = width / 2;
        int centerY = height / 2;
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        canvas.drawCircle(centerX, centerY, radius, mPaint);

        if (mState == STATE_LONG_PRESS) {
            mHandler.sendEmptyMessageDelayed(MSG_INVALIDATE, 100);
            mPaint.setColor(Color.RED);
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                int sweepAngle = (int) (System.currentTimeMillis() - mLongPressStartTime) * 360 / mLongPressTimeOut;
                canvas.drawArc(centerX - radius, centerY - radius, centerX + radius, centerY + radius, -90, sweepAngle, false, mPaint);
            }
        }
        mPaint.setStyle(Style.FILL);
        mPaint.setAlpha(0xcc);
        int radiusIn = radius - (radius / 5);
        canvas.drawCircle(centerX, centerY, radiusIn, mPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mScaleDownAnimator.cancel();
                mScaleUpAnimator.start();
                mHandler.sendEmptyMessageDelayed(MSG_LONG_PRESS, 400);
                return true;
            case MotionEvent.ACTION_UP:
                if (mState == STATE_LONG_PRESS_TIME_OUT) {
                    mState = STATE_NORMAL;
                    return true;
                }
                mScaleUpAnimator.cancel();
                mScaleDownAnimator.start();
                mHandler.removeMessages(MSG_LONG_PRESS);
                mHandler.removeMessages(MSG_LONG_PRESS_TIME_OUT);
                if (mState == STATE_LONG_PRESS) {
                    if (mListener != null) {
                        mListener.onLongPressEnd();
                    }
                } else {
                    if (mListener != null) {
                        mListener.onClick();
                    }
                }
                mState = STATE_NORMAL;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private static final int MSG_LONG_PRESS = 1;
    private static final int MSG_LONG_PRESS_TIME_OUT = 2;
    private static final int MSG_INVALIDATE = 3;

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LONG_PRESS:
                    mHandler.sendEmptyMessageDelayed(MSG_LONG_PRESS_TIME_OUT, mLongPressTimeOut);
                    mLongPressStartTime = System.currentTimeMillis();
                    if (mListener != null) {
                        mListener.onLongPress();
                    }
                    mState = STATE_LONG_PRESS;
                    invalidate();
                    break;
                case MSG_INVALIDATE:
                    invalidate();
                    break;
                case MSG_LONG_PRESS_TIME_OUT:
                    mScaleUpAnimator.cancel();
                    mScaleDownAnimator.start();
                    if (mListener != null) {
                        mListener.onLongPressEnd();
                    }
                    mState = STATE_LONG_PRESS_TIME_OUT;
                    invalidate();
                    break;
            }
        }
    };

    public void setCaptureButtonListener(ICaptureButtonListener listener) {
        mListener = listener;
    }

    public interface ICaptureButtonListener {
        void onClick();

        void onLongPress();

        void onLongPressEnd();
    }
}
