package com.slyworks.cryptowatcher;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.slyworks.cryptowatcher.tracking.Tracker;

/**
 * Created by Joshua Sylvanus, 8:25am, 27/08/2020.
 */
public class TrackingActivity extends AppCompatActivity {
//region Vars
    protected Tracker mTracker;
//endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTracker = new Tracker(this);
    }
}