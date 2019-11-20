package com.aspirasoft.huntrek.bo.maps;

import android.location.Location;
import androidx.annotation.NonNull;


public interface LocationReceiver {
    void onLocationReceived(@NonNull Location location);
}
