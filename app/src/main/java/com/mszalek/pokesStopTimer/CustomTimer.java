package com.mszalek.pokesStopTimer;

import android.os.Handler;

/**
 * Created by Marcinus on 2016-08-07.
 */
public class CustomTimer {

    private Handler mHandler;
    private long mTargetTime;
    private long timeRemaining;
    private TimerListener mListener;

    public CustomTimer(TimerListener timerListener) {
        mListener = timerListener;
        mHandler = new Handler();
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void startCountDown(long totalTime, final long interval) {
        mTargetTime = System.currentTimeMillis() + totalTime;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                timeRemaining = mTargetTime - System.currentTimeMillis();
                if (timeRemaining > 0) {
                    mHandler.postDelayed(this, interval);
                }
                if (mListener != null) {
                    mListener.onTick(timeRemaining);
                }
            }
        };
        mHandler.post(runnable);
    }

    public interface TimerListener {
        void onTick(long timeLeft);
    }
}
