package anaghesh.beacons_test;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ufobeaconsdk.callback.OnConnectSuccessListener;
import com.ufobeaconsdk.callback.OnFailureListener;
import com.ufobeaconsdk.callback.OnScanSuccessListener;
import com.ufobeaconsdk.callback.OnSuccessListener;
import com.ufobeaconsdk.main.UFOBeaconManager;
import com.ufobeaconsdk.main.UFODevice;
     /* created by Anaghesh Muruli on 14-08-2018
      * github repo: Beacons_test
      * Added dependency from UFO Beacon SDK
      *
      *
      * IMP: This activity is deprecated */


@Deprecated
public class MainActivity extends AppCompatActivity {
    UFOBeaconManager ufoBeaconManager = new UFOBeaconManager(this);
    private Button connect;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(this);
        setupUI();
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLoactonEnabled();
            }
        });

//Method to setup UIs
 }
 void setupUI(){
        connect = findViewById(R.id.connect);
 }


 void startScan(){
//        isBlutoothEnabled();
//        isLoactonEnabled();
     progressDialog.setMessage("Scanning");
     progressDialog.show();

     Log.e("Method","startScan");
     ufoBeaconManager.startScan(new OnScanSuccessListener()
     {
         @Override public void onSuccess(final UFODevice ufodevice) {
             runOnUiThread(new Runnable() {
                 @Override public void run(){
         Log.e("Inside","onSuccess");
         connect_beacons(ufodevice);   //Fetch the data from Beacon

         Toast.makeText(MainActivity.this, "Device connected", Toast.LENGTH_SHORT).show();
         progressDialog.dismiss();

         } });
    }
     }, new OnFailureListener() { @Override public void onFailure(final int code, final String message)
     { runOnUiThread(new Runnable() { @Override public void run() {
         Log.e("Inside","onfailure");

         progressDialog.dismiss();
         Toast.makeText(MainActivity.this, "No device found", Toast.LENGTH_SHORT).show();
         //Update UI

     } }); }
     }
     );
}
    //To be implement stopScan
    void stopScan(){

    Log.e("Method","stopScan");
    ufoBeaconManager.stopScan(new OnSuccessListener()
    { @Override public void onSuccess(boolean isStop) { runOnUiThread(new Runnable() { @Override public void run() {
        Toast.makeText(MainActivity.this, "Scan stopped", Toast.LENGTH_SHORT).show();
        //update UI
    } }); } },
            new OnFailureListener() { @Override public void onFailure(final int code, final String message)
            { runOnUiThread(new Runnable() { @Override public void run() {
                Toast.makeText(MainActivity.this, "Scan could not be stopped", Toast.LENGTH_SHORT).show();            } }); } });

}
    void isBlutoothEnabled(){
        Log.e("Method","isbluoothEnabled");
    ufoBeaconManager.isBluetoothEnabled(new OnSuccessListener() { @Override public void onSuccess(boolean isSuccess)
    {
        if(isSuccess){
            startScan();
        }
   } }, new OnFailureListener() { @Override public void onFailure(int code, String message) {
        Toast.makeText(MainActivity.this, "Please enable bluetooth from settings", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(MainActivity.this, "Enbale location service", Toast.LENGTH_SHORT).show();
    } });
}

    void connect_beacons(UFODevice ufodevice){
        Log.e("method","connect_beacons");
    ufodevice.connect(new OnConnectSuccessListener()
    { @Override public void onSuccess(UFODevice ufoDevice)
    {   Log.e("Status","Device connected");
        //Toast.makeText(MainActivity.this, "Device connected", Toast.LENGTH_SHORT).show();
         } },
            new OnFailureListener() {
        @Override public void onFailure(final int code, final String message)
        { runOnUiThread(new Runnable() { @Override public void run()
        {
            Log.e("Status","Device connection failed");
            Toast.makeText(MainActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
             }
        } );}
    });
    }

/*  Next phase
void deviceType(){
if (ufodevice != null && ufodevice.getDeviceType() == UFODeviceType.EDDYSTONE);// its Eddystone model

}
*/

}