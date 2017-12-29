package com.aspirasoft.huntrek.bo.maps;

import android.location.Location;
import android.support.annotation.NonNull;


public interface LocationReceiver {
    void onLocationReceived(@NonNull Location location);
}
