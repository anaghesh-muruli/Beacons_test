package anaghesh.beacons_test;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
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

import static anaghesh.beacons_test.Parking.Lat;
import static anaghesh.beacons_test.Parking.sharedpreferences;
import static anaghesh.beacons_test.ScanQR.VIN_NUM;

//Uses Google maps API and UFO Beacon SDK
public class FindcarMaps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ImageView navigation;
    private double lat, lng;
    private TextView zone_result, vin_result;
    private UFODevice ufodevice;
    LatLng latLng;
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
        sharedPreferences = getSharedPreferences("Database", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ufoBeaconManager = new UFOBeaconManager(this);


        navigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Lat", "" + sharedPreferences.getString(Lat, ""));
                Log.e("Long", "" + sharedPreferences.getString(Parking.Long, ""));
                String uri = "http://maps.google.com/maps?=" + "&daddr=" + sharedPreferences.getString(Lat, "") + "," + sharedPreferences.getString(Parking.Long, "");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);

            }
        });
        vin_result.setText(sharedPreferences.getString(VIN_NUM, ""));
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
        String lat11 = sharedpreferences.getString(Lat, "");
        lat = Double.parseDouble(lat11);
        String long11 = sharedpreferences.getString(Parking.Long, "");
        lng = Double.parseDouble(long11);
        // Add a marker in Sydney and move the camera
        latLng = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Car parked here")
                .icon(icon));
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


                                                   connect_beacons(ufodevice);
                                                   //progressDialog.dismiss();
                                                   if (ufodevice != null && ufodevice.getDeviceType() == UFODeviceType.EDDYSTONE){
                                                       ufodevice.getProximityUUID();
                                                       //macid.setText(ufodevice.getBtdevice().getAddress());
                                                       NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(FindcarMaps.this);

                                                       Intent i = new Intent(FindcarMaps.this, FindcarMaps.class);
                                                       PendingIntent pendingIntent= PendingIntent.getActivity(FindcarMaps.this,0,i,0);

                                                       mBuilder.setAutoCancel(true);
                                                       mBuilder.setDefaults(NotificationCompat.DEFAULT_ALL);
                                                       mBuilder.setTicker("Ticker");
                                                       mBuilder.setContentInfo("Distance info");
                                                       mBuilder.setContentIntent(pendingIntent);
                                                       mBuilder.setSmallIcon(R.drawable.notification);
                                                       mBuilder.setContentTitle("Car is Near");
                                                       mBuilder.setColor(ContextCompat.getColor(FindcarMaps.this, R.color.fulassure));
                                                       mBuilder.setContentText("Car distance is "+ df2.format(calculateDistance(ufodevice.getTxPower(),ufodevice.getRssi()))+" m");
                                                       mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                                                       NotificationManager notificationManager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                                       notificationManager.notify(2,mBuilder.build());
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
        Log.e("Beacon dist",""+ufodevice.getDistanceInString());
        Log.e("Beacon dist",""+ufodevice.getDistance());
        Log.e("Beacon voltage",""+ufodevice.getEddystoneTLMBatteryVoltage());
        Log.e("Beacon",""+ufodevice.getDeviceType());
       Log.e("ID",""+ufodevice.getModelId());
        Log.e("Beacon Rssi",""+ufodevice.getRssi());


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
