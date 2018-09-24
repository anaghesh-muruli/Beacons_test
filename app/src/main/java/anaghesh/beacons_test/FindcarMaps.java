package anaghesh.beacons_test;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.ufobeaconsdk.callback.OnConnectSuccessListener;
import com.ufobeaconsdk.callback.OnFailureListener;
import com.ufobeaconsdk.callback.OnScanSuccessListener;
import com.ufobeaconsdk.callback.OnSuccessListener;
import com.ufobeaconsdk.main.UFOBeaconManager;
import com.ufobeaconsdk.main.UFODevice;
import com.ufobeaconsdk.main.UFODeviceType;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static anaghesh.beacons_test.Parking.REQUEST_CHECK_SETTINGS;

//Uses Google maps API and UFO Beacon SDK
public class FindcarMaps extends AppCompatActivity implements OnMapReadyCallback,BeaconConsumer, RangeNotifier {
    protected static final String TAG = "RangingActivity";
    Region region;
    Button button;
    private BeaconManager mBeaconManager;
    private GoogleMap mMap;
    private ImageView navigation;

    GoogleApiClient mGoogleApiClient;
    private double lat, lng;
    private TextView zone_result, vin_result;
    private UFODevice ufodevice;
    LatLng latLng;
    String Macid,zone;
    FusedLocationProviderClient mFusedLocationClient;
    ImageView arrive;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    public static SharedPreferences sharedPreferences;
    private UFOBeaconManager ufoBeaconManager; //UFO Beacon SDK
    private static DecimalFormat df2 = new DecimalFormat("#0.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findcar_maps);
        toolbarSetup();
        arrive = findViewById(R.id.arrive);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        // In this example, we will use Eddystone protocol, so we have to define it here
        mBeaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Binds this activity to the BeaconService
        mBeaconManager.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupUI();
//        sharedPreferences = getSharedPreferences("Database", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
        ufoBeaconManager = new UFOBeaconManager(this);
      // isBlutoothEnabled();

        Intent i= getIntent();
        Bundle b = i.getExtras();

        if(b!=null)
        {
            String bcn =(String) b.get("BeaconPublicID");
            int vin =(int) b.get("carVIN");
            vin_result.setText(""+vin);
            lat = (double) b.get("lat");
            lng =(double) b.get("lng");
            zone =(String) b.get("pzName");
            Macid = (String) b.get("MacId");

            zone_result.setText(""+zone);

            Log.e("Lat",""+lat);
            Log.e("Long",""+lng);
            Log.e("Zone",""+zone);



        }
        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Lat", "" + lat);
                Log.e("Long", "" + lng);
                String uri = "http://maps.google.com/maps?=" + "&daddr=" + lat + "," + lng;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }
        });

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
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                    //Intent intent = new Intent(park_activity.this , ActivityPermission.class);
                    //startActivity(intent);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
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
                                ActivityCompat.requestPermissions(FindcarMaps.this,
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

    void setupUI() {
        navigation = findViewById(R.id.navigation);
        vin_result = findViewById(R.id.vin_result);
        zone_result = findViewById(R.id.parkedzone_result);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car);

        mMap = googleMap;

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
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }
        // Add a marker in Sydney and move the camera
        latLng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Car parked here")
                .icon(icon));
        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


    }
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
           // BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.markertest);

            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                //Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;


                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                mMap.animateCamera(yourLocation);

            }
        }
    };
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
                            status.startResolutionForResult(FindcarMaps.this, REQUEST_CHECK_SETTINGS);
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
    void toolbarSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar_findcarmap);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
                                                   if(s.equalsIgnoreCase(Macid)){
                                                       Log.e("Connected to",""+Macid);
                                                       Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                                       vibrator.vibrate(200);
                                                       TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content), "Vehicle is Near", TSnackbar.LENGTH_LONG);
                                                       snackbar.setActionTextColor(Color.WHITE);
                                                       View snackbarView = snackbar.getView();
                                                       snackbarView.setBackgroundColor(Color.parseColor("#FFC125"));
                                                       TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
                                                       textView.setTextColor(Color.YELLOW);
                                                       snackbar.show();
                                                       stopScan();

                                                   }
                                                   //progressDialog.dismiss();
                                                   if (ufodevice != null && ufodevice.getDeviceType() == UFODeviceType.EDDYSTONE){
                                                       ufodevice.getProximityUUID();
                                                       //macid.setText(ufodevice.getBtdevice().getAddress());

                                                   }

                                               } });
                                       }
                                   }, new OnFailureListener() { @Override public void onFailure(final int code, final String message)
                                   { runOnUiThread(new Runnable() { @Override public void run() {
                                       Log.e("Inside","onfailure");

                                       //progressDialog.dismiss();
                                       Toast.makeText(FindcarMaps.this, "No device found", Toast.LENGTH_SHORT).show();
                                       //Update UI

                                   } }); }
                                   }
        );
    }
    void stopScan(){

        Log.e("Method","stopScan");
        ufoBeaconManager.stopScan(new OnSuccessListener()
                                  { @Override public void onSuccess(boolean isStop) { runOnUiThread(new Runnable() { @Override public void run() {
                                      Toast.makeText(FindcarMaps.this, "Scan stopped", Toast.LENGTH_SHORT).show();
                                      //update UI
                                  } }); } },
                new OnFailureListener() { @Override public void onFailure(final int code, final String message)
                { runOnUiThread(new Runnable() { @Override public void run() {
                    Toast.makeText(FindcarMaps.this, "Scan could not be stopped", Toast.LENGTH_SHORT).show();            } }); } });

    }
    void isBlutoothEnabled(){
        Log.e("Method","isbluoothEnabled");
        ufoBeaconManager.isBluetoothEnabled(new OnSuccessListener() { @Override public void onSuccess(boolean isSuccess)
        {
            if(isSuccess){
                startScan();
            }
        } }, new OnFailureListener() { @Override public void onFailure(int code, String message) {
            Toast.makeText(FindcarMaps.this, "Please enable bluetooth", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(FindcarMaps.this, "Enbale location service", Toast.LENGTH_SHORT).show();
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
                              Toast.makeText(FindcarMaps.this, "Device connected", Toast.LENGTH_SHORT).show();
                          } },
                new OnFailureListener() {
                    @Override public void onFailure(final int code, final String message)
                    { runOnUiThread(new Runnable() { @Override public void run()
                    {
                        Log.e("Status","Device connection failed");
                        Toast.makeText(FindcarMaps.this, "Connection failed", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onBeaconServiceConnect() {
        Log.e("Inside","DidRange");
        // Encapsulates a beacon identifier of arbitrary byte length
        ArrayList<Identifier> identifiers = new ArrayList<>();
        region = new Region("AllBeaconsRegion", identifiers);
        // Set null to indicate that we want to match beacons with any value
        identifiers.add(null);
        // Represents a criteria of fields used to match beacon

        try {
            // Tells the BeaconService to start looking for beacons that match the passed Region object
            mBeaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        // Specifies a class that should be called each time the BeaconService gets ranging data, once per second by default
        mBeaconManager.addRangeNotifier(this);
    }
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        Log.e("Inside","DidRange");
        if (beacons.size() > 0) {
            Log.e("Inside","DidRange");
            Log.e(TAG, "Bluetooth MacId "+beacons.iterator().next().getBluetoothAddress());
            String s1 = beacons.iterator().next().getBluetoothAddress();
            Log.e(TAG, "Bluetooth Name "+beacons.iterator().next().getBluetoothName()+" ");
            Log.e(TAG, "Dist "+beacons.iterator().next().getDistance()+" ");
            Log.e(TAG, "Type "+beacons.iterator().next().getBeaconTypeCode()+" ");
            Log.e(TAG, "Rssi "+beacons.iterator().next().getRssi()+" ");
            Log.e(TAG, "Tx "+beacons.iterator().next().getTxPower()+" ");
            Log.e(TAG, "Tx "+beacons.iterator().next().getServiceUuid()+" ");
            if(s1.equalsIgnoreCase(Macid)){
                Log.e("Connected to",""+Macid);
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(200);
              arrive.setVisibility(View.VISIBLE);
                mBeaconManager.unbind(this);

            }
        }



    }



}
