package anaghesh.beacons_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ufobeaconsdk.callback.OnConnectSuccessListener;
import com.ufobeaconsdk.callback.OnFailureListener;
import com.ufobeaconsdk.callback.OnScanSuccessListener;
import com.ufobeaconsdk.callback.OnSuccessListener;
import com.ufobeaconsdk.main.UFOBeaconManager;
import com.ufobeaconsdk.main.UFODevice;
import com.ufobeaconsdk.main.UFODeviceType;

import static anaghesh.beacons_test.Parking.Lat;
import static anaghesh.beacons_test.Parking.sharedpreferences;
import static anaghesh.beacons_test.ScanQR.VIN_NUM;

//Uses Google maps API
public class FindcarMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView navigation;
    private double lat,lng;
    private TextView zone_result,vin_result;
    private UFODevice ufodevice;
    LatLng latLng;
    public static SharedPreferences sharedPreferences;
    private UFOBeaconManager ufoBeaconManager;
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
        sharedPreferences = getSharedPreferences("Database", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ufoBeaconManager = new UFOBeaconManager(this);
        isLoactonEnabled();

        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Lat",""+sharedPreferences.getString(Lat, ""));
                Log.e("Long",""+sharedPreferences.getString(Parking.Long, ""));
                String uri = "http://maps.google.com/maps?=" + "&daddr=" + sharedPreferences.getString(Lat, "") + "," + sharedPreferences.getString(Parking.Long, "");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }
        });
        vin_result.setText(sharedPreferences.getString(VIN_NUM,""));
    }

    void setupUI(){
        navigation = findViewById(R.id.navigation);
        vin_result = findViewById(R.id.vin_result);
        zone_result = findViewById(R.id.parkedzone_result);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String lat11=sharedpreferences.getString(Lat,"");
        lat = Double.parseDouble(lat11);
        String long11 = sharedpreferences.getString(Parking.Long,"");
        lng = Double.parseDouble(long11);
        // Add a marker in Sydney and move the camera
        latLng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
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


                                                   connect_beacons(ufodevice);
                                                   //progressDialog.dismiss();
                                                   if (ufodevice != null && ufodevice.getDeviceType() == UFODeviceType.EDDYSTONE){
                                                       ufodevice.getProximityUUID();
                                                       //macid.setText(ufodevice.getBtdevice().getAddress());
                                                       Toast.makeText(FindcarMaps.this, "Car No:211 is NearBy"+ ufodevice.getDistanceInString(), Toast.LENGTH_SHORT).show();
                                                        Log.e("Beacon dist",""+ufodevice.getDistanceInString());
                                                        Log.e("Beacon proximity",""+ufodevice.getProximityUUID());
                                                        Log.e("Beacon Rssi",""+ufodevice.getRssi());


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
            Toast.makeText(FindcarMaps.this, "Please enable bluetooth from settings", Toast.LENGTH_SHORT).show();
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
                       Toast.makeText(FindcarMaps.this, "Car No: --- is NearBy", Toast.LENGTH_SHORT).show();
                    }
                    } );}
                });
    }

}
