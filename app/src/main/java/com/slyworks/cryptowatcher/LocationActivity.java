package com.slyworks.cryptowatcher;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.slyworks.cryptowatcher.location.LocationManager;

/**
 * Created by Joshua Sylvanus, 11:27PM, 30/08/2020.
 */
public class LocationActivity extends TrackingActivity {
   /*
    *class containing all the Runtime permission code for
    * Location
    */

    private static final String TAG = LocationActivity.class.getSimpleName();
    private final static int PERMISSION_REQUEST_LOCATION = 1234;
    private LocationManager mLocationManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = new LocationManager(this, mTracker);
        checkLocationPermission();
        getLifecycle().addObserver(mLocationManager);

    }

    private void checkLocationPermission() {
        Log.d(TAG, "checkLocationPermission() called");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Please give me location permissions", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);
            }
        }
        else
            mLocationManager.buildGoogleApiClient();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() called with: requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]");
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                        mLocationManager.buildGoogleApiClient();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_LOCATION);
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
