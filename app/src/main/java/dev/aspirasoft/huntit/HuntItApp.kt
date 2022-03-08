package dev.aspirasoft.huntit

import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.FirebaseApp
import com.mapbox.mapboxsdk.Mapbox
import dev.aspirasoft.huntit.data.source.DataSource

class HuntItApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        FirebaseApp.initializeApp(this)
        DataSource.init(this)

        // Initialize Mapbox
        Mapbox.getInstance(this, getString(R.string.mapbox_token))
    }

    companion object {
        const val TAG = "HuntIt"
    }

}