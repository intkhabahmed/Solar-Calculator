package com.intkhabahmed.solarcalculator.ui;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intkhabahmed.solarcalculator.R;
import com.intkhabahmed.solarcalculator.databinding.ActivityMapsBinding;
import com.intkhabahmed.solarcalculator.util.AppConstants;
import com.intkhabahmed.solarcalculator.util.DateChangeListener;
import com.intkhabahmed.solarcalculator.util.DateUtil;
import com.intkhabahmed.solarcalculator.util.Moon;
import com.intkhabahmed.solarcalculator.util.PhaseCalcUtils;

import java.util.Calendar;
import java.util.Date;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DateChangeListener {

    private GoogleMap mMap;
    private static long currentTime;
    private ActivityMapsBinding mMapsBinding;
    private DateChangeListener dateChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMapsBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupUiData();
    }

    private void setupUiData() {
        dateChangeListener = this;
        currentTime = System.currentTimeMillis();
        dateChangeListener.dateChanged(new Date(currentTime));
        mMapsBinding.dateTv.setText(DateUtil.getFormattedDate(currentTime));
        mMapsBinding.nextDateIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime += AppConstants.MILLIS_IN_A_DAY;
                mMapsBinding.dateTv.setText(DateUtil.getFormattedDate(currentTime));
                dateChangeListener.dateChanged(new Date(currentTime));
            }
        });

        mMapsBinding.previousDateIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime -= AppConstants.MILLIS_IN_A_DAY;
                mMapsBinding.dateTv.setText(DateUtil.getFormattedDate(currentTime));
                dateChangeListener.dateChanged(new Date(currentTime));
            }
        });

        mMapsBinding.resetDateIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime = System.currentTimeMillis();
                mMapsBinding.dateTv.setText(DateUtil.getFormattedDate(currentTime));
                dateChangeListener.dateChanged(new Date(currentTime));
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void dateChanged(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mMapsBinding.sunRiseTimeTv.setText(DateUtil.getFormattedTime(PhaseCalcUtils.calcSunriseAndSunset
                (PhaseCalcUtils.getDayOfYear(day, month, year), true, 18.5204, 73.8567)));
        mMapsBinding.sunSetTimeTv.setText(DateUtil.getFormattedTime(PhaseCalcUtils.calcSunriseAndSunset
                (PhaseCalcUtils.getDayOfYear(day, month, year), false, 18.5204, 73.8567)));
        double[] moonRiseSet = Moon.riseSet(date, Moon.getTimeZoneOffset(date), 18.5204, 73.8567);
        mMapsBinding.moonRiseTv.setText(DateUtil.getFormattedTime((long) moonRiseSet[1]));
        mMapsBinding.moonSetTimeTv.setText(DateUtil.getFormattedTime((long) moonRiseSet[0]));
    }
}
