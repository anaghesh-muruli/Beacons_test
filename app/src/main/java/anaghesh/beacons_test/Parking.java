package anaghesh.beacons_test;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ufobeaconsdk.callback.OnFailureListener;
import com.ufobeaconsdk.callback.OnSuccessListener;
import com.ufobeaconsdk.main.UFOBeaconManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static anaghesh.beacons_test.ScanQR.BEACON_NUM;
import static anaghesh.beacons_test.ScanQR.VIN_NUM;

//uses google maps API
public class Parking extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    private TextView bcn_result, vin_result;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    UFOBeaconManager ufoBeaconManager;
    Marker mCurrLocationMarker;
    static public int flag = 0;
    public static SharedPreferences sharedPreferences;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    GoogleApiClient mGoogleApiClient;
    public static  SharedPreferences sharedpreferences;
    FusedLocationProviderClient mFusedLocationClient;
    public static final String Lat = "curLat";
    public static final String Long = "curLong";
    public double lat,lng;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);
        toolbarSetup();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Button confirm = findViewById(R.id.confirm_park);
        vin_result = findViewById(R.id.vin_res);
        bcn_result = findViewById(R.id.bcn_res);
        ufoBeaconManager = new UFOBeaconManager(this);
        sharedPreferences = getSharedPreferences("Database", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        sharedpreferences = getSharedPreferences("Database", Context.MODE_PRIVATE);
        vin_result.setText(sharedPreferences.getString(VIN_NUM, ""));
        bcn_result.setText(sharedPreferences.getString(BEACON_NUM, ""));
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                parkingApi();

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
  //  @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.markertest);

            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                //Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;


                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Current Location")
                        .icon(icon));
                mGoogleMap.animateCamera(yourLocation);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                lat =latLng.latitude;
               lng = latLng.longitude;
                editor.apply();



            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Parking.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    //Intent intent = new Intent(park_activity.this , ActivityPermission.class);
                    //startActivity(intent);
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void settingsrequest()
    {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            mGoogleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            mGoogleApiClient.connect();
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(120000);
        locationRequest.setFastestInterval(120000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Parking.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        settingsrequest();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    void toolbarSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar_parking);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    //Api to POST LatLong
    private void parkingApi() {

        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/becon_car_map/updateParked";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                        flag =1;
                        Log.e("Response","1");
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Parking.this);
                        builder.setTitle("Parking Successful");
                        builder.setIcon(R.mipmap.ic_launcher);
                        builder.setMessage("The vehicle has been parked successfully")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                      startActivity(new Intent(Parking.this, Navigation_home.class));

                                    }
                                });
                        android.app.AlertDialog alert = builder.create();
                        alert.show();


                    } else if(obj.getInt("Code")==0) {
                        Log.e("Response","0");

                        Toast.makeText(Parking.this, "VIN does not exist", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("Response Error", error.toString());
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                Log.e("Inside","getParams");

                Map<String, String> params = new HashMap<String, String>();
              //  params.put("CarVIN", vin_result.getText().toString());
                params.put("CarVIN", ""+vin_result.getText());
                Log.e("CarVIN", ""+vin_result.getText().toString());
                params.put("Latitude", ""+lat);
                params.put("Longitude", ""+lng);
                params.put("MappingUpdatedBy", ""+123);
                params.put("IsParked", ""+1);

                Log.e("Latitude", ""+lat);
               Log.e("Longitude", ""+lng);

                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }
    void stopScan(){

        Log.e("Method","stopScan");
        ufoBeaconManager.stopScan(new OnSuccessListener()
                                  { @Override public void onSuccess(boolean isStop) { runOnUiThread(new Runnable() { @Override public void run() {
                                      Toast.makeText(Parking.this, "Scan stopped", Toast.LENGTH_SHORT).show();
                                      //update UI
                                  } }); } },
                new OnFailureListener() { @Override public void onFailure(final int code, final String message)
                { runOnUiThread(new Runnable() { @Override public void run() {
                    Toast.makeText(Parking.this, "Scan could not be stopped", Toast.LENGTH_SHORT).show();            } }); } });

    }
}
