package anaghesh.beacons_test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidadvance.topsnackbar.TSnackbar;
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

import java.text.DecimalFormat;

//Uses Google maps API and UFO Beacon SDK
public class FindcarMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView navigation;
    private double lat, lng;
    private TextView zone_result, vin_result;
    private UFODevice ufodevice;
    LatLng latLng;
    String Macid,zone;
    public static SharedPreferences sharedPreferences;
    private UFOBeaconManager ufoBeaconManager; //UFO Beacon SDK
    private static DecimalFormat df2 = new DecimalFormat("#0.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findcar_maps);
        toolbarSetup();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setupUI();
//        sharedPreferences = getSharedPreferences("Database", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
        ufoBeaconManager = new UFOBeaconManager(this);
        isBlutoothEnabled();

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

    void setupUI() {
        navigation = findViewById(R.id.navigation);
        vin_result = findViewById(R.id.vin_result);
        zone_result = findViewById(R.id.parkedzone_result);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car);

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        latLng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Car parked here")
                .icon(icon));
        mMap.getUiSettings().isZoomControlsEnabled();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


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

}
