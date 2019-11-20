package com.aspirasoft.huntrek.activity;

import android.os.Bundle;
import android.util.Log;

/**
 * @deprecated It's recommended that you base your code directly on ActivityCollect or make your own NativeActitivty implementation.
 **/
public class ActivityCollectNative extends ActivityCollect {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("Unity", "ActivityCollectNative has been deprecated, please update your AndroidManifest to use ActivityCollect instead");
        super.onCreate(savedInstanceState);
    }
}
