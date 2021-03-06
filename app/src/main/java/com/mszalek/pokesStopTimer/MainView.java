package com.mszalek.pokesStopTimer;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Marcinus on 2016-08-07.
 */
public class MainView extends FrameLayout {

    private TextView tvTimeLeft;
    private ImageView mCompactedView;
    private ImageButton mRefreshButton;
    private View mExpandedView;
    private MainViewListener mListener;

    private boolean isExpanded;

    /**
     *
     * @param context must implement MainViewListener
     */
    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mListener = (MainViewListener) context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvTimeLeft = (TextView) findViewById(R.id.tv_time_left);
        mCompactedView = (ImageView) findViewById(R.id.compacted_view);
        mRefreshButton = (ImageButton) findViewById(R.id.refresh_button);
        mExpandedView = findViewById(R.id.expanded_view);
    }

    public void updateTimeLeft(long timeLeft) {
        tvTimeLeft.setText(Utils.timestampToMMss(timeLeft));
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void toggleExpanded() {
        setExpanded(!isExpanded);
    }

    public void resetTimer(long totalWaitingTime, long interval) {
        updateTimeLeft(totalWaitingTime);
        animateRefreshButton();
        mCompactedView.setEnabled(false);
    }

    public void onTimerEnded() {
        mCompactedView.setEnabled(true);
        mRefreshButton.setEnabled(true);
        updateTimeLeft(0);
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
        if (isExpanded) {
            mCompactedView.setVisibility(GONE);
            mExpandedView.setVisibility(VISIBLE);
            updateTimeLeft(mListener.getRemainingTime());
        } else {
            mCompactedView.setVisibility(VISIBLE);
            mExpandedView.setVisibility(GONE);
        }
    }

    public void onTick(long timeLeft) {
        if (timeLeft > 0) {
            if (isExpanded) {
                updateTimeLeft(timeLeft);
            }
        } else {
            onTimerEnded();
        }
    }

    //
    // MainViewListener
    //

    public interface MainViewListener {
        long getRemainingTime();
    }

    //
    // ANIMATION
    //

    private void animateRefreshButton() {
        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(getContext(), R.animator.anim_flip);
        anim.setTarget(mRefreshButton);
        anim.addListener(new MyAnimatorListener());
        anim.start();
    }

    private class MyAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setExpanded(false);
                }
            }, 1000);
            mRefreshButton.setEnabled(false);
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    }
}
