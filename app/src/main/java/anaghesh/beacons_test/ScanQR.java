package anaghesh.beacons_test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.ufobeaconsdk.main.UFODevice;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// IMP: Google vision API used. Formats to be controlled

public class ScanQR extends AppCompatActivity {
    private UFODevice ufoDevice;

    SurfaceView cameraPreview;
    EditText beacon,vin;
    BarcodeDetector barcodeDetector;
    CameraSource cameraSource;
    Button assign;
    private TextView scanInfo;
    public final static String BEACON_NUM="Beacon_num";
    public final static String VIN_NUM="vin_num";
    int beaconID, carID;
    private ImageView bcnVerify, vinVerify;
    TextWatcher textWatcher = null;
    TextWatcher textWatcher1 = null;
    boolean flag = false;
  //public  String vinString;
    boolean flag1 = false;
    public static String Macid;
    final int RequestCameraPermissionID = 1001;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        cameraPreview =  findViewById(R.id.camera_pre);

        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraPreview.getHolder());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        toolbarSetup();
        cameraPreview =  findViewById(R.id.camera_pre);
        Log.e("error",""+cameraPreview);
        beacon = findViewById(R.id.beacon_edittext);
        beacon.setText("");//test
        vin = findViewById(R.id.vin_edittext);
        vin.setText("");//test
        assign = findViewById(R.id.assign);
        bcnVerify = findViewById(R.id.bcn_verify);
        vinVerify = findViewById(R.id.vin_verify);
        scanInfo = findViewById(R.id.scan_info);


        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkBeaconApi();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        beacon.addTextChangedListener(textWatcher);

        textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkCarApi();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        vin.addTextChangedListener(textWatcher1);

        assign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((vin.getText().toString().isEmpty())&&(beacon.getText().toString().isEmpty())){
                    Toast.makeText(ScanQR.this, "VIN and Beacon cannot be empty", Toast.LENGTH_SHORT).show();
                }
                else if(beacon.getText().toString().isEmpty()){
                    Toast.makeText(ScanQR.this, "Please enter Beacon number", Toast.LENGTH_SHORT).show();
                }else if(vin.getText().toString().isEmpty()){
                    Toast.makeText(ScanQR.this, "Please enter VIN", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Thread t1 =  new Thread(new Runnable() {
                    @Override
                    public void run() {

                        Log.e("Assign thread","Upload to database");
                        if(flag1 && flag){
                            addApi();
                        }
                    }
                });
                    t1.start();

                     //Volley POST to HTTP


                }


            }
        });
        barcodeDetector = new BarcodeDetector.Builder(this)
              //  .setBarcodeFormats(Barcode.QR_CODE)
                .build();
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .build();
        //Add Event
        //alert();
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    //Request permission
                    ActivityCompat.requestPermissions(ScanQR.this,
                            new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
                if(qrcodes.size() != 0)
                {
                    beacon.post(new Runnable() {
                        @Override
                        public void run() {
                            cameraSource.stop( );
                            Log.e("QR ",""+qrcodes.valueAt(0).displayValue);

                            Log.e("Beacon ",""+beacon.getText().toString());

                            Log.e("VIN ",""+vin.getText().toString());

                            if(beacon.getText().toString().isEmpty()) {
                                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(100);
                                beacon.setText(qrcodes.valueAt(0).displayValue);
                                //vinAlert();
                                restartCamera();
                            }
                            else {
                                String s = qrcodes.valueAt(0).displayValue;
                                if(!(s.equalsIgnoreCase(beacon.getText().toString()))){
                                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(100);
                                vin.setText(s);
                                }
                                else{
                                 //   Toast.makeText(ScanQR.this, "Please scan vehicle barcode", Toast.LENGTH_SHORT).show();
                                    restartCamera();
                                }
                            }



                        }
                    });

                }

            }

        });
    }
    void toolbarSetup(){
        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    void alert(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ScanQR.this);
        builder.setTitle("Scan Beacon Barcode");
        //  builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Please scan the beacon barcode")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                              }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }
    void vinAlert(){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ScanQR.this);
        builder.setTitle("Scan Car Barcode");
        //  builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Please scan the car barcode")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        restartCamera();
                    }
                });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }


    @SuppressLint("MissingPermission")
    public void restartCamera()
    {
        Log.e("method","Restart Camera");
        try {
            cameraSource.start(cameraPreview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void checkBeaconApi() {
        scanInfo.setText("Please scan Vehicle barcode");
        String zone;
        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/becon/verifyBecon";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                        JSONArray document = obj.getJSONArray("Document");
                        Log.e("Response", "1");
                        flag = true;
                        bcnVerify.setImageDrawable(getDrawable(R.drawable.greentick));
                        bcnVerify.setVisibility(View.VISIBLE);
                        Log.e("Inside","checkBeaconAPI");
                        for (int i = 0; i < document.length(); i++) {
                            //getting the json object of the particular index inside the array
                            JSONObject Object = document.getJSONObject(i);
                            beaconID = Object.getInt("BeconID");
                            Log.e("BeaconID",""+beaconID);

                        }


                    } else if(obj.getInt("Code")==0) {
                        Log.e("Response","0");
                        flag = false;
                        bcnVerify.setVisibility(View.INVISIBLE);
                    //    Toast.makeText(ScanQR.this, "Beacon is not registered", Toast.LENGTH_SHORT).show();
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
                params.put("BeconPublicID", beacon.getText().toString());
                Log.d("BeconPublicID", beacon.getText().toString());

                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }
    private void checkCarApi() {
      if(!(beacon.getText().toString().isEmpty())&& (!(vin.getText().toString().isEmpty()))){
          scanInfo.setVisibility(View.INVISIBLE);
      }
        String zone;
        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/car/verifyCar";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                        JSONArray document = obj.getJSONArray("Document");
                        Log.e("Response", "1");
                        Log.e("Inside","checkCar");
                        flag1 =true;
                        vinVerify.setImageDrawable(getDrawable(R.drawable.greentick));
                        vinVerify.setVisibility(View.VISIBLE);
                        for (int i = 0; i < document.length(); i++) {
                            //getting the json object of the particular index inside the array
                            JSONObject Object = document.getJSONObject(i);
                            carID = Object.getInt("CarID");
                            Log.e("BeaconID",""+carID);

                        }


                    } else if(obj.getInt("Code")==0) {
                        flag1 =false;
                        Log.e("Response","0");
                        vinVerify.setVisibility(View.INVISIBLE);
                       // Toast.makeText(ScanQR.this, "Vehicle is not registered", Toast.LENGTH_SHORT).show();
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
                params.put("CarVIN", vin.getText().toString());
                Log.d("CarVIN", vin.getText().toString());

                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }
    private void addApi() {
        String zone;
        String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/becon_car_map/add";

        StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response Text", response);
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getInt("Code")==1) {
                        JSONArray document = obj.getJSONArray("Document");
                        for (int i = 0; i < document.length(); i++) {
                            //getting the json object of the particular index inside the array
                            JSONObject Object = document.getJSONObject(i);
                            Macid= Object.getString("BeconMacID");

                        }
                        SharedPreferences sharedPreferences = getSharedPreferences("Database", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(BEACON_NUM,beacon.getText().toString());
                        editor.putString(VIN_NUM,vin.getText().toString());
                        //vinString = vin.getText().toString();
                        editor.apply();
//                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ScanQR.this);
//                        builder.setTitle("Checkin Successful");
//                        //  builder.setIcon(R.mipmap.ic_launcher);
//                        builder.setMessage("Beacon No. "+sharedPreferences.getString(BEACON_NUM,"")+" is assigned to VIN "+sharedPreferences.getString(VIN_NUM,""))
//                                .setCancelable(false)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        startActivity(new Intent(ScanQR.this, Navigation_home.class));
//                                    }
//                                });
//                        android.app.AlertDialog alert = builder.create();
//                        alert.show();
                        new LovelyStandardDialog(ScanQR.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.green)
                                .setButtonsColorRes(R.color.green)
                                .setIcon(R.drawable.whitetick)
                                .setTitle("Assigned Successfully")
                                .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        startActivity(new Intent(ScanQR.this, Navigation_home.class));
                                    }
                                })
                                .setMessage("Beacon No. "+sharedPreferences.getString(BEACON_NUM,"")+" is assigned to VIN "+sharedPreferences.getString(VIN_NUM,""))
                                .show();

                    } else if(obj.getInt("Code")==0) {
                        flag1 =false;

                        Log.e("Response","0");
                        Toast.makeText(ScanQR.this, "Vehicle already assigned", Toast.LENGTH_SHORT).show();
                    }
                    else if(obj.getInt("Code")==2) {
                        flag1 =false;

                        Log.e("Response","0");
                        Toast.makeText(ScanQR.this, "Beacon already assigned", Toast.LENGTH_SHORT).show();
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
                params.put("CarID", ""+carID );
                params.put("BeconID", ""+beaconID);
               // params.put("MappingUpdatedBy", ""+11);
                params.put("MappingCreatedBy", ""+11);
               Log.e("CarID", ""+carID );
               Log.e("BeconID", ""+beaconID);

                return params;
            }
        };
        Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
    }

 
}