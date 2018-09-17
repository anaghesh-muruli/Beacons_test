package anaghesh.beacons_test;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ufobeaconsdk.callback.OnConnectSuccessListener;
import com.ufobeaconsdk.callback.OnFailureListener;
import com.ufobeaconsdk.callback.OnScanSuccessListener;
import com.ufobeaconsdk.callback.OnSuccessListener;
import com.ufobeaconsdk.main.UFOBeaconManager;
import com.ufobeaconsdk.main.UFODevice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static anaghesh.beacons_test.ScanQR.VIN_NUM;

public class Navigation_home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private ImageView checkin_img, park, findcar, checkout;
    private UFODevice ufoDevice;
    LocationManager locationManager;
    public static  SharedPreferences sharedpreferences;
    public static SharedPreferences sharedPreferences;
    String vinNum;
    private double lat, lng;
    private UFOBeaconManager ufoBeaconManager;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupUI();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        if (! mBtAdapter.isEnabled()) {
            mBtAdapter.enable();
        }

        Log.e("Activity","Navigation_home");
        sharedPreferences = getSharedPreferences("Database", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        sharedpreferences = getSharedPreferences("Database", Context.MODE_PRIVATE);
         vinNum =  sharedPreferences.getString(VIN_NUM, "");

        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }


        Log.e("Beacon Macid",""+ScanQR.Macid);
        ufoBeaconManager = new UFOBeaconManager(this);
        if(ScanQR.Macid!=null)
            isBlutoothEnabled();

       // doInBackground();
        //Home page elements
        checkin_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Navigation_home.this, ScanQR.class));
            }
        });
        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(Navigation_home.this, Parking.class));
            }
        });
        findcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Navigation_home.this,FindCar.class));
            }
        });
        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Navigation_home.this,Checkout.class));
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    Navigation_home.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        Toast.makeText(Navigation_home.this, "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(Navigation_home.this, "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    void doInBackground(){
       Thread t1 =  new Thread(new Runnable() {
            @Override
            public void run() {

                Log.e("Beacon scan thread","Running");
                isBlutoothEnabled();



            }
        });
       t1.start();
    }
    void setupUI(){
        checkin_img = findViewById(R.id.checkin);
        park = findViewById(R.id.park);
        findcar = findViewById(R.id.findcar);
        checkout = findViewById(R.id.checkout);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.bluetooth) {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()) {
                item.setIcon(R.drawable.ic_bluetooth_black_24dp);
                mBluetoothAdapter.disable();
                Toast.makeText(this, "Bluetooth is OFF", Toast.LENGTH_SHORT).show();
            }
            else{
                item.setIcon(R.drawable.ic_bluetooth_blue);
                mBluetoothAdapter.enable();
                Toast.makeText(this, "Bluetooth is ON", Toast.LENGTH_SHORT).show();
        }}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }  else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void startScan(){

//        isBlutoothEnabled();
//        isLoactonEnabled();
//     progressDialog.setMessage("Scanning");
//     progressDialog.show();

        Log.e("Method","startScan");
        ufoBeaconManager.startScan(new OnScanSuccessListener()
                                   {
                                       @Override public void onSuccess(final UFODevice ufodevice) {
                                           runOnUiThread(new Runnable() {
                                               @Override public void run(){
                                                   Log.e("Inside","onSuccess");


                                                   // connect_beacons(ufodevice);
                                                   String s = ufodevice.getBtdevice().getAddress();
                                                   Log.e("MacId",""+ufodevice.getBtdevice().getAddress());
                                                   if(s.equalsIgnoreCase(ScanQR.Macid.trim())){
                                                       Log.e("Connected to",ScanQR.Macid);
                                                       getLocation();
                                                       Log.e("Latitude", ""+lat);
                                                       Log.e("Longitude", ""+lng);
                                                       Log.e("CarVIN", ""+vinNum);
                                                       locationLogApi();
                                                       parkingApi();

                                                   }

                                               } });
                                       }
                                   }, new OnFailureListener() { @Override public void onFailure(final int code, final String message)
                                   { runOnUiThread(new Runnable() { @Override public void run() {
                                       Log.e("Inside","onfailure");

                                       //progressDialog.dismiss();
                                       Toast.makeText(Navigation_home.this, "No device found", Toast.LENGTH_SHORT).show();
                                       //Update UI

                                   } }); }
                                   }
        );
    }

    void stopScan(){

        Log.e("Method","stopScan");
        ufoBeaconManager.stopScan(new OnSuccessListener()
                                  { @Override public void onSuccess(boolean isStop) { runOnUiThread(new Runnable() { @Override public void run() {
                                      Toast.makeText(Navigation_home.this, "Scan stopped", Toast.LENGTH_SHORT).show();
                                      //update UI
                                  } }); } },
                new OnFailureListener() { @Override public void onFailure(final int code, final String message)
                { runOnUiThread(new Runnable() { @Override public void run() {
                    Toast.makeText(Navigation_home.this, "Scan could not be stopped", Toast.LENGTH_SHORT).show();            } }); } });

    }
    void isBlutoothEnabled(){
        Log.e("Method","isbluoothEnabled");
        ufoBeaconManager.isBluetoothEnabled(new OnSuccessListener() { @Override public void onSuccess(boolean isSuccess)
        {
            if(isSuccess){
                startScan();
            }
        } }, new OnFailureListener() { @Override public void onFailure(int code, String message) {
            Toast.makeText(Navigation_home.this, "Please enable bluetooth from settings", Toast.LENGTH_SHORT).show();
        } });
    }

    void isLoactonEnabled(){
        Log.e("Method","isLocationEnabled");
        ufoBeaconManager.isLocationServiceEnabled(new OnSuccessListener()
        { @Override public void onSuccess(boolean isSuccess) {
            if(isSuccess){
                isBlutoothEnabled();
            }
        } }, new OnFailureListener() { @Override public void onFailure(int code, String message) {
            Toast.makeText(Navigation_home.this, "Enbale location service", Toast.LENGTH_SHORT).show();
        } });
    }

    void connect_beacons(UFODevice ufodevice){
        Log.e("method","connect_beacons");
        Log.e("Beacon dist",""+ufodevice.getDistanceInString());
        Log.e("Beacon dist",""+ufodevice.getDistance());
        Log.e("Beacon voltage",""+ufodevice.getEddystoneTLMBatteryVoltage());
        Log.e("Beacon",""+ufodevice.getDeviceType());
        Log.e("ID",""+ufodevice.getModelId());
        Log.e("Beacon Rssi",""+ufodevice.getRssi());
        Log.e("uuid",""+ufodevice.getProximityUUID());
        Log.e("MacId",""+ufodevice.getBtdevice().getAddress());


        for(int i = 1;i<11;i++){
            Log.e("Beacon Rssi",""+ufodevice.getRssi());
            if((ufodevice.getRssi())>-85){
                Log.e("Latlong"+i,"To database");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

        Log.e("Dist",""+calculateDistance(ufodevice.getTxPower(),ufodevice.getRssi()));
        ufodevice.connect(new OnConnectSuccessListener()
                          { @Override public void onSuccess(UFODevice ufoDevice)
                          {   Log.e("Status","Device connected");

                              stopScan();
                              Toast.makeText(Navigation_home.this, "Device connected", Toast.LENGTH_SHORT).show();
                          } },
                new OnFailureListener() {
                    @Override public void onFailure(final int code, final String message)
                    { runOnUiThread(new Runnable() { @Override public void run()
                    {
                        Log.e("Status","Device connection failed");
                        Toast.makeText(Navigation_home.this, "Connection failed", Toast.LENGTH_SHORT).show();
                    }
                    } );}
                });
        Log.e("Device connect","Ended");
    }
    protected static double calculateDistance(int txPower, int rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }
    private void locationLogApi() {

        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/location_log/add";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                        Log.e("Response","1");


                    } else if(obj.getInt("Code")==0) {
                        Log.e("Response","0");

                      //  Toast.makeText(Parking.this, "VIN does not exist", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }

        }) {

            @Override
            protected Map<String, String> getParams() {
                Log.e("Inside","getParams");

                Map<String, String> params = new HashMap<String, String>();
                //  params.put("CarVIN", vin_result.getText().toString());

                    params.put("CarVIN", ""+vinNum);
                Log.e("CarVIN", ""+vinNum);
                params.put("Latitude", ""+lat);
                params.put("Longitude", ""+lng);
                Log.e("Latitude", ""+lat);
                Log.e("Longitude", ""+lng);

                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }


    void getLocation() {
        Log.e("getLocation","called");
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            lat = location.getLatitude();
            lng = location.getLongitude();
//            locationText.setText(locationText.getText() + "\n"+addresses.get(0).getAddressLine(0)+", "+
//                    addresses.get(0).getAddressLine(1)+", "+addresses.get(0).getAddressLine(2));
        }catch(Exception e)
        {

        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(Navigation_home.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    private void parkingApi() {

        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/becon_car_map/updateParked";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                        Log.e("Response","1");
                    } else if(obj.getInt("Code")==0) {
                        Log.e("Response","0");
                        Toast.makeText(Navigation_home.this, "VIN does not exist", Toast.LENGTH_SHORT).show();
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
                params.put("CarVIN", ""+vinNum);
                Log.e("CarVIN", ""+vinNum);
                params.put("Latitude", ""+lat);
                params.put("Longitude", ""+lng);
                params.put("IsParked", ""+0);
                params.put("MappingUpdatedBy", ""+123);
                Log.e("Latitude", ""+lat);
                Log.e("Longitude", ""+lng);

                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }
}
