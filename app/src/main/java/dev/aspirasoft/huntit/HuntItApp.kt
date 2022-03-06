package dev.aspirasoft.huntit

import androidx.multidex.MultiDexApplication
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.FirebaseApp
import dev.aspirasoft.huntit.data.source.DataSource

class HuntItApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        FirebaseApp.initializeApp(this)
        DataSource.init(this)
    }

    companion object {
        const val TAG = "HunTrek"
    }

}