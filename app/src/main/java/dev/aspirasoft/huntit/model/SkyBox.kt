package dev.aspirasoft.huntit.model

import android.app.Activity
import android.widget.ImageView
import dev.aspirasoft.huntit.R

/**
 * Created by saifkhichi96 on 29/12/2017.
 */
class SkyBox(context: Activity) {

    private val clouds = arrayOfNulls<ImageView>(MAX_CLOUDS)
    private val cloudSpeed = FloatArray(MAX_CLOUDS)

    init {
        clouds[0] = context.findViewById(R.id.cloud_1)
        clouds[1] = context.findViewById(R.id.cloud_2)
        clouds[2] = context.findViewById(R.id.cloud_3)

        cloudSpeed[0] = 0.010f
        cloudSpeed[1] = 0.050f
        cloudSpeed[2] = 0.025f
    }

    fun update(time: Long) {
        for (i in 0 until MAX_CLOUDS) {
            val v = cloudSpeed[i]
            val s = v * time
            clouds[i]!!.x = clouds[i]!!.x + s
        }
    }

    companion object {
        private const val MAX_CLOUDS = 3
    }

}