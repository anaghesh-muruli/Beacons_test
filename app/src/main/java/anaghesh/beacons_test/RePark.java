package anaghesh.beacons_test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RePark extends AppCompatActivity {

    EditText carVin;
    Button rePark;
    ImageView vinVerify;
    Boolean flag = false;
    private String beaconPublicId="";
    private int carID;
    public final static String BEACON="Beacon_num";
    public final static String VIN="vin_num";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_park);

        //toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //decleration of variables
        carVin = (EditText)findViewById(R.id.carVin);
        rePark = (Button)findViewById(R.id.rePark);
        vinVerify = (ImageView)findViewById(R.id.vinVerify);


        //text watcher for car vin
        TextWatcher vin= new TextWatcher() {
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
        carVin.addTextChangedListener(vin);

        rePark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(carVin.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(),"Please enter the Vehicle Identidication Number!",Toast.LENGTH_SHORT).show();
                else if(flag==false)
                    Toast.makeText(getApplicationContext(),"Vehicle Identidication Number doesnt exist!",Toast.LENGTH_SHORT).show();
                else {
                    findBeaconApi();
                    //shared preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("DatabaseNew", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(BEACON,beaconPublicId);
                    editor.putString(VIN,carVin.getText().toString());
                    editor.apply();
                    Log.e("Re-park BPID = ",BEACON);
                    Log.e("Re-park Car-VIN = ",VIN);
                    Log.e("Re-park:",sharedPreferences.getString(VIN, ""));
                    Log.e("Re-park:",sharedPreferences.getString(BEACON, ""));
                    startActivity(new Intent(getApplicationContext(), Parking.class).putExtra("From", "RePark"));
                }
                }
        });

    }
        //to check if the car is present in the database
        private void checkCarApi() {
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
                            flag =true;
                            vinVerify.setImageDrawable(getDrawable(R.drawable.greentick));
                            vinVerify.setVisibility(View.VISIBLE);
                            for (int i = 0; i < document.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject Object = document.getJSONObject(i);
                                carID = Object.getInt("CarID");
                                Log.e("CarID",""+carID);

                            }


                        } else if(obj.getInt("Code")==0) {
                            flag =false;
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
                    params.put("CarVIN", carVin.getText().toString());
                    Log.d("CarVIN", carVin.getText().toString());

                    return params;
                }
            };
            Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
        }

        //to find the beacon associated with the particular carVIN
        private void findBeaconApi(){
            String Url = "http://ec2-18-216-80-229.us-east-2.compute.amazonaws.com:3000/car/findCarWithCarVIN";

            StringRequest rq = new StringRequest(Request.Method.POST, Url , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("Response Text", response);
                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getInt("Code")==1) {
                            JSONArray document = obj.getJSONArray("Document");
                            Log.e("Response", "1");
                            Log.e("Inside","findBeacon");;
                            for (int i = 0; i < document.length(); i++) {
                                //getting the json object of the particular index inside the array
                                JSONObject Object = document.getJSONObject(i);
                                beaconPublicId = Object.getString("BeconPublicID");
                                Log.e("beaconPublicId",""+beaconPublicId);
                                Toast.makeText(RePark.this, "Vehicle is to assigned"+beaconPublicId, Toast.LENGTH_SHORT).show();


                            }


                        } else if(obj.getInt("Code")==0) {
                            Log.e("Response","0");
                           // Toast.makeText(RePark.this, "Vehicle is not assigned", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Vehicle is not assigned to a Beacon!", Toast.LENGTH_LONG).show();
                }

            }) {

                @Override
                protected Map<String, String> getParams() {
                    Log.e("Inside","getParams");

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("CarVIN", carVin.getText().toString());
                    Log.d("CarVIN", carVin.getText().toString());

                    return params;
                }
            };
            Singleton.getInstance(getApplicationContext()).addToRequestQueue(rq);
        }

}
