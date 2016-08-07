package com.mszalek.pokesStopTimer;

import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.annotation.RawRes;
import android.view.Display;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

/**
 * Created by Marcinus on 2016-08-07.
 */
public class Utils {

    public static String timestampToMMss(long timestamp) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timestamp);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timestamp)%60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static void playSoundWithVibration(Context context, @RawRes int audioResId){
        MediaPlayer.create(context, audioResId).start();
        ((Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
    }

    public static int getScreenWidth(WindowManager windowManager) {
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
