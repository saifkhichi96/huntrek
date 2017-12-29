package com.aspirasoft.huntrek.bo;

import android.app.Activity;
import android.view.View;

import com.aspirasoft.huntrek.R;
import com.aspirasoft.huntrek.utils.GameTimeListener;

import java.util.Calendar;

/**
 * Created by saifkhichi96 on 29/12/2017.
 */

public class EnvironmentController {

    private final Activity context;
    private final View mLightOverlay;
    private final View mSkyOverlay;

    private int lastKnownHour = 0;
    private int overlayColor;

    private GameTimeListener timeListener;

    public EnvironmentController(Activity activity) {
        this.context = activity;
        this.mLightOverlay = activity.findViewById(R.id.lightOverlay);
        this.mSkyOverlay = activity.findViewById(R.id.skyOverlay);
    }

    public void update(long elapsed) {
        int hourNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (timeListener != null) {
            if (hasDayArrived(hourNow)) {
                timeListener.onDayBreak();
            } else if (hasNightArrived(hourNow)) {
                timeListener.onNightArrived();
            }
        }

        mLightOverlay.setBackgroundColor(getTimeSpecificColor(hourNow));
        // mSkyOverlay.setBackgroundColor(getTimeSpecificColor(hourNow));

        lastKnownHour = hourNow;
    }

    public void setGameTimeListener(GameTimeListener timeListener) {
        this.timeListener = timeListener;
    }

    private boolean isDay(int hour) {
        GameTime gameTime = toGameTime(hour);
        return gameTime == GameTime.DAWN || gameTime == GameTime.MORNING ||
                gameTime == GameTime.NOON || gameTime == GameTime.AFTERNOON;
    }

    private boolean hasDayArrived(int hourNow) {
        return isDay(hourNow) && !isDay(lastKnownHour);
    }

    private boolean hasNightArrived(int hourNow) {
        return !isDay(hourNow) && isDay(lastKnownHour);
    }

    private int getTimeSpecificColor(int hourNow) {
        GameTime gameTimeNow = toGameTime(hourNow);

        int color;
        switch (gameTimeNow) {
            case DAWN:
                color = context.getResources().getColor(R.color.overlayDawn);
                break;
            case MORNING:
                color = context.getResources().getColor(R.color.overlayDawn);
                break;
            case NOON:
                color = context.getResources().getColor(R.color.overlayDay);
                break;
            case AFTERNOON:
                color = context.getResources().getColor(R.color.overlayDay);
                break;
            case EVENING:
                color = context.getResources().getColor(R.color.overlayDay);
                break;
            case SUNSET:
                color = context.getResources().getColor(R.color.overlayDusk);
                break;
            case DUSK:
                color = context.getResources().getColor(R.color.overlayDusk);
                break;
            default:
                color = context.getResources().getColor(R.color.overlayNight);
                break;
        }

        return color;
    }

    private GameTime toGameTime(int hour) {
        if (hour > 4 && hour <= 7) return GameTime.DAWN;
        else if (hour > 7 && hour < 11) return GameTime.MORNING;
        else if (hour >= 11 && hour < 13) return GameTime.NOON;
        else if (hour >= 13 && hour < 15) return GameTime.AFTERNOON;
        else if (hour >= 15 && hour < 18) return GameTime.EVENING;
        else if (hour >= 18 && hour < 19) return GameTime.SUNSET;
        else if (hour >= 19 && hour < 20) return GameTime.DUSK;
        else return GameTime.NIGHT;
    }

    private enum GameTime {
        DAWN,
        MORNING,
        NOON,
        AFTERNOON,
        EVENING,
        SUNSET,
        DUSK,
        NIGHT
    }

}