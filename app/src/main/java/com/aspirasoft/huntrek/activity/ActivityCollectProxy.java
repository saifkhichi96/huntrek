package com.aspirasoft.huntrek.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @deprecated Use ActivityCollect instead.
 */
public class ActivityCollectProxy extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("Unity", "ActivityCollectNative has been deprecated, please update your AndroidManifest to use ActivityCollect instead");
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, ActivityCollect.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
            intent.putExtras(extras);
        startActivity(intent);
    }
}
