package dev.aspirasoft.huntit.data

import android.app.Activity
import android.view.View
import androidx.core.content.ContextCompat
import dev.aspirasoft.huntit.R
import dev.aspirasoft.huntit.listener.TimeListener
import java.util.*

/**
 * Created by saifkhichi96 on 29/12/2017.
 */
class GameEnvironment(private val context: Activity) {

    private val mLightOverlay: View = context.findViewById(R.id.lightOverlay)
    private val mSkyOverlay: View = context.findViewById(R.id.skyOverlay)

    private var lastKnownHour = 0
    private val overlayColor = 0

    private var timeListener: TimeListener? = null

    fun update(elapsed: Long) {
        val hourNow = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        if (timeListener != null) {
            if (hasDayArrived(hourNow)) {
                timeListener!!.onDayBreak()
            } else if (hasNightArrived(hourNow)) {
                timeListener!!.onNightArrived()
            }
        }
        mLightOverlay.setBackgroundColor(getTimeSpecificColor(hourNow))
        mSkyOverlay.setBackgroundColor(getTimeSpecificColor(hourNow))
        lastKnownHour = hourNow
    }

    fun setGameTimeListener(timeListener: TimeListener) {
        this.timeListener = timeListener
    }

    private fun isDay(hour: Int): Boolean {
        val gameTime = dev.aspirasoft.huntit.data.GameEnvironment.GameTime.Companion.fromHour(hour)
        return gameTime == dev.aspirasoft.huntit.data.GameEnvironment.GameTime.DAWN || gameTime == dev.aspirasoft.huntit.data.GameEnvironment.GameTime.MORNING || gameTime == dev.aspirasoft.huntit.data.GameEnvironment.GameTime.NOON || gameTime == dev.aspirasoft.huntit.data.GameEnvironment.GameTime.AFTERNOON
    }

    private fun hasDayArrived(hourNow: Int): Boolean {
        return isDay(hourNow) && !isDay(lastKnownHour)
    }

    private fun hasNightArrived(hourNow: Int): Boolean {
        return !isDay(hourNow) && isDay(lastKnownHour)
    }

    private fun getTimeSpecificColor(hourNow: Int): Int {
        val color: Int = when (dev.aspirasoft.huntit.data.GameEnvironment.GameTime.Companion.fromHour(hourNow)) {
            dev.aspirasoft.huntit.data.GameEnvironment.GameTime.DAWN, dev.aspirasoft.huntit.data.GameEnvironment.GameTime.MORNING -> ContextCompat.getColor(
                context,
                R.color.overlayDawn)
            dev.aspirasoft.huntit.data.GameEnvironment.GameTime.NOON, dev.aspirasoft.huntit.data.GameEnvironment.GameTime.AFTERNOON, dev.aspirasoft.huntit.data.GameEnvironment.GameTime.EVENING -> ContextCompat.getColor(
                context,
                R.color.overlayDay)
            dev.aspirasoft.huntit.data.GameEnvironment.GameTime.SUNSET, dev.aspirasoft.huntit.data.GameEnvironment.GameTime.DUSK -> ContextCompat.getColor(
                context,
                R.color.overlayDusk)
            else -> ContextCompat.getColor(context, R.color.overlayNight)
        }
        return color
    }

    /**
     * Enumeration of different game times.
     */
    private enum class GameTime {
        DAWN, MORNING, NOON, AFTERNOON, EVENING, SUNSET, DUSK, NIGHT;

        companion object {
            fun fromHour(hour: Int): dev.aspirasoft.huntit.data.GameEnvironment.GameTime {
                return when (hour) {
                    in 5..7 -> dev.aspirasoft.huntit.data.GameEnvironment.GameTime.DAWN
                    in 8..10 -> dev.aspirasoft.huntit.data.GameEnvironment.GameTime.MORNING
                    in 11..12 -> dev.aspirasoft.huntit.data.GameEnvironment.GameTime.NOON
                    in 13..14 -> dev.aspirasoft.huntit.data.GameEnvironment.GameTime.AFTERNOON
                    in 15..17 -> dev.aspirasoft.huntit.data.GameEnvironment.GameTime.EVENING
                    18 -> dev.aspirasoft.huntit.data.GameEnvironment.GameTime.SUNSET
                    19 -> dev.aspirasoft.huntit.data.GameEnvironment.GameTime.DUSK
                    else -> dev.aspirasoft.huntit.data.GameEnvironment.GameTime.NIGHT
                }
            }
        }
    }

}