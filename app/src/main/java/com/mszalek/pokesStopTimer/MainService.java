package com.mszalek.pokesStopTimer;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


/**
 * Created by Marcinus on 2016-08-07.
 */
public class MainService extends Service implements View.OnTouchListener, CustomTimer.TimerListener, MainView.MainViewListener{

    private WindowManager windowManager;
    private MainView mMainView;
    private CustomTimer mTimer = new CustomTimer(this);
    private boolean isMoving;

    private int mPrevDragX;
    private int mPrevDragY;
    private WindowManager.LayoutParams mRootLayoutParams;

    private final long TOTAL_WAITING_TIME = 5 * 60 * 1000; //5 miuntes

    private void resetTimer() {
        mMainView.resetTimer(TOTAL_WAITING_TIME, 1000);
        mTimer.startCountDown(TOTAL_WAITING_TIME, 1000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Not used
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mMainView = (MainView) LayoutInflater.from(this).inflate(R.layout.main_view, null);
        mMainView.setOnTouchListener(this);
        mMainView.findViewById(R.id.refresh_button).setOnTouchListener(this);
        mMainView.findViewById(R.id.btn_exit).setOnTouchListener(this);

        mRootLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        mRootLayoutParams.gravity = Gravity.TOP | Gravity.END;

        windowManager.addView(mMainView, mRootLayoutParams);

        mRootLayoutParams.x = 0;
        mRootLayoutParams.y = 100;
        windowManager.updateViewLayout(mMainView, mRootLayoutParams);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMainView != null) {
            windowManager.removeView(mMainView);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP && !isMoving) {
            switch (view.getId()) {
                case R.id.refresh_button:
                    resetTimer();
                    break;
                case R.id.btn_exit:
                    stopSelf();
                    break;
                default:
                    mMainView.toggleExpanded();
            }
        } else if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
            if (mMainView.isExpanded()) {
                mMainView.setExpanded(false);
            }
        }
        dragTray(motionEvent.getAction(), (int)motionEvent.getRawX(), (int)motionEvent.getRawY());
        return true;
    }

    // Drags the tray as per touch info
    private void dragTray(int action, int x, int y){
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mPrevDragX = x;
                mPrevDragY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                isMoving = true;
                // Calculate position of the whole tray according to the drag, and update layout.
                float deltaX = x-mPrevDragX;
                float deltaY = y-mPrevDragY;
                mRootLayoutParams.x -= deltaX;
                mRootLayoutParams.y += deltaY;
                mPrevDragX = x;
                mPrevDragY = y;
                windowManager.updateViewLayout(mMainView, mRootLayoutParams);
                break;

            case MotionEvent.ACTION_UP:
                isMoving = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
    }

    //
    // CustomTimer.TimerListener
    //

    @Override
    public void onTick(long timeLeft) {
        mMainView.onTick(timeLeft);
        if (timeLeft <= 0) {
            Utils.playSoundWithVibration(this, R.raw.pikachu);
        }
    }

    //
    // MainView.MainViewListener
    //

    @Override
    public long getRemainingTime() {
        return mTimer.getTimeRemaining();
    }
}
