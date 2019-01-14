package com.intkhabahmed.solarcalculator.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intkhabahmed.solarcalculator.R;
import com.intkhabahmed.solarcalculator.database.PinDatabase;
import com.intkhabahmed.solarcalculator.databinding.ActivityMapsBinding;
import com.intkhabahmed.solarcalculator.fragment.PinDialogFragment;
import com.intkhabahmed.solarcalculator.model.PinInfo;
import com.intkhabahmed.solarcalculator.util.AppConstants;
import com.intkhabahmed.solarcalculator.util.AppExecutors;
import com.intkhabahmed.solarcalculator.util.DateChangeListener;
import com.intkhabahmed.solarcalculator.util.DateUtil;
import com.intkhabahmed.solarcalculator.util.Moon;
import com.intkhabahmed.solarcalculator.util.PhaseCalcUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DateChangeListener, LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int PLACE_PICKER_REQUEST = 100;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 101;
    private GoogleMap mMap;
    private static long currentTime;
    private ActivityMapsBinding mMapsBinding;
    private DateChangeListener dateChangeListener;
    private PinInfo mPinInfo;
    private LatLng mLatLng;
    private LocationManager mLocationManager;

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
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mPinInfo = new PinInfo();
        getCurrentLocation();
        setupUiData();
    }

    private void getCurrentLocation() {
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FINE_LOCATION);
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3600000,
                    0.0F, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recreate();
                } else {
                    Toast.makeText(this, getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
                }
        }
    }

    private void setupUiData() {
        dateChangeListener = this;
        currentTime = System.currentTimeMillis();
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
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mLatLng = latLng;

                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                //updating PinInfo object
                mPinInfo.setLatitude(latLng.latitude);
                mPinInfo.setLongitude(latLng.longitude);
                mPinInfo.setPlaceName(getLocalityName(latLng.latitude, latLng.longitude));

                updateLocationAndTime();

            }
        });
    }

    private String getLocalityName(double latitude, double longitude) {
        Geocoder gcd = new Geocoder(MapsActivity.this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ((addresses != null ? addresses.size() : 0) > 0) {
            return addresses.get(0).getLocality();
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search_place:
                searchPlace();
                return true;
            case R.id.save_pin:
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        PinDatabase.getInstance(MapsActivity.this).pinDao().insertPin(mPinInfo);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MapsActivity.this, getString(R.string.pin_success), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                return true;
            case R.id.saved_pins:
                DialogFragment fragment = new PinDialogFragment();
                fragment.show(getSupportFragmentManager(), PinDialogFragment.class.getSimpleName());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchPlace() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, getString(R.string.need_location_permission_message), Toast.LENGTH_LONG).show();
            return;
        }
        try {
            // Start a new Activity for the Place Picker API, this will trigger {@code #onActivityResult}
            // when a place is selected or with the user cancels.
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
        }
    }

    /***
     * Called when the Place Picker Activity returns back with a selected place (or after canceling)
     *
     * @param requestCode The request code passed when calling startActivityForResult
     * @param resultCode  The result code specified by the second activity
     * @param data        The Intent that carries the result data.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            if (place == null) {
                Log.i(TAG, "No place selected");
                return;
            }

            // Extract the place information from the API
            mLatLng = place.getLatLng();
            mPinInfo.setLatitude(mLatLng.latitude);
            mPinInfo.setLongitude(mLatLng.longitude);
            mPinInfo.setPlaceName(place.getName().toString());
            updateLocationAndTime();
        }
    }

    @Override
    public void dateChanged(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        mMapsBinding.sunRiseTimeTv.setText(DateUtil.getFormattedTime(PhaseCalcUtils.calcSunriseAndSunset
                (PhaseCalcUtils.getDayOfYear(day, month, year), true, mLatLng.latitude, mLatLng.longitude)));
        mMapsBinding.sunSetTimeTv.setText(DateUtil.getFormattedTime(PhaseCalcUtils.calcSunriseAndSunset
                (PhaseCalcUtils.getDayOfYear(day, month, year), false, mLatLng.latitude, mLatLng.longitude)));
        double[] moonRiseSet = Moon.riseSet(date, Moon.getTimeZoneOffset(date), mLatLng.latitude, mLatLng.longitude);
        mMapsBinding.moonRiseTv.setText(DateUtil.getFormattedTime((long) moonRiseSet[1]));
        mMapsBinding.moonSetTimeTv.setText(DateUtil.getFormattedTime((long) moonRiseSet[0]));
    }

    private void updateLocationAndTime() {
        // Clears the previously touched position
        mMap.clear();
        CameraPosition position = new CameraPosition.Builder()
                .target(mLatLng)
                .zoom(15.0F)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 3000, null);

        //adding marker
        // Setting the title for the marker.
        // This will be displayed on taping the marker
        mMap.addMarker(new MarkerOptions().position(mLatLng).title(getLocalityName(mLatLng.latitude, mLatLng.longitude)));

        dateChangeListener.dateChanged(new Date(currentTime));
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        updateLocationAndTime();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setLatLongAndUpdate(LatLng latLng) {
        mLatLng = latLng;
        updateLocationAndTime();
    }
}
